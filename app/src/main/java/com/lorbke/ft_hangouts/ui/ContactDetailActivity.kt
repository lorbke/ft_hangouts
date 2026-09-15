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

// Shows one contact's details, looked up in SQLite by the id passed in the Intent.
class ContactDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)

        val contactRepository = ContactRepository(this)
        // retrieves whatever ContactListActivity stored under that key before starting this activity
        val contactId = intent.getLongExtra(Contact.EXTRA_ID, 0)
        // Bail out if the id is missing/invalid - nothing to show.
        val contact = contactRepository.getById(contactId) ?: return

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = contact.firstName + " " + contact.lastName

        PhotoStorage.showInto(findViewById<ImageView>(R.id.detailPhoto), contact.photoUri, 240)
        findViewById<TextView>(R.id.detailPhone).text = contact.phoneNumber
        findViewById<TextView>(R.id.detailEmail).text = contact.email
        findViewById<TextView>(R.id.detailAddress).text = contact.address

        findViewById<MaterialButton>(R.id.editButton).setOnClickListener {
            val editIntent = Intent(this, ContactFormActivity::class.java)
            editIntent.putExtra(Contact.EXTRA_ID, contact.id)
            startActivity(editIntent)
        }

        findViewById<MaterialButton>(R.id.deleteButton).setOnClickListener {
            contactRepository.delete(contact.id)
            Toast.makeText(this, R.string.contact_deleted, Toast.LENGTH_SHORT).show()
            finish()
        }

        findViewById<MaterialButton>(R.id.messageButton).setOnClickListener {
            val messageIntent = Intent(this, ConversationActivity::class.java)
            messageIntent.putExtra(Contact.EXTRA_ID, contact.id)
            startActivity(messageIntent)
        }

        findViewById<MaterialButton>(R.id.callButton).setOnClickListener {
            // Hands off to the system Dialer app - a real Android feature,
            // nothing mocked here.
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact.phoneNumber))
            startActivity(callIntent)
        }
    }
}
