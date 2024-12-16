package com.example.tap_u4p3_b

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tap_u4p3_b.clases.Sucursal
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RegisterActivity : AppCompatActivity() {

    private lateinit var editUsuario: EditText
    private lateinit var editNombre: EditText
    private lateinit var editPassword: EditText
    private lateinit var editPassword2: EditText
    private lateinit var editPhone: EditText
    private lateinit var editEmail: EditText
    private lateinit var editSucursal: Spinner
    private lateinit var btnRegistrar: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readSucursales()
        enableEdgeToEdge()

        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editUsuario = findViewById(R.id.editUsuario)
        editNombre = findViewById(R.id.editNombre)
        editPassword = findViewById(R.id.editPassword)
        editPassword2 = findViewById(R.id.editPassword2)
        editPhone = findViewById(R.id.editPhone)
        editEmail = findViewById(R.id.editEmail)


        btnRegistrar = findViewById(R.id.btnRegistrar)

        btnRegistrar.setOnClickListener { registrar() }

        // Obtén la referencia al Spinner
        editSucursal = findViewById(R.id.sucursal)


    }

    private fun readSucursales() {
        val url = resources.getString(R.string.ws) + "ModelSucursal/endPointGetActives.php"

        val request = object : StringRequest(Method.POST, url, { response ->
            try {
                val sucursales = interpretaSucursales(response)
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sucursales)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                editSucursal.adapter = adapter
            } catch (e: Exception) {
                println("Error al interpretar sucursales: ${e.message}")
            }
        }, { error ->
            println("Error en la solicitud: ${error.message}")
            Toast.makeText(this@RegisterActivity, "Error al leer sucursales", Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                return HashMap()
            }
        }

        Volley.newRequestQueue(this).add(request)
    }



    private fun interpretaSucursales(response: String): List<Sucursal> {
        val gson = Gson()
        return try {
            val listType = object : TypeToken<List<Sucursal>>() {}.type
            gson.fromJson(response, listType)
        } catch (e: Exception) {
            println("Error al convertir JSON: ${e.message}")
            emptyList()
        }
    }



    private fun registrar() {
        if(editUsuario.text.isEmpty()) {
            editUsuario.error = "El usuario es requerido"
            return
        }
        if(editNombre.text.isEmpty()) {
            editNombre.error = "El nombre es requerido"
            return
        }
        if(editPassword.text.isEmpty()) {
            editPassword.error = "La contraseña es requerida"
            return
        }
        if(editPassword.text.equals(editPassword2.text)) {
            editPassword.error = "Las contraseñas no coinciden"
            return
        }
        if(editPhone.text.isEmpty()) {
            editPhone.error = "El teléfono es requerido"
            return
        }
        if(editEmail.text.isEmpty()) {
            editEmail.error = "El correo es requerido"
            return
        }



        procesarRegistro(editUsuario.text.toString(), editPassword.text.toString(), editNombre.text.toString(), editPhone.text.toString(), editEmail.text.toString())
    }

    private fun procesarRegistro(usr: String, pass: String, name: String, phone: String, email: String) {
        val sucursalSeleccionada = editSucursal.selectedItem as Sucursal
        val idSucursal = sucursalSeleccionada.idSucursal?.toString() ?: ""

        val url = resources.getString(R.string.ws) + "ModelUser/endpointRegister.php"

        val request = object : StringRequest(Method.POST, url, {
            interpreta(it)
        }, {
            println(it.message)
            Toast.makeText(this@RegisterActivity, "Error al registrar", Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String, String>()
                params["username"] = usr
                params["password"] = pass
                params["name"] = name
                params["phone"] = phone
                params["email"] = email
                params["Sucursal_idSucursal"] = idSucursal
                params["user_type"] = "2"
                params["status"] = "1"
                return params
            }
        }
        Volley.newRequestQueue(this).add(request)
    }



    private fun interpreta(result: String) {
        println(result)
        if(!result.contains("Error")) {
            Toast.makeText(this, "Registrado", Toast.LENGTH_LONG).show()
            finish()
        } else {
            Toast.makeText(this, "Hubo un error al registrar, intente de nuevo más tarde", Toast.LENGTH_LONG).show()
        }
    }

}