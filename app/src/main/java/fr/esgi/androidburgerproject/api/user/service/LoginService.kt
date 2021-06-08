package fr.esgi.androidburgerproject.api.user.service

import android.content.Context
import android.util.Log
import com.google.gson.JsonObject
import fr.esgi.androidburgerproject.api.provider.ApiClient
import fr.esgi.androidburgerproject.api.provider.AppPreferences
import fr.esgi.androidburgerproject.api.user.api.UserInterface
import fr.esgi.androidburgerproject.api.user.model.LoginModel
import fr.esgi.androidburgerproject.api.user.model.SubscribeModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginService(private val context: Context) {
    private val token: String
        get() = AppPreferences.token

    fun subscribe(userData: SubscribeModel, onResult: (JsonObject?) -> Unit) {
        val call: Call<JsonObject> =
            ApiClient.buildService(UserInterface::class.java, this.context).register(userData)
        call.enqueue(object : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>?, response: Response<JsonObject>?) {
                Log.d("Subscribe", "Success")
                onResult(response?.body())
            }

            override fun onFailure(call: Call<JsonObject>?, t: Throwable?) {
                Log.d("Subscribe", "Failure ${call?.request().toString()}")
                onResult(null)
            }
        })
    }

    fun loginUser(loginData: LoginModel, onResult: (JsonObject?) -> Unit) {
        val call: Call<JsonObject> = ApiClient.buildService(UserInterface::class.java, this.context)
            .login(loginData)
        call.enqueue(object : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>?, response: Response<JsonObject>?) {
                Log.d("Login", "Success ${response?.body().toString()}")
                Log.d("Login", "Success ${response.toString()}")
                onResult(response?.body())
            }

            override fun onFailure(call: Call<JsonObject>?, t: Throwable?) {
                Log.d("Burger", "Failure ${call?.request()}")
                onResult(null)
            }
        })
    }

    fun deleteToken() {
        /*
        val call: Call<Void> = ApiClient.buildService(UserInterface::class.java, this.context)
            .logout(token.substring(1, token.length - 1))
        call.enqueue(object : Callback<Void> {

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                Log.d("Logout", "Failure ${call?.request()}")
            }

            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                Log.d("Logout", "Success")
                response?.body()
            }
        })
        */
    }
}