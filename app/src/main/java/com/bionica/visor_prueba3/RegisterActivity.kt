package com.bionica.visor_prueba3
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException



class RegisterActivity : AppCompatActivity() {

    //declarar la instancia de firebase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 2. Inicializar la instancia en onCreate()
        auth = Firebase.auth

        val spinner: Spinner = findViewById(R.id.spinnerRoles)
        val opciones = listOf("Productor", "Estudiante", "Investigador", "Técnico")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            opciones
        )
        val btnIngresar = findViewById<Button>(R.id.btn_ingresar_reg)
        val editTextEmailAddress = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val editTxtPassword = findViewById<EditText>(R.id.editTxtPassword)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        btnIngresar.setOnClickListener {
            //startActivity(Intent(this, HomeActivity::class.java))

            val email = editTextEmailAddress.text.toString().trim()
            val password = editTxtPassword.text.toString().trim()
            //parte del codigo para registro segun video de mouradev
            if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (editTxtPassword.length() < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //llamar a la funcion
            registrarUsuarioConFirebase(email, password);
        }



        //version de la pagina de firebase autenticacion con correo

    }
    //=======================================================================
    private fun registrarUsuarioConFirebase(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registro exitoso
                    Toast.makeText(baseContext, "Cuenta creada exitosamente.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    // Un punto clave: Al crear un usuario, Firebase lo autentica automáticamente.
                    // Aquí puedes navegar a tu actividad principal.
                    // val intent = Intent(this, HomeActivity::class.java)
                    // startActivity(intent)
                    // finish() // Cierra la actividad de registro para que el usuario no pueda volver.
                } else {
                    // Si el registro falla, muestra un mensaje.
                    val exception = task.exception
                    val mensajeError = when (exception) {
                        is FirebaseAuthWeakPasswordException -> "La contraseña es demasiado débil."
                        is FirebaseAuthUserCollisionException -> "El correo electrónico ya está en uso."
                        else -> "Fallo en el registro: ${exception?.message}"
                    }
                    Toast.makeText(baseContext, mensajeError, Toast.LENGTH_LONG).show()
                }
            }
    }
}