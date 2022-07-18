package com.edu.sdproject2

/*
 * CS3013 - Mobile App Dev. - Summer 2022
 * Instructor: Thyago Mota
 * Student(s):
 * Description: App 02 - DBHelper class
 */

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.Serializable
import java.text.SimpleDateFormat

class DBHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "BucketList.db"
        const val DATABASE_VERSION = 1
        val ISO_FORMAT = SimpleDateFormat("yyyy-MM-dd")
        val USA_FORMAT = SimpleDateFormat("MM/dd/yyyy")
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // create the table
        db?.execSQL("""
            CREATE TABLE bucketlist ( 
                description   TEXT PRIMARY KEY, 
                creation_date TEXT NOT NULL, 
                update_date   TEXT, 
                status        INTEGER NOT NULL)
        """)

        // populate the table with a few items
        db?.execSQL("""
            INSERT INTO bucketlist VALUES 
                ("complete a marathon in less than 4h", "2022-01-25", "2022-01-26", ${Item.SCHEDULED}),
                ("show your ID to a random person and ask: have you seen this person?", "2021-02-15", "2021-02-22", ${Item.COMPLETED}), 
                ("ride an escalator backwards", "2021-02-17", "2022-02-18", ${Item.ARCHIVED})
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // drop the table
        db?.execSQL("""
            DROP TABLE IF EXISTS bucketlist
        """)

        // then call "onCreate" again
        onCreate(db)
    }
}