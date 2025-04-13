package com.pigmanms.personaldb.util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import com.google.gson.Gson
import com.pigmanms.personaldb.db.PersonDao
import com.pigmanms.personaldb.model.Person
import java.io.File

object JsonImporter {
    fun import(ctx: Context, jsonUri: Uri) {
        val json = ctx.contentResolver.openInputStream(jsonUri)!!.bufferedReader().readText()
        val arr = Gson().fromJson(json, Array<JsonExporter.JsonPerson>::class.java)
        val dao = PersonDao(ctx)
        arr.forEach { jp: JsonExporter.JsonPerson ->
            val photoUri = jp.photoBase64?.let { b64 ->
                val bytes = Base64.decode(b64, Base64.DEFAULT)
                val file = File(ctx.cacheDir, "p_${System.currentTimeMillis()}.jpg")
                file.writeBytes(bytes)
                MediaStore.Images.Media.insertImage(ctx.contentResolver, file.absolutePath, jp.name, null)
                Uri.fromFile(file).toString()
            }
            dao.save(
                Person(
                    name = jp.name, note = jp.note, likes = jp.likes, dislikes = jp.dislikes,
                    birthday = jp.birthday, speech = jp.speech, personality = jp.personality,
                    interests = jp.interests, mbti = jp.mbti, tags = jp.tags, photoUri = photoUri
                )
            )
        }
    }
}