package com.lorbke.ft_hangouts.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

// The only class allowed to run SQL for contacts. Activities call these
// methods instead of touching SQLite (or DbHelper) directly.
class ContactRepository(context: Context) {

    private val dbHelper = DbHelper(context)

    fun getAll(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "contact", null, null, null, null, null,
            "first_name ASC"
        )
        // cursor.use{} closes the cursor automatically when the block ends,
        // even if something inside throws - like try/finally, but built in.
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

    // Returns the id SQLite assigned to the new row.
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

    // Used by the sms layer to match an incoming message's sender number
    // against a saved contact. Loads everything and filters in Kotlin with
    // PhoneUtils - simple, and plenty fast for a personal contact list.
    fun findByPhoneNumber(number: String): Contact? {
        return getAll().firstOrNull { PhoneUtils.matches(it.phoneNumber, number) }
    }

    // Bonus behaviour: an unknown sender becomes a new contact, named after
    // their number.
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

    // ContentValues is Android's key/value bag for one row going INTO the database.
    private fun contactToValues(contact: Contact): ContentValues {
        val values = ContentValues()
        values.put("first_name", contact.firstName)
        values.put("last_name", contact.lastName)
        values.put("phone_number", contact.phoneNumber)
        values.put("email", contact.email)
        values.put("address", contact.address)
        // ContentValues.put accepts a null String fine - stores SQL NULL.
        values.put("photo_uri", contact.photoUri)
        return values
    }

    // The reverse direction: one row coming OUT of a query result.
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
