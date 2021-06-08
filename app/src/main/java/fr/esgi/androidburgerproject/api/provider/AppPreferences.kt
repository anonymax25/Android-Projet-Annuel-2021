package fr.esgi.androidburgerproject.api.provider

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import fr.esgi.androidburgerproject.api.user.model.UserModel
import com.google.gson.reflect.TypeToken
object AppPreferences {
    private const val NAME = "AndroidBurgerProject"
    private const val MODE = Context.MODE_PRIVATE
    private val gsonConverter: Gson = Gson()
    private lateinit var preferences: SharedPreferences

    //SharedPreferences variables
    private val IS_LOGIN = Pair("is_login", false)
    private val _ID = Pair("_id", "")
    private val TOKEN = Pair("token", "")
    private val ADDRESS = Pair("address", "");
    private val USER = Pair("user", "")
    private val PRODUCTS = Pair("products", "")
    private val MENUS = Pair("menus", "")

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    //an inline function to put variable and save it
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    //SharedPreferences variables getters/setters
    var isLogin: Boolean
        get() = preferences.getBoolean(IS_LOGIN.first, IS_LOGIN.second)
        set(value) = preferences.edit {
            it.putBoolean(IS_LOGIN.first, value)
        }
    var _id: String
        get() {
            // strips off all non-ASCII characters
            var text = preferences.getString(_ID.first, _ID.second) ?: ""
            text = text.replace("[^\\x00-\\x7F]".toRegex(), "")

            // erases all the ASCII control characters
            text = text.replace("[\\p{Cntrl}&&[^\r\n\t]]".toRegex(), "")
            text = text.replace("[\"+]".toRegex(), replacement = "")
            // removes non-printable characters from Unicode
            text = text.replace("\\p{C}".toRegex(), "")
            return text.trim()

        }
        set(value) = preferences.edit {
            it.putString(_ID.first, value)
        }
    var token: String
        get() = preferences.getString(TOKEN.first, TOKEN.second) ?: ""
        set(value) = preferences.edit {
            it.putString(TOKEN.first, value)
        }


    var user: UserModel?
        get() {
            val json: String? = preferences.getString(USER.first, USER.second)
            return Gson().fromJson(json, UserModel::class.java)
        }
        set(user) {
            if (user !== null) {
                val currentUser: String = Gson().toJson(user)
                preferences.edit {
                    it.putString(USER.first, currentUser)
                }
            }
        }

    fun clear(key: String) {
        this.preferences.edit().remove(key).apply()
    }
}