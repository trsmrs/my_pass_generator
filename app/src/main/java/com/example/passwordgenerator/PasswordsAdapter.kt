package com.example.passwordgenerator

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PasswordsAdapter(private var passwords: List<Passwords>, context : Context):
    RecyclerView.Adapter<PasswordsAdapter.PassViewerHolder>() {
    class PassViewerHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val updateButton: ImageView = itemView.findViewById(R.id.btnEdit)
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

        holder.updateButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateActivity::class.java).apply {
                putExtra("pass_id", pass.id)
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    fun refreshData(newPasswords: List<Passwords>){
        passwords = newPasswords
        notifyDataSetChanged()
    }

}