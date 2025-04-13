package com.pigmanms.personaldb.model

data class Person(
    val id: Long = 0,
    val name: String,
    val photoUri: String?,
    val note: String
)