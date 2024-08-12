package com.example.passwordgenerator

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import java.io.FileOutputStream


class VaultActivity : AppCompatActivity() {
    private val REQUEST_CODE_PICK_FILE = 1
    private lateinit var binding: ActivityVaultBinding
    private lateinit var db: PasswordsDatabaseHelper
    private lateinit var passwordsAdapter: PasswordsAdapter
    private lateinit var databaseBackup: DatabaseBackup
    private val databaseRestore = RestoreDatabaseBackup(this)


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

        binding.restoreBtn.setOnClickListener {
            openFilePicker()

        }

        // Configurar um botão para abrir o File Picker
        binding.restoreBtn.setOnLongClickListener {
            restoreBackup()
            true
        }
    }
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*" // Ou defina um tipo específico se necessário
        }
        startActivityForResult(intent, REQUEST_CODE_PICK_FILE)
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

    private fun refreshData() {
        // Feche e reabra o banco de dados para garantir que os dados estejam atualizados
        db.close()
        db = PasswordsDatabaseHelper(this)
        passwordsAdapter.refreshData(db.getAllPass())
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val file = getFileFromUri(uri)
                file?.let {
                    // Restaurar o banco de dados
                    if (databaseRestore.restoreDatabase(it)) {
                        refreshData()
                        Toast.makeText(this, "Restauração bem-sucedida", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Falha na restauração", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        val inputStream = this.contentResolver.openInputStream(uri)
        val tempFile = File(this.cacheDir, "temp_backup.db")
        inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        return if (tempFile.exists()) tempFile else null
    }

    private fun restoreBackup() {
        val backupFile = File(getExternalFilesDir(null), "pwdgenbackup/vaultpass.db")
        if (databaseRestore.restoreDatabase(backupFile)) {
            Toast.makeText(this, "Restauração bem-sucedida", Toast.LENGTH_SHORT).show()
            refreshData()
        } else {
            Toast.makeText(this, "Falha na restauração", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        passwordsAdapter.refreshData(db.getAllPass())
    }
}