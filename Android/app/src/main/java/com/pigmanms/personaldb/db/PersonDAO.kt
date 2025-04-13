package com.pigmanms.personaldb.db

import android.content.ContentValues
import android.content.Context
import com.pigmanms.personaldb.model.Person

class PersonDao(ctx: Context) {
    private val helper = PersonDbHelper(ctx)

    fun list(): List<Person> = helper.readableDatabase.rawQuery(
        "SELECT * FROM persons ORDER BY name", null
    ).use { c ->
        buildList {
            while (c.moveToNext()) add(
                Person(
                    id = c.getLong(c.getColumnIndexOrThrow("id")),
                    name = c.getString(c.getColumnIndexOrThrow("name")),
                    photoUri = c.getString(c.getColumnIndexOrThrow("photoUri")),
                    note = c.getString(c.getColumnIndexOrThrow("note"))
                )
            )
        }
    }

    fun save(p: Person) {
        val db = helper.writableDatabase
        val v = ContentValues().apply {
            put("name", p.name)
            put("photoUri", p.photoUri)
            put("note", p.note)
        }
        if (p.id == 0L) db.insert("persons", null, v)
        else db.update("persons", v, "id=?", arrayOf(p.id.toString()))
    }

    fun delete(id: Long) {
        helper.writableDatabase.delete("persons", "id=?", arrayOf(id.toString()))
    }
}