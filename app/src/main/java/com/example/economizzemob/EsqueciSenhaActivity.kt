package com.example.economizzemob

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class EsqueciSenhaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_esqueceu_senha)

        val inputEmail = findViewById<EditText>(R.id.inputEmailRecuperar)
        val btnEnviar = findViewById<AppCompatButton>(R.id.btnEnviarLink)
        val btnVoltar = findViewById<TextView>(R.id.btnVoltarLoginRecuperar)

        btnEnviar.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            if (email.isEmpty() || !email.contains("@")) {
                inputEmail.error = "Digite um e-mail válido"
                return@setOnClickListener
            }
            // Aqui você pode integrar com Firebase depois
            // Por enquanto só simula envio
            Toast.makeText(this, "Link enviado para $email", Toast.LENGTH_LONG).show()
            finish()
        }

        btnVoltar.setOnClickListener { finish() }
    }
}