package com.example.chatroom.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
}