package com.example.smartbuspassenger.ui.account

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartbuspassenger.MapActivity
import com.example.smartbuspassenger.R
import com.example.smartbuspassenger.data.api.UserApi
import com.example.smartbuspassenger.data.models.LoginRequest
import com.example.smartbuspassenger.data.storage.UserStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class LoginActivity : AppCompatActivity() {
    private lateinit var btnToRegister: TextView
    private lateinit var btnLogin: Button
    private lateinit var login: TextView
    private lateinit var password: TextView

    private val userApi: UserApi by inject()
    private val userStorage: UserStorage by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnToRegister = findViewById(R.id.regText)
        btnLogin = findViewById(R.id.logInButton)

        login = findViewById(R.id.login)
        password = findViewById(R.id.password)

        btnToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val user = userApi.authentication(
                        LoginRequest(
                            login.text.toString(),
                            password.text.toString()
                        )
                    )

                    userStorage.token = user.token.token
                    userStorage.user = user.user

                    println(user)

                    withContext(Dispatchers.Main) {
                        startActivity(Intent(this@LoginActivity, MapActivity::class.java))
                    }
                } catch (_: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Нет такого пользователя", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}