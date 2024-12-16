package com.example.tap_u4p3_b.clases

import java.io.Serializable

data class Sucursal(
    var idSucursal: Int?,
    var name: String?,
    var direction: String?,
    var percentageAdmin: String?,
    val percentageSucursal: String?,
    var phone: String?,
    val status: Boolean
) {
    override fun toString(): String {
        return name ?: "Sin nombre"
    }
}