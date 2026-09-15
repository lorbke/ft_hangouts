package com.lorbke.ft_hangouts.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.lorbke.ft_hangouts.data.ContactRepository
import com.lorbke.ft_hangouts.data.Message
import com.lorbke.ft_hangouts.data.MessageRepository

// Declared in AndroidManifest.xml with an intent-filter for SMS_RECEIVED.
// The OS constructs this class and calls onReceive() whenever a text arrives
// - even if ft_hangouts is not open. There is no "our app" running yet at
// that point, so everything this needs (the repositories) is created fresh
// from the Context the OS hands us.
class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val contactRepository = ContactRepository(context)
        val messageRepository = MessageRepository(context)

        // Telephony.Sms.Intents does the raw PDU parsing for us and just
        // hands back ready-to-use SmsMessage objects.
        val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

        for (sms in smsMessages) {
            val sender = sms.originatingAddress ?: continue
            val contact = contactRepository.findOrCreateByPhoneNumber(sender)

            messageRepository.insert(
                Message(
                    id = 0,
                    contactId = contact.id,
                    body = sms.messageBody,
                    timestamp = System.currentTimeMillis(),
                    isIncoming = true
                )
            )
        }
    }
}
