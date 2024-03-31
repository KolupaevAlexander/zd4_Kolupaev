package com.example.prakt_19_1

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DBHelper (val context: Context): SQLiteOpenHelper (context,"crimeDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        val quary = "CREATE TABLE crimeDB (id INT PRIMARY KEY, title TEXT, date TEXT, suspend TEXT)"
        db!!.execSQL(quary)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS crimeDB")
        onCreate(db)
    }

    fun addCrime(crime: Crime)
    {
        val values = ContentValues()
        values.put("title", crime.title)
        values.put("date", crime.date)
        values.put("suspend", crime.suspend)
        val db = this.writableDatabase
        db.insert("crimeDB", null, values)
        db.close()
    }

    fun deleteAllCrimes() {
        val db = writableDatabase
        db.delete("crimeDB", null, null)
    }

    @SuppressLint("Range")
    fun readAllCrimes(): ArrayList<Crime> {
        val crimes = ArrayList<Crime>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from crimeDB", null)
        } catch (e: SQLiteException) {
            return ArrayList()
        }
        var title: String
        var date: String
        var suspend: String
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                title = cursor.getString(cursor.getColumnIndex(DBContract.CrimesEntry.TITLE))
                date = cursor.getString(cursor.getColumnIndex(DBContract.CrimesEntry.DATE))
                suspend = cursor.getString(cursor.getColumnIndex(DBContract.CrimesEntry.SUSPEND))
                crimes.add(Crime(title, date, suspend))
                cursor.moveToNext()
            }
        }
        return crimes
    }

    fun getCrime(title: String):Boolean //в функцию передается пароль и логин
    {
        val db = this.readableDatabase
        // поиск через запрос
        val result = db.rawQuery("SELECT * FROM crimeDB WHERE title = '$title'",null)
        //выводим  true или false
        return result.moveToFirst()
    }

}