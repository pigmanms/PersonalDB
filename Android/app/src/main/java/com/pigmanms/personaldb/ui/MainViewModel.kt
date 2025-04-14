package com.pigmanms.personaldb.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pigmanms.personaldb.db.PersonDao
import com.pigmanms.personaldb.model.Person

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = PersonDao(app)

    private val _people = MutableLiveData<List<Person>>(dao.list())

    val people: LiveData<List<Person>> = _people
    fun refresh() {
        _people.value = dao.list()   // DAO 재호출 → LiveData 갱신
    }
    init { refresh() }

    /** 빠른 검색 */
    fun search(q: String) {
        val query = q.trim().lowercase()
        _people.value = if (query.isEmpty()) dao.list() else dao.list().filter { p ->
            listOf(
                p.name, p.note, p.likes, p.dislikes, p.birthday, p.speech,
                p.personality, p.interests, p.mbti, p.tags
            ).any { it.contains(query, true) }
        }
    }
}
