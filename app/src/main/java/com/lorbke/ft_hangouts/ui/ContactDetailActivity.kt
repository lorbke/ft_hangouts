package com.lorbke.ft_hangouts.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.lorbke.ft_hangouts.R
import com.lorbke.ft_hangouts.data.Contact
import com.lorbke.ft_hangouts.data.ContactRepository
import com.lorbke.ft_hangouts.data.PhotoStorage
import com.lorbke.ft_hangouts.data.Prefs

class ContactDetailActivity : AppCompatActivity() {

    private lateinit var contactRepository: ContactRepository
    private lateinit var contact: Contact
    private var contactId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)

        contactRepository = ContactRepository(this)
        // retrieves whatever ContactListActivity stored under that key before starting this activity
        contactId = intent.getLongExtra(Contact.EXTRA_ID, 0)

        findViewById<MaterialButton>(R.id.editButton).setOnClickListener {
            val editIntent = Intent(this, ContactFormActivity::class.java)
            editIntent.putExtra(Contact.EXTRA_ID, contactId)
            startActivity(editIntent)
        }

        findViewById<MaterialButton>(R.id.deleteButton).setOnClickListener {
            contactRepository.delete(contactId)
            Toast.makeText(this, R.string.contact_deleted, Toast.LENGTH_SHORT).show()
            finish()
        }

        findViewById<MaterialButton>(R.id.messageButton).setOnClickListener {
            val messageIntent = Intent(this, ConversationActivity::class.java)
            messageIntent.putExtra(Contact.EXTRA_ID, contactId)
            startActivity(messageIntent)
        }

        findViewById<MaterialButton>(R.id.callButton).setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact.phoneNumber))
            startActivity(callIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        val loadedContact = contactRepository.getById(contactId)
        if (loadedContact == null) {
            finish()
            return
        }
        contact = loadedContact

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = contact.firstName + " " + contact.lastName
        toolbar.setBackgroundColor(Prefs.getHeaderColor(this))

        PhotoStorage.showPhoto(findViewById<ImageView>(R.id.detailPhoto), contact.photoUri, 240)
        findViewById<TextView>(R.id.detailPhone).text = contact.phoneNumber
        findViewById<TextView>(R.id.detailEmail).text = contact.email
        findViewById<TextView>(R.id.detailAddress).text = contact.address
    }
}
