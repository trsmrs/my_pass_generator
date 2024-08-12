package com.example.passwordgenerator

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class RestoreDatabaseBackup(private val context: Context) {

    private val databaseName = "vaultpass.db"
    private val TAG = "DatabaseRestore"

    fun restoreDatabase(backupFile: File): Boolean {
        val currentDB = context.getDatabasePath(databaseName)

        return try {
            // Fechar todas as conexões com o banco de dados atual
            context.deleteDatabase(databaseName)

            FileInputStream(backupFile).use { input ->
                FileOutputStream(currentDB).use { output ->
                    input.copyTo(output)
                }
            }

            Log.i(TAG, "Restauração realizada com sucesso de: ${backupFile.absolutePath}")
            true
        } catch (e: IOException) {
            Log.e(TAG, "Erro ao restaurar o banco de dados", e)
            false
        }
    }

    fun getLatestBackup(): File? {
        val backupDir = File(context.getExternalFilesDir(null), "pwdgenbackup")
        if (!backupDir.exists() || !backupDir.isDirectory) {
            Log.e(TAG, "Diretório de backup não encontrado")
            return null
        }

        return backupDir.listFiles { file ->
            file.name.startsWith(databaseName)
        }?.maxByOrNull { it.lastModified() }
    }

    fun restoreLatestBackup(): Boolean {
        val latestBackup = getLatestBackup()
        return if (latestBackup != null) {
            restoreDatabase(latestBackup)
        } else {
            Log.e(TAG, "Nenhum backup encontrado para restaurar")
            false
        }
    }
}