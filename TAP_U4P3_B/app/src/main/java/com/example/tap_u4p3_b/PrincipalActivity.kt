package com.example.tap_u4p3_b

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tap_u4p3_b.clases.Sucursal
import com.example.tap_u4p3_b.clases.User
import com.example.tap_u4p3_b.clases.Util
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

data class TotalResponse(val total: String)


class PrincipalActivity : AppCompatActivity() {
    private lateinit var welcomeText: TextView
    private lateinit var sucursalesList : ListView
    private lateinit var fabNuevaSucursal: FloatingActionButton
    private lateinit var totalSales: TextView
    private lateinit var unlog: Button

    private val sucursales = ArrayList<Sucursal>()

    private val endpoint = "ModelSucursal/endPointGetAllSucursal.php"

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val user = intent.getSerializableExtra("usr") as User?

        setContentView(R.layout.activity_principal_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        welcomeText = findViewById(R.id.textWelcome)
        if (user != null){
            welcomeText.text = "Bienvenido ${user.name}"
        }

        totalSales = findViewById(R.id.totalSales)
        totalVentas()

        unlog = findViewById(R.id.btnLogout)
        unlog.setOnClickListener { unlog() }




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sucursalesList = findViewById(R.id.sucursalesListView)

        fabNuevaSucursal = findViewById(R.id.fabNuevaCategoria)

        fabNuevaSucursal.setOnClickListener { nueva() }
        sucursalesList.setOnItemClickListener { adapterView, view, i, l -> acciones(i) }

        actualizaCategorias()



    }

