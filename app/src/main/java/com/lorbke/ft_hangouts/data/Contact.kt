package com.lorbke.ft_hangouts.data

// Plain holder for one contact's data - like a C struct. Mirrors a row in the
// "contact" table (see DbHelper). id is 0 for a contact that hasn't been
// saved yet; SQLite assigns the real id on insert.
data class Contact(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val email: String,
    val address: String,
    // Path to a copy of the photo in our own private storage, or null if the
    // contact has none. Defaulted to null so existing code that builds a
    // Contact without a photo doesn't need to change.
    val photoUri: String? = null
) {
    // companion object = property tied to the class, not one instance of the class
    // A static const value attached to the class, like a shared #define.
    companion object {
        // Intent extra key used to pass a contact's id between screens.
        // A static const value attached to the class, like a shared #define.
        const val EXTRA_ID = "contact_id"
    }
}
