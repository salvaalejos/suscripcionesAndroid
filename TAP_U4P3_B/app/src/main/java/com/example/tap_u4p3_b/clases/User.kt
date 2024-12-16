package com.example.tap_u4p3_b.clases

import java.io.Serializable

data class User(
    val idUser: String,
    val username: String,
    val password: String,
    val name: String,
    val user_type: String,
    val email: String,
    val phone: String,
    val Sucursal_idSucursal: String,
    val status: String

) : Serializable
