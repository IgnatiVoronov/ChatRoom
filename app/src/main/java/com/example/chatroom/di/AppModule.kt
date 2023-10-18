package com.example.chatroom.di

import com.example.chatroom.data.Message
import com.example.chatroom.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun mAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    fun mDatabaseRef(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
    }

    @Provides
    fun userList(): ArrayList<User> {
        return ArrayList()
    }

    @Provides
    fun messageList(): ArrayList<Message> {
        return ArrayList()
    }
}