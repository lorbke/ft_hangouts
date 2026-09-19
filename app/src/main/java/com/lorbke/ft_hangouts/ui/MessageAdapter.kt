package com.lorbke.ft_hangouts.ui

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lorbke.ft_hangouts.R
import com.lorbke.ft_hangouts.data.Message

class MessageAdapter(private val messages: MutableList<Message>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bodyText: TextView = view.findViewById(R.id.messageBody)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bodyText.text = message.body

        val layoutParams = holder.bodyText.layoutParams as FrameLayout.LayoutParams
        if (message.isIncoming) {
            layoutParams.gravity = Gravity.START
            holder.bodyText.setBackgroundResource(R.drawable.bubble_incoming)
            holder.bodyText.setTextColor(0xFF1D1B20.toInt())
        } else {
            layoutParams.gravity = Gravity.END
            holder.bodyText.setBackgroundResource(R.drawable.bubble_outgoing)
            holder.bodyText.setTextColor(0xFFFFFFFF.toInt())
        }
        holder.bodyText.layoutParams = layoutParams
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}
