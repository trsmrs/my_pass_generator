package com.example.passwordgenerator

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class DatabaseBackup(private val context: Context) {
    private val databaseName = "vaultpass.db"
    private val TAG = "DatabaseBackup"


    fun backupDatabase(): File? {
        try {
            val currentDB = context.getDatabasePath(databaseName)
            val backupDir = File(context.getExternalFilesDir(null), "pwdgenbackup")
//            val backupDir = File(Environment.DIRECTORY_DOWNLOADS)
            if (!backupDir.exists()) {
                if (!backupDir.mkdirs()) {
                    Log.e(TAG, "Falha ao criar diretório de backup")
                    return null
                }
            }

//            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

            val backupFile = File(backupDir, databaseName)
            if (backupDir.usableSpace < currentDB.length()) {
                Log.e(TAG, "Espaço insuficiente para backup")
                return null
            }

            FileInputStream(currentDB).use { input ->
                FileOutputStream(backupFile).use { output ->
                    input.copyTo(output)
                }
            }

            Log.i(TAG, "Backup realizado com sucesso: ${backupFile.absolutePath}")
            return backupFile
        } catch (e: IOException) {
            Log.e(TAG, "Erro ao realizar backup", e)
            return null
        }
    }

    fun shareBackup(backupFile: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", backupFile)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "application/octet-stream"
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(shareIntent, "Compartilhar backup"))
    }
}