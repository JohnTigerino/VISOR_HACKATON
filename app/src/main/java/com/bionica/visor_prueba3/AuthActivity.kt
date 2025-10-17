package com.bionica.visor_prueba3

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.auth.auth        //no
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AlertDialog


class AuthActivity : AppCompatActivity() {


    //agregado 12/10/2025 19:09
    private lateinit var auth: FirebaseAuth

    //==========================================================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Firebase Auth
        auth = Firebase.auth

        //================================================================================================
        setContentView(R.layout.activity_auth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnIngresar = findViewById<Button>(R.id.btn_ingresar)
        btnIngresar.setOnClickListener {
            val dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_singin, null, false)

            val etEmail = dialogView.findViewById<EditText>(R.id.etEmail)
            val etPass = dialogView.findViewById<EditText>(R.id.etPassword)

            val dialog = MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setTitle("Iniciar sesión")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Entrar", null) // importante: null aquí
                .create()

            dialog.setOnShowListener {
                val btnEntrar = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                btnEntrar.setOnClickListener {
                    val email = etEmail.text.toString().trim()
                    val pass = etPass.text.toString()

                    var isValid = true
                    etEmail.error = null
                    etPass.error = null

                    // Validar campos vacíos
                    if (email.isEmpty()) {
                        etEmail.error = "El correo es obligatorio"
                        isValid = false
                    }
                    if (pass.isEmpty()) {
                        etPass.error = "La contraseña es obligatoria"
                        isValid = false
                    }

                    // Validar formato de correo
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        etEmail.error = "Correo inválido"
                        isValid = false
                    }

                    // Validar longitud mínima de contraseña
                    if (pass.length < 6) {
                        etPass.error = "Mínimo 6 caracteres"
                        isValid = false
                    }

                    if (!isValid) return@setOnClickListener

                    // 👉 Llamada a Firebase
                    auth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Autenticación exitosa.", Toast.LENGTH_SHORT)
                                    .show()
                                dialog.dismiss() // cerrar solo en éxito
                                startActivity(Intent(this, HomeActivity::class.java))
                            } else {
                                val exception = task.exception
                                val mensajeError = when (exception) {
                                    is FirebaseAuthInvalidUserException -> {
                                        etEmail.error = "El correo no está registrado"
                                        "El correo electrónico no está registrado."
                                    }

                                    is FirebaseAuthInvalidCredentialsException -> {
                                        etPass.error = "Contraseña incorrecta"
                                        "La contraseña es incorrecta."
                                    }

                                    else -> "Fallo en la autenticación: ${exception?.message}"
                                }
                                Toast.makeText(this, mensajeError, Toast.LENGTH_LONG).show()
                                // No cerramos el diálogo aquí
                            }
                        }
                }
            }

            dialog.show()
        }

        val btnCrearCuenta = findViewById<Button>(R.id.btn_Crear_Cuenta)
        btnCrearCuenta.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

// Evitar cambio de orientación
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        /**private fun iniciarSesionConFirebase(email: String, password: String) {  //pass password
        auth.signInWithEmailAndPassword(email, password)           //pass password
        .addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
        // Inicio de sesión exitoso
        val user = auth.currentUser
        Toast.makeText(baseContext, "Autenticación exitosa.", Toast.LENGTH_SHORT).show()
        // ir a otra actividad
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        // finish()
        } else {
        // Si el inicio de sesión falla, muestra un mensaje al usuario.
        val exception = task.exception
        val mensajeError = when (exception) {
        is FirebaseAuthInvalidUserException -> "El correo electrónico no está registrado."
        is FirebaseAuthInvalidCredentialsException -> "La contraseña es incorrecta."
        else -> "Fallo en la autenticación: ${exception?.message}"
        }
        Toast.makeText(baseContext, mensajeError, Toast.LENGTH_LONG).show()
        //==================================================================================
        }
        }
        }***/
    }
}