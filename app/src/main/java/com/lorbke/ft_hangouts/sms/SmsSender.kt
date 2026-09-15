package com.lorbke.ft_hangouts.sms

import android.content.Context
import android.telephony.SmsManager
import com.lorbke.ft_hangouts.data.Message
import com.lorbke.ft_hangouts.data.MessageRepository

// The only class allowed to send a text. Wraps Android's SmsManager and logs
// what was sent to SQLite in the same call, so the two never get out of sync.
class SmsSender(context: Context) {

    private val messageRepository = MessageRepository(context)

    // Returns the saved Message (with the id SQLite assigned) so the caller
    // can show it immediately without re-querying the whole conversation.
    fun send(phoneNumber: String, contactId: Long, body: String): Message {
        // getDefault() is the classic, works-on-every-API way to get an
        // SmsManager. Android 12+ offers a newer Context-based lookup, but
        // getDefault() still works fine and needs no minSdk-based branching.
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, body, null, null)

        val message = Message(
            id = 0,
            contactId = contactId,
            body = body,
            timestamp = System.currentTimeMillis(),
            isIncoming = false
        )
        val newId = messageRepository.insert(message)
        return message.copy(id = newId)
    }
}
