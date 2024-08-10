package com.example.passwordgenerator

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passwordgenerator.databinding.ActivityVaultBinding
import java.io.File


class VaultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVaultBinding
    private lateinit var db: PasswordsDatabaseHelper
    private lateinit var passwordsAdapter: PasswordsAdapter
    private lateinit var databaseBackup: DatabaseBackup

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

        databaseBackup = DatabaseBackup(this)
        db = PasswordsDatabaseHelper(this)
        passwordsAdapter = PasswordsAdapter(db.getAllPass(), this)

        binding.passRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.passRecyclerView.adapter = passwordsAdapter

        binding.addBtn.setOnClickListener {
            val intent = Intent(this, AddPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.backupBtn.setOnClickListener {
            performBackup()
        }
    }

    override fun onResume() {
        super.onResume()
        passwordsAdapter.refreshData(db.getAllPass())
    }

    private fun performBackup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                return
            }
        }

        // Feche todas as conexões com o banco de dados
        db.close()

        val backupFile = databaseBackup.backupDatabase()
        if (backupFile != null) {
            Toast.makeText(this, "Backup realizado com sucesso", Toast.LENGTH_SHORT).show()
            // Perguntar ao usuário se deseja compartilhar o backup
            showShareBackupDialog(backupFile)
        } else {
            Toast.makeText(this, "Falha ao realizar o backup", Toast.LENGTH_SHORT).show()
        }

        // Reabra o banco de dados
        db = PasswordsDatabaseHelper(this)
    }

    private fun showShareBackupDialog(backupFile: File) {
        AlertDialog.Builder(this)
            .setTitle("Backup Concluído")
            .setMessage("Deseja compartilhar o arquivo de backup?")
            .setPositiveButton("Sim") { _, _ ->
                databaseBackup.shareBackup(backupFile)
            }
            .setNegativeButton("Não", null)
            .show()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            performBackup()
        } else {
            Toast.makeText(this, "Permissão negada. Não é possível fazer backup.", Toast.LENGTH_SHORT).show()
        }
    }
}