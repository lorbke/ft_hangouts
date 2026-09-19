package com.lorbke.ft_hangouts.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class MessageRepository(context: Context) {

    private val dbHelper = DbHelper(context)

    fun getByContact(contactId: Long): List<Message> {
        val messages = mutableListOf<Message>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "message", null, "contact_id = ?", arrayOf(contactId.toString()), null, null,
            "timestamp ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                messages.add(messageFromCursor(it))
            }
        }
        return messages
    }

    fun insert(message: Message): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues()
        values.put("contact_id", message.contactId)
        values.put("body", message.body)
        values.put("timestamp", message.timestamp)
        // SQLite has no boolean type
        values.put("direction", if (message.isIncoming) 0 else 1)
        return db.insert("message", null, values)
    }

    private fun messageFromCursor(cursor: Cursor): Message {
        return Message(
            contactId = cursor.getLong(cursor.getColumnIndexOrThrow("contact_id")),
            body = cursor.getString(cursor.getColumnIndexOrThrow("body")),
            timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")),
            isIncoming = cursor.getInt(cursor.getColumnIndexOrThrow("direction")) == 0
        )
    }
}
