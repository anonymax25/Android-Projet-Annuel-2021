package fr.esgi.androidburgerproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import fr.esgi.androidburgerproject.api.provider.AppPreferences
import fr.esgi.androidburgerproject.profile.ProfileActivity

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.auth = FirebaseAuth.getInstance()
        AppPreferences.init(this)

        val profileButton = findViewById<Button>(R.id.profile_button)

        profileButton.setOnClickListener {
            if (AppPreferences.isLogin || this.auth.currentUser !== null) {
                startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
            } else {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }
        }
    }
}