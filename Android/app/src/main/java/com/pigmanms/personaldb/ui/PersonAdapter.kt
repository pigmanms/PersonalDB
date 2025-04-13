package com.pigmanms.personaldb.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pigmanms.personaldb.R
import com.pigmanms.personaldb.model.Person

class PersonAdapter(private val onClick: (Long) -> Unit) :
    ListAdapter<Person, PersonAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Person>() {
            override fun areItemsTheSame(o: Person, n: Person) = o.id == n.id
            override fun areContentsTheSame(o: Person, n: Person) = o == n
        }
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val txtName: TextView = view.findViewById(R.id.txtName)
        private val imgPhoto: ImageView = view.findViewById(R.id.imgPhoto)
        fun bind(p: Person) {
            txtName.text = p.name
            if (p.photoUri != null) imgPhoto.setImageURI(Uri.parse(p.photoUri))
            else imgPhoto.setImageResource(R.drawable.ic_launcher_background)
            itemView.setOnClickListener { onClick(p.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_person, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))
}