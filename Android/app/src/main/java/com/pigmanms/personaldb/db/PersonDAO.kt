package com.pigmanms.personaldb.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.pigmanms.personaldb.model.Person

class PersonDao(ctx: Context) {
    private val helper = PersonDbHelper(ctx)

    /**
     * Cursor → String helper
     *  – 컬럼 값이 NULL 이면 빈 문자열 반환해 NPE 를 예방합니다.
     */
    private fun Cursor.str(col: String): String {
        val idx = getColumnIndexOrThrow(col)
        return if (isNull(idx)) "" else getString(idx)
    }

    fun list(): List<Person> = helper.readableDatabase.rawQuery(
        "SELECT * FROM persons ORDER BY name", null
    ).use { c ->
        buildList {
            while (c.moveToNext()) add(
                Person(
                    id = c.getLong(c.getColumnIndexOrThrow("id")),
                    name = c.str("name"),
                    photoUri = if (c.isNull(c.getColumnIndexOrThrow("photoUri"))) null else c.getString(c.getColumnIndexOrThrow("photoUri")),
                    note = c.str("note"),
                    likes = c.str("likes"),
                    dislikes = c.str("dislikes"),
                    birthday = c.str("birthday"),
                    speech = c.str("speech"),
                    personality = c.str("personality"),
                    interests = c.str("interests"),
                    mbti = c.str("mbti"),
                    tags = c.str("tags")
                )
            )
        }
    }

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