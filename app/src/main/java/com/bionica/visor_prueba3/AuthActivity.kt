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


class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                    // aquí haces tu lógica de login (Firebase Auth, etc.)
                    Toast.makeText(this, "email=$email", Toast.LENGTH_SHORT).show()
                }
                .create()

            dialog.show()

            val btnCrearCuenta = findViewById<Button>(R.id.btn_Crear_Cuenta)
            btnCrearCuenta.setOnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        }
    }
}