package com.lorbke.ft_hangouts.data

data class Contact(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val email: String,
    val address: String,
    val photoUri: String? = null
) {
    // companion object = property tied to the class, not one instance of the class
    // A static const value attached to the class, like a shared #define.
    companion object {
        const val EXTRA_ID = "contact_id"
    }
}
