package com.lorbke.ft_hangouts.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lorbke.ft_hangouts.R
import com.lorbke.ft_hangouts.data.Contact
import com.lorbke.ft_hangouts.data.PhotoStorage

// RecyclerView does not know how to draw a Contact. This adapter is the bridge:
// it turns each Contact in the list into one row (item_contact.xml) on screen.
// Think of it as three callbacks the RecyclerView calls for you, similar to
// how you'd pass function pointers to a generic "for each visible row" loop.
//
// onContactClick is a variable that HOLDS A FUNCTION, similar to a function
// pointer in C. Its type "(Contact) -> Unit" means: takes a Contact, returns
// nothing. The Activity that creates this adapter decides what that function
// actually does (see ContactListActivity).
class ContactAdapter(
    private val contacts: List<Contact>,
    private val onContactClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    // Holds the two TextViews of one row so we only call findViewById once per row,
    // not once per scroll frame.
    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val photoImage: ImageView = view.findViewById(R.id.contactPhoto)
        val nameText: TextView = view.findViewById(R.id.contactName)
        val phoneText: TextView = view.findViewById(R.id.contactPhone)
    }

    // Called when the RecyclerView needs a brand new (empty) row to reuse later.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    // Called to fill one row's views with the contact at the given position.
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.nameText.text = contact.firstName + " " + contact.lastName
        holder.phoneText.text = contact.phoneNumber
        PhotoStorage.showInto(holder.photoImage, contact.photoUri, 96)

        // Tapping anywhere on the row calls the function we were given.
        holder.itemView.setOnClickListener {
            onContactClick(contact)
        }
    }

    // Tells the RecyclerView how many rows exist in total.
    override fun getItemCount(): Int {
        return contacts.size
    }
}
