package com.lorbke.ft_hangouts.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class ContactRepository(context: Context) {

    private val dbHelper = DbHelper(context)

    fun getAll(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "contact", null, null, null, null, null,
            "first_name ASC"
        )
        // cursor.use{} closes the cursor (and the db resource) automatically when the block ends, even if something inside throws
        cursor.use {
            while (it.moveToNext()) {
                contacts.add(contactFromCursor(it))
            }
        }
        return contacts
    }

    fun getById(id: Long): Contact? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "contact", null, "_id = ?", arrayOf(id.toString()), null, null, null
        )
        cursor.use {
            if (it.moveToFirst()) {
                return contactFromCursor(it)
            }
        }
        return null
    }

    fun insert(contact: Contact): Long {
        val db = dbHelper.writableDatabase
        return db.insert("contact", null, contactToValues(contact))
    }

    fun update(contact: Contact) {
        val db = dbHelper.writableDatabase
        db.update("contact", contactToValues(contact), "_id = ?", arrayOf(contact.id.toString()))
    }

    fun delete(id: Long) {
        val db = dbHelper.writableDatabase
        db.delete("contact", "_id = ?", arrayOf(id.toString()))
    }

    fun findByPhoneNumber(number: String): Contact? {
        return getAll().firstOrNull { PhoneUtils.matches(it.phoneNumber, number) }
    }

    // bonus shit
    fun findOrCreateByPhoneNumber(number: String): Contact {
        val existing = findByPhoneNumber(number)
        if (existing != null) {
            return existing
        }
        val newContact = Contact(
            id = 0,
            firstName = number,
            lastName = "",
            phoneNumber = number,
            email = "",
            address = ""
        )
        val newId = insert(newContact)
        return newContact.copy(id = newId)
    }

    private fun contactToValues(contact: Contact): ContentValues {
        val values = ContentValues()
        values.put("first_name", contact.firstName)
        values.put("last_name", contact.lastName)
        values.put("phone_number", contact.phoneNumber)
        values.put("email", contact.email)
        values.put("address", contact.address)
        values.put("photo_uri", contact.photoUri)
        return values
    }

    private fun contactFromCursor(cursor: Cursor): Contact {
        val photoIndex = cursor.getColumnIndexOrThrow("photo_uri")
        return Contact(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("_id")),
            firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name")),
            lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name")),
            phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("phone_number")),
            email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
            address = cursor.getString(cursor.getColumnIndexOrThrow("address")),
            photoUri = if (cursor.isNull(photoIndex)) null else cursor.getString(photoIndex)
        )
    }
}
