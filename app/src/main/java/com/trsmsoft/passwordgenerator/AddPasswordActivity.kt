package com.trsmsoft.passwordgenerator

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.trsmsoft.passwordgenerator.databinding.ActivityAddPasswordBinding
import com.trsmsoft.passwordgenerator.databinding.ActivityVaultBinding

class AddPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPasswordBinding
    private lateinit var db: PasswordsDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = PasswordsDatabaseHelper(this)

        binding.saveBtn.setOnClickListener {
            val title = binding.titlePassword.text.toString()
            val content = binding.contentPassword.text.toString()
            val password = Passwords(0, title, content)
            db.insertPassword(password)
            finish()
            Toast.makeText(this, "Senha Salva", Toast.LENGTH_SHORT).show()
        }
    }
}