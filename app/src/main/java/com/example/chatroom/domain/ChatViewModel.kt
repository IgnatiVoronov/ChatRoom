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
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@HiltViewModel
class ChatViewModel @Inject constructor(
    var mDatabaseRef: DatabaseReference,
    var mAuth: FirebaseAuth
) : ViewModel() {

    private val _fcmToken = MutableLiveData("token")
    val fcmToken: LiveData<String>
        get() = _fcmToken

    private val _currentUserName = MutableLiveData("name")
    val currentUserName: LiveData<String>
        get() = _currentUserName

    private val _userName = MutableLiveData("name")
    val userName: LiveData<String>
        get() = _userName

    private val _uid = MutableLiveData("uid")
    val uid: LiveData<String>
        get() = _uid

    private val _senderRoom = MutableLiveData("senderRoom")
    val senderRoom: LiveData<String>
        get() = _senderRoom

    private val _receiverRoom = MutableLiveData("receiverRoom")
    val receiverRoom: LiveData<String>
        get() = _receiverRoom

    private val _apiKey = MutableLiveData("apiKey")
    val apiKey: LiveData<String>
        get() = _apiKey

    fun setFCNToken(fcmToken: String) {
        _fcmToken.value = fcmToken
    }

    fun setCurrentUserName(currentUserName: String) {
        _currentUserName.value = currentUserName
    }

    fun setName(userName: String) {
        _userName.value = userName
    }

    fun setUid(uid: String) {
        _uid.value = uid
    }

    fun setSenderRoom(senderRoom: String) {
        _senderRoom.value = senderRoom
    }

    fun setReceiverRoom(receiverRoom: String) {
        _receiverRoom.value = receiverRoom
    }

    fun setApiKey(apiKey: String) {
        _apiKey.value = apiKey
    }

    fun addUserToDatabase(
        name: String,
        email: String,
        uid: String
    ) {
        mDatabaseRef.child("user").child(uid).setValue(User(name, email, uid, fcmToken.value))
    }

    fun getDatabaseUsers(
        userList: ArrayList<User>,
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
        messageList: ArrayList<Message>,
        messageAdapter: MessageAdapter,
        chatRecyclerView: RecyclerView
    ) {
        //logic of adding data to recyclerView
        mDatabaseRef.child("chats").child(senderRoom.value!!).child("messages")
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
                }
            })
    }

    fun addMessageToDatabase(
        senderUid: String,
        sendButtonImageView: ImageView,
        messageBoxEditText: EditText
    ) {
        //adding the message to database
        sendButtonImageView.setOnClickListener {
            val message = messageBoxEditText.text.toString()
            val messageObject = Message(message, senderUid)

            //create node of chat
            mDatabaseRef.child("chats").child(senderRoom.value!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDatabaseRef.child("chats").child(receiverRoom.value!!).child("messages").push()
                        .setValue(messageObject)
                    sendNotification(message)
                }
            //clear message box after sending message
            messageBoxEditText.setText("")
        }
    }

    suspend fun getCurrentUserName() = suspendCoroutine<DataSnapshot> { continuation ->
        mDatabaseRef.child("user").child(mAuth.currentUser!!.uid).child("userName").get()
            .addOnSuccessListener {
                setCurrentUserName(it.value.toString())
                continuation.resume(it)
            }
    }

    fun addFCMTokenToDatabase() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                mDatabaseRef.child("user").child(mAuth.currentUser!!.uid).child("fcmToken")
                    .setValue(token)
            }
        }
    }

    fun getFCMTokenFromDatabase(uid: String) {
        mDatabaseRef.child("user").child(uid).child("fcmToken").get()
            .addOnSuccessListener {
                setFCNToken(it.value.toString())
            }
    }

    private fun sendNotification(message: String) {
        //current user name, message, current userid, other user token
        mDatabaseRef.child("user").child(mAuth.currentUser!!.uid).child("userName").get()
            .addOnSuccessListener {
                try {
                    val jsonObject = JSONObject()
                    val notificationObj = JSONObject()
                    notificationObj.put("title", it.value)
                    notificationObj.put("body", message)

                    val dataObj = JSONObject()
                    dataObj.put("userName", it.value)
                    dataObj.put("uid", mAuth.currentUser!!.uid)

                    jsonObject.put("notification", notificationObj)
                    jsonObject.put("data", dataObj)
                    jsonObject.put("to", fcmToken.value)

                    callApi(jsonObject)
                } catch (_: Exception) {

                }
            }

    }

    fun callApi(jsonObject: JSONObject) {
        val JSON: MediaType = "application/json".toMediaType()
        val client = OkHttpClient()
        val url = "https://fcm.googleapis.com/fcm/send"
        val body = jsonObject.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .header(
                "Authorization",
                "Bearer " + apiKey.value
            )
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //Not yet implemented
            }

            override fun onResponse(call: Call, response: Response) {
                //Not yet implemented
            }

        })
    }
}