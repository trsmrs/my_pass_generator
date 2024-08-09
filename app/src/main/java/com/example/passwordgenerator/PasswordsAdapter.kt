package com.example.passwordgenerator

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class PasswordsAdapter(private var passwords: List<Passwords>, context : Context):
    RecyclerView.Adapter<PasswordsAdapter.PassViewerHolder>() {
        private var db: PasswordsDatabaseHelper = PasswordsDatabaseHelper(context)

    class PassViewerHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val updateButton: ImageView = itemView.findViewById(R.id.btnEdit)
        val deleteButton: ImageView = itemView.findViewById(R.id.btnDel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassViewerHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pass_item, parent,false)
        return PassViewerHolder(view)
    }

    override fun getItemCount(): Int = passwords.size



    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: PassViewerHolder, position: Int) {
        val pass = passwords[position]
        holder.titleTextView.text = pass.title
        holder.contentTextView.text = pass.content

        holder.updateButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdatePassActivity::class.java).apply {
                putExtra("pass_id", pass.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            // Mostra um diálogo de confirmação
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
            alertDialogBuilder.setTitle("Confirmação")
            alertDialogBuilder.setMessage("Tem certeza de que deseja excluir esta senha?")
            alertDialogBuilder.setPositiveButton("Sim") { _, _ ->
                // Usuário confirmou, exclui a senha
                db.deletePass(pass.id)
                refreshData(db.getAllPass())
                Toast.makeText(holder.itemView.context, "Senha Excluída!", Toast.LENGTH_SHORT).show()
            }
            alertDialogBuilder.setNegativeButton("Não") { _, _ ->
                // Usuário cancelou, não faz nada
            }
            alertDialogBuilder.show()
        }

        holder.itemView.setOnLongClickListener {
            val clipboardManager = holder.itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Password Content", holder.contentTextView.text.toString())
                    clipboardManager.setPrimaryClip(clipData)
                    Toast.makeText(holder.itemView.context, "Senha copiada para a área de transferência", Toast.LENGTH_SHORT).show()

            return@setOnLongClickListener true
        }

    }

    fun refreshData(newPasswords: List<Passwords>){
        passwords = newPasswords
        notifyDataSetChanged()
    }

}