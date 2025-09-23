package com.bionica.visor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bionica.visor_prueba3.HomeActivity
import com.bionica.visor_prueba3.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    companion object {
        private const val TAG = "RegisterActivity"
        private const val MIN_PASSWORD = 6
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val etEmail = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val etPassword = findViewById<EditText>(R.id.editTxtPassword)
        val etName = findViewById<EditText>(R.id.editTxtName)   // si no tienes este campo, quita la lógica
        val btnIngresar = findViewById<Button>(R.id.btn_ingresar_reg)

        btnIngresar.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass  = etPassword.text.toString()
            val pass2 = etName.text?.toString() ?: pass  // si no hay confirm, iguala

            // Validaciones rápidas
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Correo inválido"; etEmail.requestFocus(); return@setOnClickListener
            }
            if (pass.length < MIN_PASSWORD) {
                etPassword.error = "Mínimo $MIN_PASSWORD caracteres"; etPassword.requestFocus(); return@setOnClickListener
            }

            crearUsuario(email, pass)
        }
    }

    private fun crearUsuario(email: String, password: String) {
        // === Este es tu snippet de Firebase, integrado ===
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)    // ve a Home o muestra mensaje
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, task.exception?.localizedMessage ?: "Error al crear la cuenta", Toast.LENGTH_LONG).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // ejemplo: ir a Home y cerrar registro
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else {
            // te quedas en la misma pantalla; podrías mostrar estados/errores
        }
    }
}
