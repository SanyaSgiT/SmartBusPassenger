package com.example.smartbuspassenger.ui.account

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.smartbuspassenger.MapActivity
import com.example.smartbuspassenger.R
import com.example.smartbuspassenger.data.api.UserApi
import com.example.smartbuspassenger.data.models.CreateUserRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class RegisterActivity : AppCompatActivity() {
    private lateinit var btnLogin: TextView
    private lateinit var btnReg: Button
    private lateinit var login: TextView
    private lateinit var name: TextView
    private lateinit var password: TextView

    private val userApi: UserApi by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        btnLogin = findViewById(R.id.logInText)
        btnReg = findViewById(R.id.registerButton)

        login = findViewById(R.id.login)
        name = findViewById(R.id.name)
        password = findViewById(R.id.password)

        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnReg.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val user = userApi.createUser(
                    CreateUserRequest(
                        login.text.toString(),
                        name.text.toString(),
                        password.text.toString()
                    )
                )
            }
            startActivity(Intent(this, MapActivity::class.java))
        }
    }
}