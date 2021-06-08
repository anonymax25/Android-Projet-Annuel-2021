package fr.esgi.androidburgerproject.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import fr.esgi.androidburgerproject.LoginActivity
import fr.esgi.androidburgerproject.R
import fr.esgi.androidburgerproject.api.provider.AppPreferences
import fr.esgi.androidburgerproject.api.user.model.UserModel
import fr.esgi.androidburgerproject.api.user.service.LoginService
import fr.esgi.androidburgerproject.api.user.service.UserService

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_activity)

        val userService = UserService(this.applicationContext)
        val loginService = LoginService(this.applicationContext)

        val backButton = findViewById<Button>(R.id.back_button)
        val email = findViewById<TextView>(R.id.email)
        val name = findViewById<TextView>(R.id.name)
        val logout = findViewById<Button>(R.id.logout_button)
        if (AppPreferences.user === null) {
            userService.getUser { response ->
                if (response !== null) {
                    AppPreferences.user = UserModel(
                        response.get("id").toString(),
                        response.get("name").toString(),
                        response.get("email").toString(),
                    )

                    email?.text = response.get("email").toString()
                    name?.text = response.get("login").toString()
                }
            }
        }else{
            email?.text = AppPreferences.user?.email;
            name?.text = AppPreferences.user?.name;
        }

        logout?.setOnClickListener {
            // loginService.deleteToken()
            AppPreferences.clear("token")
            AppPreferences.clear("id")
            AppPreferences.clear("user")
            AppPreferences.clear("is_login")
            AppPreferences.clear("email")
            startActivity(Intent(this, LoginActivity::class.java))
        }


        backButton?.setOnClickListener {
            finish();
        }
    }
}