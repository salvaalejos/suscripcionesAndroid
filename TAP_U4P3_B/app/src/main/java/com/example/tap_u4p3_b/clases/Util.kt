package com.example.tap_u4p3_b.clases

import android.content.Context
import android.widget.Toast
import com.android.volley.Request.Method
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tap_u4p3_b.R

abstract class Util(val ctx: Context) {

    private val ws = "http://192.168.88.78/suscripcionesPHP/Models/"

    fun consumePOST(endpoint: String, params: HashMap<String,String>) {
        val url = ws + endpoint

        val request = object : StringRequest(
            Method.POST, url, { interpreta(it) }, { fallo(it.message) }
        ) {
            override fun getParams(): MutableMap<String, String>? {
                return params
            }
        }

        Volley.newRequestQueue(ctx).add(request)
    }

    abstract fun interpreta(result: String)

    private fun fallo(msg: String?) {
        /*if( msg != null) {
            println(msg)
            Toast.makeText(ctx, "Error", Toast.LENGTH_LONG).show()
        }*/
        msg?.let {
            println(it)
            Toast.makeText(ctx, "Error", Toast.LENGTH_LONG).show()
        }
    }

}