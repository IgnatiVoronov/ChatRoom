package com.example.chatroom.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatroom.data.Message
import com.example.chatroom.databinding.ReceiveLayoutBinding
import com.example.chatroom.databinding.SendLayoutBinding
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    companion object{
        const val ITEM_RECEIVE = 1
        const val ITEM_SENT = 2
    }

    class ReceiveViewHolder(private val binding: ReceiveLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val receiveMessage = binding.receiveMessageTextView
    }

    class SendViewHolder(private val binding: SendLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val sentMessage = binding.sentMessageTextView
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        if (viewType == ITEM_RECEIVE) {
            //inflate receive
            val binding =
                ReceiveLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ReceiveViewHolder(binding)
        } else {
            //inflate sent
            val binding =
                SendLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SendViewHolder(binding)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if (holder.javaClass == SendViewHolder::class.java) {
            //logic for sent view holder
            val viewHolder = holder as SendViewHolder
            holder.sentMessage.text = currentMessage.message
        } else {
            //logic for receive view holder
            val viewHolder = holder as ReceiveViewHolder
            holder.receiveMessage.text = currentMessage.message
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]

        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)) {
            return ITEM_SENT
        } else {
            return ITEM_RECEIVE
        }
    }


}