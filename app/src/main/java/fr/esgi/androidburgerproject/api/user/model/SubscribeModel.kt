package fr.esgi.androidburgerproject.api.user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SubscribeModel(
    @Expose
    @SerializedName("email")
    val email: String,
    @Expose
    @SerializedName("login")
    val login: String,
    @Expose
    @SerializedName("password")
    val password: String
)