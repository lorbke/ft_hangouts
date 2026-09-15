package com.lorbke.ft_hangouts.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.lorbke.ft_hangouts.R
import com.lorbke.ft_hangouts.data.Contact
import com.lorbke.ft_hangouts.data.ContactRepository
import com.lorbke.ft_hangouts.data.PhotoStorage

// One screen, two jobs: creating a brand new contact, or editing an existing
// one. If the Intent that started us carries a contact id that actually
// exists in the database, we are in "edit" mode and pre-fill the fields;
// otherwise we start blank ("create" mode).
class ContactFormActivity : AppCompatActivity() {

    // Path to the photo (in our own storage) that will be saved with this
    // contact - null means no photo. Needs to be a class property because it
    // is set from inside the pickPhoto callback, then read later when Save
    // is tapped - two different points in time, not one straight-line call.
    private var selectedPhotoPath: String? = null

    // Wires up the system photo picker. Like the SMS permission launcher in
    // ContactListActivity, this must be registered unconditionally before
    // the screen is shown, so it lives here as a property, not inside onCreate.
    private val pickPhoto =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                selectedPhotoPath = PhotoStorage.copyToAppStorage(this, uri)
                PhotoStorage.showInto(findViewById(R.id.photoPreview), selectedPhotoPath, 240)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_form)

        val contactRepository = ContactRepository(this)
        val contactId = intent.getLongExtra(Contact.EXTRA_ID, 0)
        val existingContact = if (contactId != 0L) contactRepository.getById(contactId) else null
        val isEditMode = existingContact != null

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(if (isEditMode) R.string.edit_contact else R.string.add_contact)

        val firstNameInput = findViewById<TextInputEditText>(R.id.firstNameInput)
        val lastNameInput = findViewById<TextInputEditText>(R.id.lastNameInput)
        val phoneInput = findViewById<TextInputEditText>(R.id.phoneInput)
        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        val addressInput = findViewById<TextInputEditText>(R.id.addressInput)
        val photoPreview = findViewById<ImageView>(R.id.photoPreview)

        if (existingContact != null) {
            firstNameInput.setText(existingContact.firstName)
            lastNameInput.setText(existingContact.lastName)
            phoneInput.setText(existingContact.phoneNumber)
            emailInput.setText(existingContact.email)
            addressInput.setText(existingContact.address)
            selectedPhotoPath = existingContact.photoUri
            PhotoStorage.showInto(photoPreview, selectedPhotoPath, 240)
        }

        findViewById<MaterialCardView>(R.id.photoCard).setOnClickListener {
            pickPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        findViewById<MaterialButton>(R.id.choosePhotoButton).setOnClickListener {
            pickPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        findViewById<MaterialButton>(R.id.saveButton).setOnClickListener {
            val contact = Contact(
                id = existingContact?.id ?: 0,
                firstName = firstNameInput.text.toString(),
                lastName = lastNameInput.text.toString(),
                phoneNumber = phoneInput.text.toString(),
                email = emailInput.text.toString(),
                address = addressInput.text.toString(),
                photoUri = selectedPhotoPath
            )

            if (isEditMode) {
                contactRepository.update(contact)
            } else {
                contactRepository.insert(contact)
            }

            Toast.makeText(this, R.string.contact_saved, Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
