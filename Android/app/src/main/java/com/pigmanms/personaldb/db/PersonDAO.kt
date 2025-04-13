package com.pigmanms.personaldb.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.pigmanms.personaldb.model.Person

class PersonDao(ctx: Context) {
    private val helper = PersonDbHelper(ctx)

    // Cursor → String (NULL 안전)
    private fun Cursor.str(col: String): String {
        val i = getColumnIndexOrThrow(col)
        return if (isNull(i)) "" else getString(i)
    }

    private fun Cursor.toPerson() = Person(
        id = getLong(getColumnIndexOrThrow("id")),
        name = str("name"),
        photoUri = if (isNull(getColumnIndexOrThrow("photoUri"))) null else getString(getColumnIndexOrThrow("photoUri")),
        note = str("note"), likes = str("likes"), dislikes = str("dislikes"),
        birthday = str("birthday"), speech = str("speech"), personality = str("personality"),
        interests = str("interests"), mbti = str("mbti"), tags = str("tags")
    )

    fun list(): List<Person> = helper.readableDatabase.rawQuery(
        "SELECT * FROM persons ORDER BY name", null
    ).use { c -> buildList { while (c.moveToNext()) add(c.toPerson()) } }

    fun get(id: Long): Person? = helper.readableDatabase.rawQuery(
        "SELECT * FROM persons WHERE id=?", arrayOf(id.toString())
    ).use { if (it.moveToFirst()) it.toPerson() else null }

    fun search(keyword: String): List<Person> = helper.readableDatabase.rawQuery(
        "SELECT * FROM persons WHERE name LIKE ? OR note LIKE ? OR likes LIKE ? OR dislikes LIKE ? OR tags LIKE ? ORDER BY name",
        Array(5) { "%$keyword%" }
    ).use { c -> buildList { while (c.moveToNext()) add(c.toPerson()) } }

    fun save(p: Person) {
        val v = ContentValues().apply {
            put("name", p.name);      put("photoUri", p.photoUri);   put("note", p.note)
            put("likes", p.likes);    put("dislikes", p.dislikes); put("birthday", p.birthday)
            put("speech", p.speech);  put("personality", p.personality); put("interests", p.interests)
            put("mbti", p.mbti);      put("tags", p.tags)
        }
        val db = helper.writableDatabase
        if (p.id == 0L) db.insert("persons", null, v)
        else db.update("persons", v, "id=?", arrayOf(p.id.toString()))
    }
}