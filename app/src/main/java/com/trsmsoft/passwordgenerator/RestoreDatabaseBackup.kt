package com.trsmsoft.passwordgenerator

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
        // Verifica se o arquivo de backup tem a extensão correta
        if (!isValidDatabaseFile(backupFile)) {
            Log.e(TAG, "Arquivo selecionado não é um banco de dados válido (.db)")
            return false
        }

        val currentDB = context.getDatabasePath(databaseName)

        return try {
            // Fechar todas as conexões com o banco de dados atual
            context.deleteDatabase(databaseName)

            // Restaurar o banco de dados a partir do backup
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

    private fun isValidDatabaseFile(file: File): Boolean {
        // Verifica se o arquivo tem a extensão ".db"
        return file.extension == "db"
    }

    fun getLatestBackup(): File? {
        val backupDir = File(context.getExternalFilesDir(null), "pwdgenbackup")
        if (!backupDir.exists() || !backupDir.isDirectory) {
            Log.e(TAG, "Diretório de backup não encontrado")
            return null
        }

        // Filtra arquivos que terminam com ".db"
        return backupDir.listFiles { file ->
            file.extension == "db"
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