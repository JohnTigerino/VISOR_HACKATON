package com.bionica.visor_prueba3
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
//import androidx.compose.foundation.gestures.Orientation
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.bionica.visor_prueba3.data.model.User
//import com.bionica.visor_prueba3.data.model.ApiResponse
import com.bionica.visor_prueba3.network.ApiService
import android.opengl.ETC1.isValid

class RegisterActivity : AppCompatActivity() {
    private val api = ApiService.create()    //instancia para la api

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
        //val Rol =   spinner.selectedItem.toString()  //guardamos la seleccion del spinner

        //Guardar elementos de la vista en variables para usar
        val btnIngresar = findViewById<Button>(R.id.btn_ingresar_reg)
        val editTextEmailAddress = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val editTxtPassword = findViewById<EditText>(R.id.editTxtPassword)
        val editTxtPassword2 = findViewById<EditText>(R.id.editTxtPassword2)


        val btnCerrar = findViewById< ImageButton>(R.id.btnCerrar)  //+
        //const Estado = ;

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        btnIngresar.setOnClickListener {
            //startActivity(Intent(this, HomeActivity::class.java))

            val email = editTextEmailAddress.text.toString().trim()
            val password = editTxtPassword.text.toString().trim()
            val password2 = editTxtPassword2.text.toString().trim()  //se usa para la comparacion de contrasenas
            //cons isValid,
            //val spinner = spinner.selectedItem.toString()

            //parte del codigo para registro segun video de mouradev
            if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (editTxtPassword.length() < 6 || editTxtPassword2.length() < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //contrasenas iguales
            if (password != password2){
                Toast.makeText(this, "Las contraseñas deben ser iguales.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar formato de correo
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextEmailAddress.error = "Correo inválido"
                return@setOnClickListener
                //isValid = false
            }

            //if (!isValid) return@setOnClickListener
            //llamar a la funcion
            registrarUsuarioConFirebase(email, password);
        }


        //=====
        btnCerrar.setOnClickListener {
            //val intent = Intent(this, AuthActivity::class.java)
            //startActivity(intent)
            finish()
        }

        //=====Evitar cambio de orientacion=====
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //==========Prueba de la conexion con la api para la bd ==========
        /**lifecycleScope.launch {

            val email = editTextEmailAddress.text.toString().trim()
            val password = editTxtPassword.text.toString().trim()
            val name = findViewById<EditText>(R.id.editTxtName)

            try {
                // Paso 1: crear usuario en Firebase
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser = task.result?.user
                            val newUser = User(
                                id = 0, // lo asigna tu backend
                                Nombre = name,
                                email = firebaseUser?.email ?: email
                            )

                            // Paso 2: registrar en tu API
                            lifecycleScope.launch {
                                try {
                                    val response = ApiService.create().createUser(newUser)
                                    if (response.success) {
                                        // Usuario registrado en ambos lados
                                        Log.d("Register", "Usuario creado: ${response.data}")
                                    } else {
                                        Log.e("Register", "Error API: ${response.message}")
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        } else {
                            Log.e("Register", "Error Firebase: ${task.exception?.message}")
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }***/
    }
    //=======================================================================
    private fun registrarUsuarioConFirebase(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val name = findViewById<EditText>(R.id.editTxtName).text.toString()
                    val Estado = "Activo"
                    val spinner: Spinner = findViewById(R.id.spinnerRoles)
                    val Rol = spinner.selectedItem.toString()

                    val firebaseUser = task.result?.user
                    val newUser = User(
                        //id = 0, // lo asigna tu backend
                        Nombre = name,
                        Correo = firebaseUser?.email ?: email,
                        Estado = Estado,
                        Rol = Rol
                    )

                    lifecycleScope.launch {
                        try {
                            val response = ApiService.create().createUser(newUser)
                            if (response.success) {
                                // Usuario registrado en ambos lados
                                Log.d("Register", "Usuario creado: ${response.data}")
                            } else {
                                Log.e("Register", "Error API: ${response.message}")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
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