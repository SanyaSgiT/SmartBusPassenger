package com.example.smartbuspassenger.data.storage

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.smartbuspassenger.domain.User
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UserStorage(private val sharedPreferences: SharedPreferences) {
    var user: User?
        set(value) {
            sharedPreferences.edit {
                if (value == null) {
                    remove(::user.name)
                } else {
                    putString(::user.name, Json.encodeToString(value))
                }
            }
        }
        get() = sharedPreferences.getString(::user.name, null)?.let {
            Json.decodeFromString(User.serializer(), it)
        }

    var token: String?
        set(value) {
            sharedPreferences.edit {
                if (value == null) {
                    remove(::token.name)
                } else {
                    putString(::token.name, value)
                }
            }
        }
        get() = sharedPreferences.getString(::token.name, null)

    val hasUser: Boolean
        get() = token != null && user != null
}