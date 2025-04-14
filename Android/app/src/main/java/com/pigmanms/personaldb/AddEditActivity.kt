package com.pigmanms.personaldb

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.pigmanms.personaldb.db.PersonDao
import com.pigmanms.personaldb.model.Person
import com.pigmanms.personaldb.ui.MainViewModel


class AddEditActivity : AppCompatActivity() {

    private lateinit var dao: PersonDao
    private var photoUri: String? = null
    private var editingId: Long = 0
    private val viewModel: MainViewModel by viewModels()


    // 이미지 선택 런처
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri ?: return@registerForActivityResult

        // 권한을 “영구” 보존 – READ 플래그만 주면 됩니다
        contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        photoUri = uri.toString()
        // BasicFragment 가 열려 있다면 즉시 썸네일 반영
        supportFragmentManager.findFragmentByTag("f0")
            ?.view?.findViewById<ImageView>(R.id.imgPhoto)
            ?.setImageURI(uri)
    }

    /** 외부에서 호출 */
    fun launchImagePicker() = pickImageLauncher.launch(
        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)
        dao = PersonDao(this)
        editingId = intent.getLongExtra("personId", 0)

        val pager = findViewById<ViewPager2>(R.id.viewPager)
        val tabs = findViewById<TabLayout>(R.id.tabLayout)
        pager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 2
            override fun createFragment(position: Int): Fragment =
                if (position == 0) BasicFragment() else DetailFragment()
        }
        TabLayoutMediator(tabs, pager) { tab, pos ->
            tab.text = if (pos == 0) "기본" else "세부"
        }.attach()
    }


    // ──────────────────────────────────────────────────────────────────────────────
    //  Static 프래그먼트 (중첩 클래스 ‑ inner 키워드 X)  ────────────────────────────────

    /** 사진·이름·메모 탭 */
    class BasicFragment : Fragment(R.layout.tab_basic) {
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            val act = requireActivity() as AddEditActivity
            view.findViewById<Button>(R.id.btnPick).setOnClickListener { act.launchImagePicker() }
            if (act.editingId != 0L) act.fillFields(view)
        }
    }

    /** 세부 필드 + 저장 */
    class DetailFragment : Fragment(R.layout.tab_detail) {
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            val act = requireActivity() as AddEditActivity
            view.findViewById<Button>(R.id.btnSave).setOnClickListener { act.save() }
            if (act.editingId != 0L) act.fillFields(view)
        }
    }

    // ──────────────────────────────────────────────────────────────────────────────

    private fun fillFields(root: View) {
        val p = dao.list().firstOrNull { it.id == editingId } ?: return
        root.findViewById<EditText>(R.id.edtName)?.setText(p.name)
        root.findViewById<EditText>(R.id.edtNote)?.setText(p.note)
        photoUri = p.photoUri
        photoUri?.let { root.findViewById<ImageView>(R.id.imgPhoto)?.setImageURI(Uri.parse(it)) }
        root.findViewById<EditText>(R.id.edtLikes)?.setText(p.likes)
        root.findViewById<EditText>(R.id.edtDislikes)?.setText(p.dislikes)
        root.findViewById<EditText>(R.id.edtBirthday)?.setText(p.birthday)
        root.findViewById<EditText>(R.id.edtSpeech)?.setText(p.speech)
        root.findViewById<EditText>(R.id.edtPersonality)?.setText(p.personality)
        root.findViewById<EditText>(R.id.edtInterests)?.setText(p.interests)
        root.findViewById<EditText>(R.id.edtMbti)?.setText(p.mbti)
        root.findViewById<EditText>(R.id.edtTags)?.setText(p.tags)
    }

    private fun save() {
        // 두 프래그먼트의 루트 뷰를 안전하게 가져온다 ("f0"=Basic, "f1"=Detail)
        val basicRoot = supportFragmentManager.findFragmentByTag("f0")?.view
        val detailRoot = supportFragmentManager.findFragmentByTag("f1")?.view

        if (basicRoot == null || detailRoot == null) {
            Toast.makeText(this, "화면이 아직 준비되지 않았습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val person = Person(
            id = editingId,
            name = basicRoot.findViewById<EditText>(R.id.edtName).text.toString(),
            photoUri = photoUri,
            note = basicRoot.findViewById<EditText>(R.id.edtNote).text.toString(),
            likes = detailRoot.findViewById<EditText>(R.id.edtLikes).text.toString(),
            dislikes = detailRoot.findViewById<EditText>(R.id.edtDislikes).text.toString(),
            birthday = detailRoot.findViewById<EditText>(R.id.edtBirthday).text.toString(),
            speech = detailRoot.findViewById<EditText>(R.id.edtSpeech).text.toString(),
            personality = detailRoot.findViewById<EditText>(R.id.edtPersonality).text.toString(),
            interests = detailRoot.findViewById<EditText>(R.id.edtInterests).text.toString(),
            mbti = detailRoot.findViewById<EditText>(R.id.edtMbti).text.toString(),
            tags = detailRoot.findViewById<EditText>(R.id.edtTags).text.toString()
        )
        dao.save(person)
        setResult(RESULT_OK)   // ✔ 저장 성공 신호
        finish()
    }
    override fun onResume() {
        super.onResume()
        viewModel.refresh()        // ↓ 아래 3단계 참고 //TODO: FIXTHIS
    }

}