package com.example.madcamp_week2.ui.main.contact

import android.content.Context
import android.system.Os.bind
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp_week2.R

class ContactViewAdapter(private val context: Context, val phoneBookList: List<PhoneBook>):
    RecyclerView.Adapter<ContactViewAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ContactViewAdapter.ViewHolder, position: Int) {
        val item = phoneBookList[position]
//        val listener = View.OnClickListener {it ->
//            Toast.makeText(it.context, "name: ${item.name}", Toast.LENGTH_SHORT).show()
//        }
        holder.apply {
            bind(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ContactViewAdapter.ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ViewHolder(inflatedView)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name = v?.findViewById<TextView>(R.id.name)
        val number = v?.findViewById<TextView>(R.id.number)

        fun bind (phoneBook: PhoneBook) {
            //id?.text = phoneBook.id
            name?.text = phoneBook.name
            number?.text = phoneBook.number
        }
    }

    override fun getItemCount(): Int {
        return phoneBookList.size
    }

}