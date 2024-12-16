package com.example.tap_u4p3_b

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tap_u4p3_b.clases.User
import com.google.gson.Gson
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    private lateinit var editUsr: EditText
    private lateinit var editPass: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editUsr = findViewById(R.id.editUsr)
        editPass = findViewById(R.id.editPass)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        btnLogin.setOnClickListener { login() }
        btnRegister.setOnClickListener { register() }
    }

    private fun login() {
        val usr = editUsr.text.toString().trim()
        val pass = editPass.text.toString().trim()

        if (usr.isEmpty()) {
            editUsr.error = "El usuario es obligatorio"
            return
        }
        if (pass.isEmpty()) {
            editPass.error = "La contraseña es obligatoria"
            return
        }

        val url = resources.getString(R.string.ws) + "ModelUser/endPointLogin.php"

        val request = object : StringRequest(Method.POST, url, {
            interpreta(it)
        }, {
            println("Error de red: ${it.message}")
            Toast.makeText(
                this@MainActivity,
                "Error al acceder, verifica tu conexión",
                Toast.LENGTH_LONG
            ).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["usr"] = usr
                params["pass"] = pass
                return params
            }
        }

        Volley.newRequestQueue(this).add(request)
    }


    private fun interpreta(result: String) {
        println(result)
        try {
            // Parsea el resultado como un JSONObject
            val jsonObject = org.json.JSONObject(result)

            // Convierte el JSONObject a un objeto User usando Gson
            val user = Gson().fromJson(jsonObject.toString(), User::class.java)

            // Inicia la siguiente actividad con los datos del usuario
            val segue = Intent(this, PrincipalActivity::class.java)
            segue.putExtra("usr", user)
            startActivity(segue)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al interpretar la respuesta del servidor", Toast.LENGTH_LONG).show()
        }
    }



    private fun register() {
        val segue = Intent(this, RegisterActivity::class.java)
        startActivity(segue)
    }

}