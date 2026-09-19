package com.lorbke.ft_hangouts.ui

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lorbke.ft_hangouts.R
import com.lorbke.ft_hangouts.data.Contact
import com.lorbke.ft_hangouts.data.ContactRepository
import com.lorbke.ft_hangouts.data.Prefs
import java.util.Date

// home screen
class ContactListActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var contactList: RecyclerView
    private lateinit var contactRepository: ContactRepository
    private val requestSmsPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)

        contactRepository = ContactRepository(this)

        requestSmsPermissions.launch(
            arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS)
        )

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setBackgroundColor(Prefs.getHeaderColor(this))

        contactList = findViewById(R.id.contactList)
        contactList.layoutManager = LinearLayoutManager(this)

        val addContactButton = findViewById<FloatingActionButton>(R.id.addContactButton)
        addContactButton.setOnClickListener {
            startActivity(Intent(this, ContactFormActivity::class.java))
        }
    }

    // runs when screen is visited again and also after onCreate
    override fun onResume() {
        super.onResume()
        val contacts = contactRepository.getAll()
        // trailing lambda here (the in-line function definition) is taken by the ContactAdapter constructer as the onContactClick parameter
        contactList.adapter = ContactAdapter(contacts) { contact ->
            val intent = Intent(this, ContactDetailActivity::class.java)
            intent.putExtra(Contact.EXTRA_ID, contact.id)
            startActivity(intent)
        }

        val backgroundTimestamp = Prefs.consumeBackgroundTimestamp(this)
        if (backgroundTimestamp != null) {
            val time = DateFormat.getTimeFormat(this).format(Date(backgroundTimestamp))
            Toast.makeText(this, getString(R.string.backgrounded_at, time), Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.contact_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_header_color) {
            showHeaderColorPicker()
            return true
        }
        if (item.itemId == R.id.action_change_language) {
            showLanguagePicker()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showHeaderColorPicker() {
        val colorNames = arrayOf("Purple", "Teal", "Orange", "Red")
        val colorValues = intArrayOf(
            Color.parseColor("#6750A4"),
            Color.parseColor("#00796B"),
            Color.parseColor("#EF6C00"),
            Color.parseColor("#C62828")
        )

        AlertDialog.Builder(this)
            .setTitle(R.string.change_header_color)
            .setItems(colorNames) { _, which ->
                Prefs.setHeaderColor(this, colorValues[which])
                toolbar.setBackgroundColor(colorValues[which])
            }
            .show()
    }

    private fun showLanguagePicker() {
        val languageNames = arrayOf("English", "Deutsch")
        val languageTags = arrayOf("en", "de")

        AlertDialog.Builder(this)
            .setTitle(R.string.change_language)
            .setItems(languageNames) { _, which ->
                val locales = LocaleListCompat.forLanguageTags(languageTags[which])
                AppCompatDelegate.setApplicationLocales(locales)
            }
            .show()
    }
}
