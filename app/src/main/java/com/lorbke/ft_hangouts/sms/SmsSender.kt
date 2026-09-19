package com.lorbke.ft_hangouts.sms

import android.telephony.SmsManager

object SmsSender {

    fun send(phoneNumber: String, body: String) {
        // getDefault() still works and simpler than newer options
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, body, null, null)
    }
}
