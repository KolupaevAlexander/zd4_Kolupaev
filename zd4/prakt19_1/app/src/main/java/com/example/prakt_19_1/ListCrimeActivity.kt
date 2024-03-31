package com.example.prakt_19_1

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

lateinit var list: LinearLayout
lateinit var helper: DBHelper
class ListCrimeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_crime)
        list = findViewById(R.id.listView)
        loadListView()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_top, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addCrime -> {
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.delAll -> {
               delCrimes()
                loadListView()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun delCrimes()
    {
        val db = DBHelper (this)
        db.deleteAllCrimes()
        loadListView()
    }

    fun loadListView()
    {
        helper = DBHelper(this)
        var array = helper.readAllCrimes()
        list.removeAllViews()
        array.forEach {
            var crimes_tv = TextView(this)
            crimes_tv.textSize = 20F
            crimes_tv.text = "${it.title.toString()} - ${it.date.toString()}"
            list.addView(crimes_tv)
        }
    }
}