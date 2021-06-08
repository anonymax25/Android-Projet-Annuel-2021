package fr.esgi.androidburgerproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import fr.esgi.androidburgerproject.api.provider.AppPreferences
import fr.esgi.androidburgerproject.api.user.model.LoginModel
import fr.esgi.androidburgerproject.api.user.model.SubscribeModel
import fr.esgi.androidburgerproject.api.user.model.UserModel
import fr.esgi.androidburgerproject.api.user.service.LoginService
import fr.esgi.androidburgerproject.api.user.service.UserService

class SubscribeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.subscribe_activity)

        val emailInput = findViewById<EditText>(R.id.outlined_email)
        val usernameInput = findViewById<EditText>(R.id.outlined_email)
        val passwordInput = findViewById<EditText>(R.id.outlined_password)
        val submitButton = findViewById<Button>(R.id.login_submit)


        submitButton.setOnClickListener {
            subscribe(emailInput, usernameInput, passwordInput)
        }
    }

    private fun subscribe(emailInput: EditText, usernameInput: EditText, passwordInput: EditText) {
        val email = emailInput.text.toString()
        val username = usernameInput.text.toString()
        val password = passwordInput.text.toString()

        if (email.isNotEmpty() && email.isNotBlank() && username.isNotEmpty()
            && username.isNotBlank() && password.isNotEmpty() && password.isNotBlank()) {
            val loginService = LoginService(this.applicationContext)
            val userService = UserService(this.applicationContext)
            var session: JsonObject?

            loginService.subscribe(SubscribeModel(email,username,password)) { response ->
                if(response !== null) {
                    loginService.loginUser(LoginModel(username, password)) { loginResponse ->
                        session = loginResponse
                        if (session !== null) {
                            AppPreferences.isLogin = true
                            AppPreferences.token = session?.get("token").toString()
                            AppPreferences._id = session?.get("uid").toString()
                            userService.getUser { res ->
                                if (res !== null) {
                                    AppPreferences.user = UserModel(
                                        res.get("id").toString(),
                                        res.get("name").toString(),
                                        res.get("email").toString(),
                                    )
                                }
                            }
                            this.startActivity(Intent(this, MainActivity::class.java))
                        }
                    }
                }
            }
        }
    }
}