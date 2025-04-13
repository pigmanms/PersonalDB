package com.pigmanms.personaldb

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.pigmanms.personaldb.db.PersonDao
import com.pigmanms.personaldb.model.Person

class AddEditActivity : AppCompatActivity() {
    private lateinit var dao: PersonDao
    private var photoUri: String? = null
    private var editingId: Long = 0

    /** SAF `ACTION_GET_CONTENT` → 스토리지 권한 필요 없음 */
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            /* 선택한 이미지 URI 를 앱이 계속 읽을 수 있도록 영구 권한 요청 */
            try {
                contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: SecurityException) { /* 이미 보유 중일 수 있음 */ }
            photoUri = it.toString()
            findViewById<ImageView>(R.id.imgPhoto).setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)
        dao = PersonDao(this)

        val img = findViewById<ImageView>(R.id.imgPhoto)
        val edtName = findViewById<EditText>(R.id.edtName)
        val edtNote = findViewById<EditText>(R.id.edtNote)

        editingId = intent.getLongExtra("id", 0)
        if (editingId != 0L) {
            val p = dao.list().first { it.id == editingId }
            edtName.setText(p.name)
            edtNote.setText(p.note)
            photoUri = p.photoUri
            photoUri?.let { uriStr ->
                try { img.setImageURI(Uri.parse(uriStr)) } catch (_: SecurityException) {}
            }
        }

        findViewById<Button>(R.id.btnPick).setOnClickListener {
            pickImage.launch("image/*")
        }
        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val person = Person(editingId, edtName.text.toString(), photoUri, edtNote.text.toString())
            dao.save(person)
            finish()
        }
    }
}