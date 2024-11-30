// DatabaseHelper.kt
package com.example.epiapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "epi_db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "EPIs"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NOME = "nome"
        private const val COLUMN_QUANTIDADE = "quantidade"
        private const val COLUMN_DESCRICAO = "descricao"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_NOME TEXT, "
                + "$COLUMN_QUANTIDADE INTEGER, "
                + "$COLUMN_DESCRICAO TEXT)")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Atualizações futuras do banco de dados
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addEpi(epi: Epi): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_NOME, epi.nome)
        contentValues.put(COLUMN_QUANTIDADE, epi.quantidade)
        contentValues.put(COLUMN_DESCRICAO, epi.descricao)

        val result = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return result != -1L
    }

    fun getAllEpis(): List<Epi> {
        val epis = ArrayList<Epi>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val epi = Epi(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    nome = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOME)),
                    quantidade = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTIDADE)),
                    descricao = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRICAO))
                )
                epis.add(epi)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return epis
    }

    fun updateEpi(epi: Epi): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_NOME, epi.nome)
        contentValues.put(COLUMN_QUANTIDADE, epi.quantidade)
        contentValues.put(COLUMN_DESCRICAO, epi.descricao)

        val result = db.update(TABLE_NAME, contentValues, "$COLUMN_ID = ?", arrayOf(epi.id.toString()))
        db.close()
        return result > 0
    }

    fun deleteEpi(id: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }
}
