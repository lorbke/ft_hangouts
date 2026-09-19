package com.lorbke.ft_hangouts.data

data class Message(
    val contactId: Long,
    val body: String,
    val timestamp: Long,
    val isIncoming: Boolean
)
