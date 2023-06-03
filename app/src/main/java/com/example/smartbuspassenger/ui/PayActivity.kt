package com.example.smartbuspassenger.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.smartbuspassenger.MapActivity
import com.example.smartbuspassenger.R

class PayActivity : AppCompatActivity() {
    private lateinit var pay: Button
    private lateinit var unpay: Button
    private lateinit var textRoute: TextView

    private val route: String by lazy {
        intent.extras?.getString("Route", "-10").let {
            if (it == null || it == "") error("Route must be supplied via extras")
            else it
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)

        pay = findViewById(R.id.payButton)
        unpay = findViewById(R.id.unpayButton)
        textRoute = findViewById(R.id.description)

        textRoute.text = route

        pay.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
            setupDialog()
        }

        unpay.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }
    }

    private fun setupDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Уведомление об оплате")
        builder.setMessage("Платеж успешно отправлен")

        builder.setPositiveButton("Отлично") { dialog, which ->
            Toast.makeText(
                applicationContext,
                "Отлично", Toast.LENGTH_SHORT
            ).show()
        }
        builder.show()
    }
}