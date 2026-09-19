package com.lorbke.ft_hangouts.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.lorbke.ft_hangouts.R
import com.lorbke.ft_hangouts.data.Contact
import com.lorbke.ft_hangouts.data.ContactRepository
import com.lorbke.ft_hangouts.data.Message
import com.lorbke.ft_hangouts.data.MessageRepository
import com.lorbke.ft_hangouts.data.Prefs
import com.lorbke.ft_hangouts.sms.SmsSender

class ConversationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        val contactRepository = ContactRepository(this)
        val messageRepository = MessageRepository(this)

        val contactId = intent.getLongExtra(Contact.EXTRA_ID, 0)
        val contact = contactRepository.getById(contactId) ?: return

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = contact.firstName + " " + contact.lastName
        toolbar.setBackgroundColor(Prefs.getHeaderColor(this))

        val messages = messageRepository.getByContact(contactId).toMutableList()
        val adapter = MessageAdapter(messages)

        val messageList = findViewById<RecyclerView>(R.id.messageList)
        messageList.layoutManager = LinearLayoutManager(this)
        messageList.adapter = adapter

        val messageInput = findViewById<EditText>(R.id.messageInput)
        findViewById<MaterialButton>(R.id.sendButton).setOnClickListener {
            val text = messageInput.text.toString().trim()
            val hasSendPermission = ContextCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED

            if (text.isNotEmpty()) {
                if (hasSendPermission) {
                    SmsSender.send(contact.phoneNumber, text)

                    val newMessage = Message(
                        contactId = contactId,
                        body = text,
                        timestamp = System.currentTimeMillis(),
                        isIncoming = false
                    )
                    messageRepository.insert(newMessage)
                    messages.add(newMessage)

                    adapter.notifyItemInserted(messages.size - 1)
                    messageList.scrollToPosition(messages.size - 1)
                    messageInput.setText("")
                } else {
                    Toast.makeText(this, R.string.sms_permission_required, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
