package fr.esgi.androidburgerproject.api.user.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import fr.esgi.androidburgerproject.api.cacheManager.api.data.Cacheable
import fr.esgi.androidburgerproject.api.user.model.LoginModel
import fr.esgi.androidburgerproject.api.user.model.SubscribeModel
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface UserInterface {
    @Headers("Content-Type: application/json")
    @POST("authentication/login")
    fun login(@Body loginData: LoginModel): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("authentication/login")
    fun register(@Body data: SubscribeModel): Call<JsonObject>

    @Cacheable
    @Headers("Content-Type: application/json")
    @GET("user")
    fun getUser(@Header("Authorization") token: String): Call<JsonObject>
}
