package com.pigmanms.personaldb.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PersonDbHelper(ctx: Context) : SQLiteOpenHelper(ctx, "people.db", null, 2) {
    override fun onCreate(db: SQLiteDatabase) {
        createV2(db)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldV: Int, newV: Int) {
        if (oldV == 1) {
            db.execSQL("ALTER TABLE persons ADD COLUMN likes TEXT;")
            db.execSQL("ALTER TABLE persons ADD COLUMN dislikes TEXT;")
            db.execSQL("ALTER TABLE persons ADD COLUMN birthday TEXT;")
            db.execSQL("ALTER TABLE persons ADD COLUMN speech TEXT;")
            db.execSQL("ALTER TABLE persons ADD COLUMN personality TEXT;")
            db.execSQL("ALTER TABLE persons ADD COLUMN interests TEXT;")
            db.execSQL("ALTER TABLE persons ADD COLUMN mbti TEXT;")
            db.execSQL("ALTER TABLE persons ADD COLUMN tags TEXT;")
        } else {
            db.execSQL("DROP TABLE IF EXISTS persons"); createV2(db)
        }
    }
    private fun createV2(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE persons(
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              name TEXT NOT NULL,
              photoUri TEXT,
              note TEXT,
              likes TEXT,
              dislikes TEXT,
              birthday TEXT,
              speech TEXT,
              personality TEXT,
              interests TEXT,
              mbti TEXT,
              tags TEXT
            );
            """.trimIndent()
        )
    }
}