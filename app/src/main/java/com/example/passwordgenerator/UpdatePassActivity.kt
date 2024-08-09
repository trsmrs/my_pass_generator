package com.example.passwordgenerator

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.passwordgenerator.databinding.ActivityUpdateBinding

class UpdatePassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateBinding
    private lateinit var db: PasswordsDatabaseHelper
    private var passId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = PasswordsDatabaseHelper(this)

        passId = intent.getIntExtra("pass_id", -1)
        if(passId == -1){
            finish()
            return
        }

        val pass = db.getPassByID(passId)
        binding.editTitlePassword.setText(pass.title)
        binding.editContentPassword.setText(pass.content)

        binding.editSaveBtn.setOnClickListener {
            val newTitle = binding.editTitlePassword.text.toString()
            val newContent = binding.editContentPassword.text.toString()
            val updatePasswords = Passwords(passId, newTitle, newContent )
            db.updatePass(updatePasswords)
            finish()
            Toast.makeText(this, "Alterações salvas!", Toast.LENGTH_SHORT).show()
        }
    }
}