package com.pigmanms.personaldb.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PersonDbHelper(context: Context) : SQLiteOpenHelper(context, "people.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE persons(
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              name TEXT NOT NULL,
              photoUri TEXT,
              note TEXT
            );
            """.trimIndent()
        )
    }
    override fun onUpgrade(db: SQLiteDatabase, old: Int, newV: Int) {
        db.execSQL("DROP TABLE IF EXISTS persons"); onCreate(db)
    }
}