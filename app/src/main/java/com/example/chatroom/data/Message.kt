package com.example.chatroom.data

data class Message(
    val message: String? = null,
    val senderId: String? = null
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
)
