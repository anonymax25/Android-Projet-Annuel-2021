package fr.esgi.androidburgerproject.api.user.service

import android.content.Context
import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import fr.esgi.androidburgerproject.api.cacheManager.api.data.Cacheable
import fr.esgi.androidburgerproject.api.provider.ApiClient
import fr.esgi.androidburgerproject.api.provider.AppPreferences
import fr.esgi.androidburgerproject.api.user.api.UserInterface
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserService(private val context: Context?) {
    private val token: String
        get() = AppPreferences.token
    @Cacheable
    fun getUser(onResult: (JsonObject?) -> Unit) {
        val call: Call<JsonObject> = ApiClient.buildService(
            UserInterface::class.java,
            this.context!!
        )
            .getUser("Bearer ${token.substring(1, token.length - 1)}")
        call.enqueue(object : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>?, response: Response<JsonObject>?) {
                Log.d("User", "Success ${response?.body().toString()}")
                onResult(response?.body())
            }

            override fun onFailure(call: Call<JsonObject>?, t: Throwable?) {
                Log.d("User", "Failure ${call?.request()}")
                onResult(null)
            }
        })
    }
}