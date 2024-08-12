package com.example.passwordgenerator

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PasswordsDatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "vaultpass.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "allpassword"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TITLE TEXT, $COLUMN_CONTENT TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertPassword(pass: Passwords){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, pass.title)
            put(COLUMN_CONTENT, pass.content)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }



    fun getAllPass(): List<Passwords>{
        val passwordsList = mutableListOf<Passwords>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        while(cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))

            val pass = Passwords(id, title, content)
            passwordsList.add(pass)
        }
        cursor.close()
        db.close()
        return passwordsList
    }

    fun updatePass(pass: Passwords){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, pass.title)
            put(COLUMN_CONTENT, pass.content)
        }
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(pass.id.toString())
        db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    fun getPassByID(passId: Int): Passwords{
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $passId"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))


        cursor.close()
        db.close()
        return Passwords(id, title, content)
    }

    fun deletePass(passId: Int){
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(passId.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }

}