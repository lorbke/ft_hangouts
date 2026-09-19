package com.lorbke.ft_hangouts.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        // Required for "ON DELETE CASCADE" below to actually delete a
        // contact's messages when the contact itself is deleted.
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE contact (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                first_name TEXT NOT NULL,
                last_name TEXT NOT NULL,
                phone_number TEXT NOT NULL,
                email TEXT NOT NULL,
                address TEXT NOT NULL,
                photo_uri TEXT
            )
            """
        )
        db.execSQL(
            """
            CREATE TABLE message (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                contact_id INTEGER NOT NULL,
                body TEXT NOT NULL,
                timestamp INTEGER NOT NULL,
                direction INTEGER NOT NULL,
                FOREIGN KEY(contact_id) REFERENCES contact(_id) ON DELETE CASCADE
            )
            """
        )

        // some mock data
        db.execSQL(
            "INSERT INTO contact (first_name, last_name, phone_number, email, address) " +
                "VALUES ('Ada', 'Lovelace', '+49 151 00000001', 'ada@example.com', '1 Analytical Engine Rd')"
        )
        db.execSQL(
            "INSERT INTO contact (first_name, last_name, phone_number, email, address) " +
                "VALUES ('Alan', 'Turing', '+49 151 00000002', 'alan@example.com', '2 Bletchley Park Way')"
        )
        db.execSQL(
            "INSERT INTO contact (first_name, last_name, phone_number, email, address) " +
                "VALUES ('Grace', 'Hopper', '+49 151 00000003', 'grace@example.com', '3 Compiler Ave')"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS message")
        db.execSQL("DROP TABLE IF EXISTS contact")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "ft_hangouts.db"
        private const val DATABASE_VERSION = 2
    }
}
