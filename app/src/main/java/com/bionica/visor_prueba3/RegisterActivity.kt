package com.bionica.visor_prueba3
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val spinner: Spinner = findViewById(R.id.spinnerRoles)
        val opciones = listOf("Productores", "Estudiantes", "Investigadores", "Técnicos")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            opciones
        )
        val btnIngresar = findViewById<Button>(R.id.btn_ingresar_reg)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        btnIngresar.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }
}