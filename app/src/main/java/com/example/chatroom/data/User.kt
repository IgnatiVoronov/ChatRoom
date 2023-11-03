package com.example.chatroom.data

data class User(
    val userName: String? = null,
    val email: String? = null,
    val uid: String? = null,
    val fcmToken: String? = null
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
)
