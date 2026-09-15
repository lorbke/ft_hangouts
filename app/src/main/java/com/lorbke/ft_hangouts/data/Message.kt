package com.lorbke.ft_hangouts.data

// One text message in a conversation. Mirrors a row in the "message" table.
// contactId says which contact this belongs to; isIncoming says which side
// of the screen to draw it on (received vs. sent).
data class Message(
    val id: Long,
    val contactId: Long,
    val body: String,
    val timestamp: Long,
    val isIncoming: Boolean
)
