package com.example.prakt_19_1

import android.provider.BaseColumns

object DBContract {

    class CrimesEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "crimeDB"
            val TITLE = "title"
            var DATE = "date"
            var SUSPEND = "suspend"
        }
    }
}