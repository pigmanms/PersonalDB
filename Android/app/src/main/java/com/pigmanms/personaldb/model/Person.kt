package com.pigmanms.personaldb.model

data class Person(
    val id: Long = 0,
    val name: String,
    val photoUri: String?,
    val note: String,
    val likes: String = "",
    val dislikes: String = "",
    val birthday: String = "",   // yyyy‑MM‑dd
    val speech: String = "",      // 말투
    val personality: String = "", // 성향
    val interests: String = "",
    val mbti: String = "",
    val tags: String = ""          // comma‑separated
)