package com.lorbke.ft_hangouts.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.lorbke.ft_hangouts.data.ContactRepository
import com.lorbke.ft_hangouts.data.Message
import com.lorbke.ft_hangouts.data.MessageRepository

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val contactRepository = ContactRepository(context)
        val messageRepository = MessageRepository(context)

        val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

        for (sms in smsMessages) {
            val sender = sms.originatingAddress ?: continue
            val contact = contactRepository.findOrCreateByPhoneNumber(sender)

            messageRepository.insert(
                Message(
                    contactId = contact.id,
                    body = sms.messageBody,
                    timestamp = System.currentTimeMillis(),
                    isIncoming = true
                )
            )
        }
    }
}
