package com.example.passwordgenerator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PasswordsAdapter(private var passwords: List<Passwords>, context : Context):
    RecyclerView.Adapter<PasswordsAdapter.PassViewerHolder>() {
    class PassViewerHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassViewerHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pass_item, parent,false)
        return PassViewerHolder(view)
    }

    override fun getItemCount(): Int = passwords.size



    override fun onBindViewHolder(holder: PassViewerHolder, position: Int) {
        val pass = passwords[position]
        holder.titleTextView.text = pass.title
        holder.contentTextView.text = pass.content
    }

    fun refreshData(newPasswords: List<Passwords>){
        passwords = newPasswords
        notifyDataSetChanged()
    }

}