package com.example.economizzemob

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private var senhaVisivel = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        findViewById<TextView>(R.id.btnEsqueceuSenha).setOnClickListener {
            startActivity(Intent(this, EsqueciSenhaActivity::class.java))
        }

        val email = findViewById<EditText>(R.id.inputEmailLogin)
        val senha = findViewById<EditText>(R.id.inputSenhaLogin)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)
        val btnIrCadastro = findViewById<TextView>(R.id.btnIrCadastro)

        // OLHINHO DA SENHA
        senha.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= senha.right - senha.compoundDrawables[2].bounds.width()) {
                    senhaVisivel = !senhaVisivel
                    if (senhaVisivel) {
                        senha.inputType = android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    } else {
                        senha.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                    }
                    senha.setSelection(senha.text.length)
                    return@setOnTouchListener true
                }
            }
            false
        }

        btnEntrar.setOnClickListener {
            if (email.text.toString().isEmpty() || senha.text.toString().isEmpty()) {
                Toast.makeText(this, "Preencha tudo", Toast.LENGTH_SHORT).show()
            } else {
                // LEVA PRA TELA PRINCIPAL
                Toast.makeText(this, "Logado!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java) // se sua tela principal chama HomeActivity, troca aqui
                startActivity(intent)
                finish()
            }
        }

        btnIrCadastro.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }
    }
}