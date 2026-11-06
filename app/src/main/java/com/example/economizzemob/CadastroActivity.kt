package com.example.economizzemob

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class CadastroActivity : AppCompatActivity() {

    private var senhaVisivel = false
    private var confirmaVisivel = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        val inputNome = findViewById<EditText>(R.id.inputNome)
        val inputEmail = findViewById<EditText>(R.id.inputEmailCadastro)
        val inputSenha = findViewById<EditText>(R.id.inputSenhaLogin)
        val inputConfirma = findViewById<EditText>(R.id.inputConfirmarSenha)
        val btnCriar = findViewById<Button>(R.id.btnCriarConta)
        val txtRequisitos = findViewById<TextView>(R.id.txtRequisitos)
        val btnVoltar = findViewById<TextView>(R.id.btnVoltarLogin)

        // OLHINHO SENHA
        inputSenha.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (inputSenha.compoundDrawables[2] != null && event.rawX >= inputSenha.right - inputSenha.compoundDrawables[2].bounds.width()) {
                    senhaVisivel = !senhaVisivel
                    toggleSenha(inputSenha, senhaVisivel)
                    return@setOnTouchListener true
                }
            }
            false
        }

        // OLHINHO CONFIRMAR SENHA - adiciona o olhinho no XML também
        inputConfirma.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (inputConfirma.compoundDrawables[2] != null && event.rawX >= inputConfirma.right - inputConfirma.compoundDrawables[2].bounds.width()) {
                    confirmaVisivel = !confirmaVisivel
                    toggleSenha(inputConfirma, confirmaVisivel)
                    return@setOnTouchListener true
                }
            }
            false
        }

        // VALIDAÇÃO VERDE
        inputSenha.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val senha = s.toString()
                val tem8 = senha.length >= 8
                val temMaiuscula = senha.any { it.isUpperCase() }
                val temMinuscula = senha.any { it.isLowerCase() }
                val temNumero = senha.any { it.isDigit() }
                val temEspecial = senha.any { "!@#$%^&*".contains(it) }

                val texto = "🔒 Requisitos da senha:\n" +
                        "${if (tem8) "✅" else "❌"} Mínimo 8 caracteres\n" +
                        "${if (temMaiuscula) "✅" else "❌"} Pelo menos 1 letra maiúscula\n" +
                        "${if (temMinuscula) "✅" else "❌"} Pelo menos 1 letra minúscula\n" +
                        "${if (temNumero) "✅" else "❌"} Pelo menos 1 número\n" +
                        "${if (temEspecial) "✅" else "❌"} Pelo menos 1 caractere especial (!@#$%^&*)"

                txtRequisitos.text = texto

                if (tem8 && temMaiuscula && temMinuscula && temNumero && temEspecial) {
                    txtRequisitos.setTextColor(ContextCompat.getColor(this@CadastroActivity, android.R.color.holo_green_dark))
                } else {
                    txtRequisitos.setTextColor(ContextCompat.getColor(this@CadastroActivity, android.R.color.black))
                }
            }
        })

        btnCriar.setOnClickListener {
            if (inputNome.text.toString().isEmpty() || inputEmail.text.toString().isEmpty() || inputSenha.text.toString().isEmpty()) {
                Toast.makeText(this, "Preencha tudo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (inputSenha.text.toString() != inputConfirma.text.toString()) {
                Toast.makeText(this, "Senhas não conferem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Conta criada!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnVoltar.setOnClickListener {
            finish()
        }
    }

    private fun toggleSenha(editText: EditText, visivel: Boolean) {
        if (visivel) {
            editText.inputType = android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            editText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        editText.setSelection(editText.text.length)
    }
}