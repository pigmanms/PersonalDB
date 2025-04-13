package com.pigmanms.personaldb.util

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.pigmanms.personaldb.db.PersonDao
import com.pigmanms.personaldb.model.Person
import java.io.File
import java.io.FileOutputStream

object JsonExporter {
    private val gson = Gson()

    fun exportAll(ctx: Context): Uri = export(ctx, PersonDao(ctx).list())
    fun exportOne(ctx: Context, id: Long): Uri =
        export(ctx, listOfNotNull(PersonDao(ctx).get(id)))

    // ───────────────────────────────── private ─────────────────────────────
    private fun export(ctx: Context, list: List<Person>): Uri {
        val json = gson.toJson(list.map { p: Person -> p.toJson(ctx) })
        val file = File(ctx.cacheDir, "people-${System.currentTimeMillis()}.json")
        FileOutputStream(file).use { it.write(json.toByteArray()) }
        return FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprov", file)
    }

    private fun Person.toJson(ctx: Context) = JsonPerson(
        id, name, note, likes, dislikes, birthday, speech,
        personality, interests, mbti, tags,
        photoBase64 = photoUri?.let { uriStr ->
            ctx.contentResolver.openInputStream(Uri.parse(uriStr))?.use { input ->
                Base64.encodeToString(input.readBytes(), Base64.NO_WRAP)
            }
        }
    )

    data class JsonPerson(
        val id: Long,
        val name: String,
        val note: String,
        val likes: String,
        val dislikes: String,
        val birthday: String,
        val speech: String,
        val personality: String,
        val interests: String,
        val mbti: String,
        val tags: String,
        @SerializedName("photo") val photoBase64: String? = null
    )
}