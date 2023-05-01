package com.example.smartbuspassenger.ui.account

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.smartbuspassenger.MapActivity
import com.example.smartbuspassenger.R
import com.example.smartbuspassenger.data.service.LoginRequest
import com.example.smartbuspassenger.di.interceptor
import com.example.smartbuspassenger.di.userApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.logging.HttpLoggingInterceptor

class LoginActivity : AppCompatActivity() {
    private lateinit var btnToRegister: TextView
    private lateinit var btnLogin: Button
    private lateinit var login: TextView
    private lateinit var password: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        btnToRegister = findViewById(R.id.regText)
        btnLogin = findViewById(R.id.logInButton)

        login = findViewById(R.id.login)
        password = findViewById(R.id.password)

        interceptor.level = HttpLoggingInterceptor.Level.BODY

        btnToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val user = userApi.authentication(
                    LoginRequest(
                        login.text.toString(),
                        password.text.toString()
                    )
                )
            }
            startActivity(Intent(this, MapActivity::class.java))
        }
    }
}