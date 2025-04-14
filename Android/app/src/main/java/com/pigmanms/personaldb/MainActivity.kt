package com.pigmanms.personaldb

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.pigmanms.personaldb.db.PersonDao
import com.pigmanms.personaldb.util.JsonExporter
import com.pigmanms.personaldb.util.JsonImporter
import com.pigmanms.personaldb.ui.PersonAdapter
import androidx.activity.viewModels
import com.pigmanms.personaldb.ui.MainViewModel
import com.pigmanms.personaldb.R
import androidx.recyclerview.widget.LinearLayoutManager


class MainActivity : AppCompatActivity() {
    private lateinit var dao: PersonDao
    private lateinit var adapter: PersonAdapter
    private var selectedId: Long? = null
    private val viewModel: MainViewModel by viewModels()

    // JSON 가져오기 런처
    private val pickJson = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { JsonImporter.import(this, it); refresh() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. 어댑터 인스턴스화
        adapter = PersonAdapter { id ->
            startActivity(Intent(this, AddEditActivity::class.java).apply {
                putExtra("personId", id)     // ← 키 이름 통일!
            })
        }

        // 2. RecyclerView 준비
        findViewById<RecyclerView>(R.id.recycler).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        // 3. LiveData 관찰 → 리스트 전달 (어댑터 초기화 이후!)
        viewModel.people.observe(this) { list ->
            adapter.submitList(list)
        }
    }

    private fun refresh() = adapter.submitList(dao.list())

    // ───────────────────────── 메뉴 ─────────────────────────
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "이름·태그·MBTI 등 검색"

        // 🔍 실시간 필터링
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?) = false
            override fun onQueryTextChange(q: String?): Boolean {
                viewModel.search(q.orEmpty())   // ← ViewModel 에 quick‑search 함수 구현
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_add      -> { startActivity(Intent(this, AddEditActivity::class.java)); true }
        R.id.menuExportAll   -> { JsonExporter.exportAll(this); true }
        R.id.menuExportOne   -> { /* 선택 id 전달 */ true }
        R.id.menuImport      -> { /* SAF → JsonImporter */ true }
        else                 -> super.onOptionsItemSelected(item)
    }

    private fun share(uri: Uri) {
        val i = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(i, "Export JSON"))
    }
    private val editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) viewModel.refresh()   // 🔄 즉시 재조회
    }

}