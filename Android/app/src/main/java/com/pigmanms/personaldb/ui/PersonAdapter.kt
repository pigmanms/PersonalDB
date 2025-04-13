package com.pigmanms.personaldb.ui

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.pigmanms.personaldb.R
import com.pigmanms.personaldb.model.Person

class PersonAdapter(ctx: Context, private var items: List<Person>) : ArrayAdapter<Person>(ctx, 0, items) {

    fun update(newList: List<Person>) { items = newList; notifyDataSetChanged() }

    override fun getCount() = items.size
    override fun getItem(position: Int) = items[position]

    override fun getView(pos: Int, convert: View?, parent: ViewGroup): View {
        val v = convert ?: LayoutInflater.from(context).inflate(R.layout.item_person, parent, false)
        val p = items[pos]
        v.findViewById<TextView>(R.id.txtName).text = p.name
        v.findViewById<TextView>(R.id.txtNote).text = "${p.mbti}  ${p.tags.split(",").firstOrNull() ?: ""}"
        p.photoUri?.let { v.findViewById<ImageView>(R.id.imgPhoto).setImageURI(Uri.parse(it)) }
        return v
    }
}