    private fun totalVentas() {


        val url = resources.getString(R.string.ws) + "ModelPayment/endPointTotalGains.php"

        val request = object : StringRequest(Method.POST, url, {
            interpretaVentas(it)
        }, {
            println("Error de red: ${it.message}")
            Toast.makeText(
                this@PrincipalActivity,
                "Error al acceder, verifica tu conexión",
                Toast.LENGTH_LONG
            ).show()
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                return params
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun interpretaVentas(result: String) {
        println(result)
        try {
            // Parsea el resultado como un JSONObject
            val jsonObject = org.json.JSONObject(result)

            // Convierte el JSONObject a un objeto TotalResponse usando Gson
            val totalResponse = Gson().fromJson(jsonObject.toString(), TotalResponse::class.java)
            totalSales.text = "Total de ventas: $${totalResponse.total}"

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al interpretar la respuesta del servidor", Toast.LENGTH_LONG).show()
        }
    }

    private fun unlog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Cerrar sesión")
        dialog.setMessage("¿Estás seguro de cerrar sesión?")

        dialog.setPositiveButton("Sí") { dialogInterface: DialogInterface, i: Int ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        dialog.setNeutralButton("No") { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
        }

        dialog.show()
    }



    private fun actualizaCategorias() {
        val params = HashMap<String, String>()

        object : Util(this){
            override fun interpreta(result: String) {
                println(result)
                muestraCategorias(result)
            }
        }.consumePOST(endpoint, params)
    }

    private fun muestraCategorias(result: String) {
        sucursales.clear()
        val lista = ArrayList<String>()
        try {
            val jsonArray = JSONArray(result)
            for (i in 0..jsonArray.length()-1) {
                val json = jsonArray.getJSONObject(i)
                lista.add(json.getString("name"))

                val cat = Gson().fromJson(
                    json.toString(), Sucursal::class.java
                )
                sucursales.add(cat)
            }

            sucursalesList.adapter = ArrayAdapter(
                this, android.R.layout.simple_list_item_1, lista
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error en categorias", Toast.LENGTH_LONG).show()
        }
    }

    private fun nueva() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Nueva sucursal")
        dialog.setMessage("Ingrese nombre de la sucursal")

        // Edit para editar la categoria
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val editText = EditText(this).apply { hint = "Nombre sucursal" }
        val direction = EditText(this).apply { hint = "Dirección" }
        val percentageAdmin = EditText(this).apply { hint = "Porcentaje de administración" }
        val phone = EditText(this).apply { hint = "Teléfono" }

        layout.addView(editText)
        layout.addView(direction)
        layout.addView(percentageAdmin)
        layout.addView(phone)

        dialog.setView(layout)

        dialog.setPositiveButton("Guardar") { dialogInterface: DialogInterface, i: Int ->
            agregar(editText.text.toString(), direction.text.toString(), percentageAdmin.text.toString(), phone.text.toString())
        }

        dialog.setNeutralButton("Cancelar") { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
        }

        dialog.show()
    }

    private fun agregar(name: String, direction: String, percentageAdmin: String, phone: String) {
        val params = HashMap<String, String>()
        params.put("name", name)
        params.put("direction", direction)
        params.put("percentageAdmin", percentageAdmin)
        params.put("phone", phone)
        params.put("status", "1")

        add(params)
    }

    private fun acciones(index: Int) {
        val suc = sucursales.get(index)

        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Acciones (${suc.idSucursal})")
        dialog.setMessage(
            "¿Qué deseas hacer con la categoría\n${suc.name}?"
        )

        // Edit para editar la categoria
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL


        val editText = EditText(this).apply { hint = "Nombre sucursal" }
        val direction = EditText(this).apply { hint = "Dirección" }
        val percentageAdmin = EditText(this).apply { hint = "Porcentaje de administración" }


        val phone = EditText(this).apply { hint = "Teléfono" }

        layout.addView(editText)
        layout.addView(direction)
        layout.addView(percentageAdmin)
        layout.addView(phone)

        dialog.setView(layout)

        var newSuc: Sucursal = suc
        newSuc.idSucursal = suc.idSucursal
        newSuc.name = editText.text.toString()
        newSuc.direction = direction.text.toString()
        newSuc.percentageAdmin = percentageAdmin.text.toString()
        newSuc.phone = phone.text.toString()


        dialog.setPositiveButton("Editar") { dialogInterface: DialogInterface, i: Int ->
            editar(suc)
        }

        dialog.setNegativeButton("Borrar") { dialogInterface: DialogInterface, i: Int ->
            borrar(suc)
        }

        dialog.setNeutralButton("Cancelar") { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
        }

        dialog.show()
    }

    private fun editar(suc: Sucursal) {
        val params = HashMap<String, String>()
        params.put("name", suc.name.toString())
        params.put("idSucursal", "" + suc.idSucursal)
        params.put("direction", suc.direction.toString())
        params.put("percentageAdmin", suc.percentageAdmin.toString())
        params.put("phone", suc.phone.toString())
        params.put("status", "1")
        update(params)
    }

    private fun borrar(suc: Sucursal) {
        val params = HashMap<String, String>()
        params.put("idSucursal", "" + suc.idSucursal)

        delete(params)
    }

    private fun consumir(params: HashMap<String, String>) {
        object : Util(this) {
            override fun interpreta(result: String) {
                if(!result.contains("Error")) {
                    Toast.makeText(this@PrincipalActivity, "Acción exitosa", Toast.LENGTH_LONG).show()
                    actualizaCategorias()
                } else {
                    Toast.makeText(this@PrincipalActivity, "Error al ejecutar la acción", Toast.LENGTH_LONG).show()
                }
            }
        }.consumePOST(endpoint, params)
    }

    private fun delete(params: HashMap<String, String>) {
        object : Util(this) {
            override fun interpreta(result: String) {
                if(!result.contains("Error")) {
                    Toast.makeText(this@PrincipalActivity, "Eliminación exitosa", Toast.LENGTH_LONG).show()
                    actualizaCategorias()
                } else {
                    Toast.makeText(this@PrincipalActivity, "Error al ejecutar la acción", Toast.LENGTH_LONG).show()
                }
            }
        }.consumePOST("ModelSucursal/endPointDeleteSucursal.php", params)
    }

    private fun add(params: HashMap<String, String>) {
        object : Util(this) {
            override fun interpreta(result: String) {
                if(!result.contains("Error")) {
                    Toast.makeText(this@PrincipalActivity, "Creación exitosa", Toast.LENGTH_LONG).show()
                    actualizaCategorias()
                } else {
                    Toast.makeText(this@PrincipalActivity, "Error al ejecutar la acción", Toast.LENGTH_LONG).show()
                }
            }
        }.consumePOST("ModelSucursal/endPointAddSucursal.php", params)
    }

    private fun update(params: HashMap<String, String>) {
        object : Util(this) {
            override fun interpreta(result: String) {
                if(!result.contains("Error")) {
                    Toast.makeText(this@PrincipalActivity, "Actualización exitosa", Toast.LENGTH_LONG).show()
                    actualizaCategorias()
                } else {
                    Toast.makeText(this@PrincipalActivity, "Error al ejecutar la acción", Toast.LENGTH_LONG).show()
                }
            }
        }.consumePOST("ModelSucursal/endPointUpdateSucursal.php", params)
    }

}