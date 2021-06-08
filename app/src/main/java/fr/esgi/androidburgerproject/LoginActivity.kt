package fr.esgi.androidburgerproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.JsonObject
import fr.esgi.androidburgerproject.api.provider.AppPreferences
import fr.esgi.androidburgerproject.api.user.model.LoginModel
import fr.esgi.androidburgerproject.api.user.model.UserModel
import fr.esgi.androidburgerproject.api.user.service.LoginService
import fr.esgi.androidburgerproject.api.user.service.UserService

class LoginActivity : AppCompatActivity() {
    companion object {
        const val SIGN_IN_RESULT_CODE = 1001
        lateinit var signInClient: GoogleSignInClient
        lateinit var signInOptions: GoogleSignInOptions
        private lateinit var auth: FirebaseAuth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppPreferences.init(this)

        auth = FirebaseAuth.getInstance()
        setupGoogleLogin()
        if (AppPreferences.isLogin || FirebaseAuth.getInstance().currentUser !== null) {
            this.startActivity(Intent(this, MainActivity::class.java))
        }
        setContentView(R.layout.connection_activity)

        val emailInput = findViewById<EditText>(R.id.outlined_email)
        val passwordInput = findViewById<EditText>(R.id.outlined_password)
        val submitButton = findViewById<Button>(R.id.login_submit)
        val skipButton = findViewById<Button>(R.id.skip_button)

        submitButton.setOnClickListener {
            regularLogin(emailInput, passwordInput)
        }

        skipButton.setOnClickListener {
            skipLogin()
        }
    }

    private fun googleLogin() {
        val loginIntent: Intent = signInClient.signInIntent
        startActivityForResult(loginIntent, SIGN_IN_RESULT_CODE)
    }

    /**
     * Regular login with email + password with api and with caching
     */
    private fun regularLogin(emailInput: EditText, passwordInput: EditText) {
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty() && email.isNotBlank()
            && password.isNotBlank()) {

            val loginService = LoginService(this.applicationContext)
            val userService = UserService(this.applicationContext)

            var response: JsonObject?

            loginService.loginUser(LoginModel(email, password)) { loginResponse ->

                response = loginResponse

                Log.d("error", response.toString());

                if (response !== null) {
                    AppPreferences.isLogin = true
                    AppPreferences.token = response?.get("token").toString()
                    AppPreferences._id = response?.get("uid").toString()
                    userService.getUser { res ->
                        if (res !== null) {
                            Log.d("test", res.toString())
                            AppPreferences.user = UserModel(
                                res.get("id").toString(),
                                res.get("name").toString(),
                                res.get("email").toString(),
                            )
                            Log.d("test", AppPreferences.user.toString())
                        }
                    }
                    this.startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Login Failed ! ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun skipLogin() {
        this.startActivity(Intent(this, SubscribeActivity::class.java))
    }

    /**
     * Setup google client
     */
    private fun setupGoogleLogin() {
        signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_client_id))
            .requestEmail()
            .build()
        signInClient = GoogleSignIn.getClient(this, signInOptions)
    }

    /**
     * Handle Google sign in response
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    googleFirebaseAuth(account)
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Real login from google authentification
     */
    private fun googleFirebaseAuth(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                this.startActivity(Intent(this, MainActivity::class.java))
            } else {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }
}