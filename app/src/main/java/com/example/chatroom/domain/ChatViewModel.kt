package com.example.chatroom.domain

import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.chatroom.data.Message
import com.example.chatroom.data.User
import com.example.chatroom.ui.adapters.MessageAdapter
import com.example.chatroom.ui.adapters.UserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    private val _userName = MutableLiveData("name")
    val userName: LiveData<String>
        get() = _userName

    private val _uid = MutableLiveData("uid")
    val uid: LiveData<String>
        get() = _uid


    fun setName(userName: String) {
        _userName.value = userName
    }

    fun setUid(uid: String) {
        _uid.value = uid
    }

    fun addUserToDatabase(
        name: String,
        email: String,
        uid: String,
        mDatabaseRef: DatabaseReference
    ) {
        mDatabaseRef.child("user").child(uid).setValue(User(name, email, uid))
    }

    fun getDatabaseUsers(
        mDatabaseRef: DatabaseReference,
        userList: ArrayList<User>,
        mAuth: FirebaseAuth,
        adapter: UserAdapter
    ) {
        mDatabaseRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)

                    if (mAuth.currentUser?.uid != currentUser?.uid) {
                        currentUser?.let { userList.add(it) }
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    fun addDateToRecyclerView(
        senderRoom: String,
        messageList: ArrayList<Message>,
        messageAdapter: MessageAdapter,
        mDatabaseRef: DatabaseReference,
        chatRecyclerView: RecyclerView
    ) {
        //logic of adding data to recyclerView
        mDatabaseRef.child("chats").child(senderRoom).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //clear previous messageList
                    messageList.clear()

                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        message?.let { messageList.add(it) }
                    }
                    messageAdapter.notifyDataSetChanged()
                    //scroll to the last message
                    chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    fun addMessageToDatabase(
        senderUid: String,
        senderRoom: String,
        receiverRoom: String,
        sendButtonImageView: ImageView,
        messageBoxEditText: EditText,
        mDatabaseRef: DatabaseReference
    ) {
        //adding the message to database
        sendButtonImageView.setOnClickListener {
            val message = messageBoxEditText.text.toString()
            val messageObject = Message(message, senderUid)

            //create node of chat
            mDatabaseRef.child("chats").child(senderRoom).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDatabaseRef.child("chats").child(receiverRoom).child("messages").push()
                        .setValue(messageObject)
                }
            //clear message box after sending message
            messageBoxEditText.setText("")
        }
    }
}