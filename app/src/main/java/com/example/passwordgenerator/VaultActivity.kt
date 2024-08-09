package com.example.passwordgenerator

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passwordgenerator.databinding.ActivityVaultBinding

class VaultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVaultBinding
    private lateinit var db: PasswordsDatabaseHelper
    private lateinit var passwordsAdapter: PasswordsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVaultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = PasswordsDatabaseHelper(this)
        passwordsAdapter = PasswordsAdapter(db.getAllPass(), this)

        binding.passRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.passRecyclerView.adapter = passwordsAdapter

        binding.addBtn.setOnClickListener {
            val intent = Intent(this, AddPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        passwordsAdapter.refreshData(db.getAllPass())
    }
}