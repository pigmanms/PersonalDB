package com.pigmanms.personaldb

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pigmanms.personaldb.db.PersonDao
import com.pigmanms.personaldb.ui.PersonAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var dao: PersonDao
    private lateinit var adapter: PersonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dao = PersonDao(this)

        val listView = findViewById<ListView>(R.id.listView)
        adapter = PersonAdapter(this, dao.list())
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val p = adapter.getItem(position)
            val i = Intent(this, AddEditActivity::class.java).apply { putExtra("id", p.id) }
            startActivity(i)
        }

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            startActivity(Intent(this, AddEditActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.update(dao.list())
    }
}