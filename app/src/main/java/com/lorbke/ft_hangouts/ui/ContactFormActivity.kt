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
import com.lorbke.ft_hangouts.data.Prefs

// screen for creating or editing a contact
class ContactFormActivity : AppCompatActivity() {

    private var selectedPhotoPath: String? = null

    private val pickPhoto =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                selectedPhotoPath = PhotoStorage.copyToAppStorage(this, uri)
                PhotoStorage.showPhoto(findViewById(R.id.photoPreview), selectedPhotoPath, 240)
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
        toolbar.setBackgroundColor(Prefs.getHeaderColor(this))

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
            PhotoStorage.showPhoto(photoPreview, selectedPhotoPath, 240)
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
