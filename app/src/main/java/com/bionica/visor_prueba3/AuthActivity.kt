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

        val btn_ingresar = findViewById<Button>(R.id.btn_ingresar)
        btn_ingresar.setOnClickListener {
            val dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_singin, null, false)

            val etEmail = dialogView.findViewById<EditText>(R.id.etEmail)
            val etPass = dialogView.findViewById<EditText>(R.id.etPassword)

            val dialog = MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setTitle("Iniciar sesión")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Entrar") { _, _ ->
                    val email = etEmail.text.toString().trim()
                    val pass = etPass.text.toString()
                    //Toast.makeText(this, "email=$email", Toast.LENGTH_SHORT).show()

                    //logica firebase inicio de sesion

                    if (email.isNotEmpty() && pass.isNotEmpty()) {
                        // Aquí llamamos a la función de Firebase
                        iniciarSesionConFirebase(email, pass)
                    } else {
                        Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
                        //======
                          //cambiar
                    }
                    //=====

                }
                .create()

            dialog.show()
                //btn movido

        }
        val btnCrearCuenta = findViewById<Button>(R.id.btn_Crear_Cuenta)
        btnCrearCuenta.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        } 
        //====================================Evitar cambio de orientacion=============================================
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    //======================================================================================
    private fun iniciarSesionConFirebase(email: String, password: String) {  //pass password
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
                    //val dialogView = LayoutInflater.from(this)
                    //    .inflate(R.layout.dialog_singin, null, false)

                   // val etEmail = dialogView.findViewById<EditText>(R.id.etEmail)
                   // val etPass = dialogView.findViewById<EditText>(R.id.etPassword)

                    //val dialog = MaterialAlertDialogBuilder(this)
                       // .setView(dialogView)
                       // .setTitle("Iniciar sesión")
                       // .setNegativeButton("Cancelar", null)
                        //.setPositiveButton("Entrar") { _, _ ->
                         //   val email = etEmail.text.toString().trim()
                        //    val pass = etPass.text.toString()
                            //Toast.makeText(this, "email=$email", Toast.LENGTH_SHORT).show()

                            //logica firebase inicio de sesion
                      //  }
                      //  .create()

                    //dialog.show()

                }
            }
    }
}