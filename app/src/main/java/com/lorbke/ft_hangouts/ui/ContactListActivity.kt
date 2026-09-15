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

// The home screen: shows every contact as a list, read from SQLite.
class ContactListActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var contactList: RecyclerView
    private lateinit var contactRepository: ContactRepository

    // This wires up the system's "Allow SMS permission?" dialog. Registering
    // it has to happen unconditionally, before the screen is shown - so it
    // lives here as a property, not inside onCreate. The {} at the end is the
    // callback for when the user answers; we don't need to react, so it's empty.
    private val requestSmsPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)

        contactRepository = ContactRepository(this)

        // Ask once, up front, for both SMS permissions the app needs.
        // If the user already granted them, this is a silent no-op.
        requestSmsPermissions.launch(
            arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS)
        )

        toolbar = findViewById(R.id.toolbar)
        // Registers our Toolbar as the screen's action bar, which is what makes
        // onCreateOptionsMenu / onOptionsItemSelected below get called.
        setSupportActionBar(toolbar)
        // Apply whatever header color was saved last time (or the default).
        toolbar.setBackgroundColor(Prefs.getHeaderColor(this))

        contactList = findViewById(R.id.contactList)
        contactList.layoutManager = LinearLayoutManager(this)

        val addContactButton = findViewById<FloatingActionButton>(R.id.addContactButton)
        addContactButton.setOnClickListener {
            // No id extra -> ContactFormActivity opens in "create" mode.
            startActivity(Intent(this, ContactFormActivity::class.java))
        }
    }

    // Runs every time this screen becomes visible again - including the very
    // first time (onResume always follows onCreate) and after returning from
    // adding/editing/deleting a contact on another screen.
    override fun onResume() {
        super.onResume()
        val contacts = contactRepository.getAll()
        // trailing lambda here (the in-line function definition) is taken by the ContactAdapter constructer as the onContactClick parameter
        contactList.adapter = ContactAdapter(contacts) { contact ->
            val intent = Intent(this, ContactDetailActivity::class.java)
            intent.putExtra(Contact.EXTRA_ID, contact.id)
            startActivity(intent)
        }

        // If the whole app was backgrounded since we last checked (tracked
        // by FtHangoutsApp), show when. Returns null on a normal first
        // launch, or if we already showed this same background period.
        val backgroundTimestamp = Prefs.consumeBackgroundTimestamp(this)
        if (backgroundTimestamp != null) {
            val time = DateFormat.getTimeFormat(this).format(Date(backgroundTimestamp))
            Toast.makeText(this, getString(R.string.backgrounded_at, time), Toast.LENGTH_LONG).show()
        }
    }

    // Builds the three-dot overflow menu.
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.contact_list_menu, menu)
        return true
    }

    // Called when a menu item is tapped.
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
        // Language names are shown in their own language, not translated -
        // the same way every app's language picker does it.
        val languageNames = arrayOf("English", "Deutsch")
        val languageTags = arrayOf("en", "de")

        AlertDialog.Builder(this)
            .setTitle(R.string.change_language)
            .setItems(languageNames) { _, which ->
                // Overrides the app's language independently of the device's
                // system language. AppCompatDelegate saves this choice for us
                // (it survives restarts on its own) and recreates this screen
                // to apply it immediately.
                val locales = LocaleListCompat.forLanguageTags(languageTags[which])
                AppCompatDelegate.setApplicationLocales(locales)
            }
            .show()
    }
}
