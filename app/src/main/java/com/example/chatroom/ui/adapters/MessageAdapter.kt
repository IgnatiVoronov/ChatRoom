package com.example.chatroom.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatroom.data.Message
import com.example.chatroom.databinding.ReceiveLayoutBinding
import com.example.chatroom.databinding.SendLayoutBinding
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, private val messageList: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_RECEIVE = 1
        const val ITEM_SENT = 2
    }

    class ReceiveViewHolder(binding: ReceiveLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val receiveMessage = binding.receiveMessageTextView
    }

    class SendViewHolder(binding: SendLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val sentMessage = binding.sentMessageTextView
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        return if (viewType == ITEM_RECEIVE) {
            //inflate receive
            val binding =
                ReceiveLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ReceiveViewHolder(binding)
        } else {
            //inflate sent
            val binding =
                SendLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SendViewHolder(binding)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if (holder.javaClass == SendViewHolder::class.java) {
            //logic for sent view holder
            holder as SendViewHolder
            holder.sentMessage.text = currentMessage.message
        } else {
            //logic for receive view holder
            holder as ReceiveViewHolder
            holder.receiveMessage.text = currentMessage.message
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]

        return if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)) {
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }


}