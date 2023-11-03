package com.example.chatroom.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.chatroom.domain.ChatViewModel
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ClearChatDialogFragment : DialogFragment() {

    @Inject
    lateinit var mDatabaseRef: DatabaseReference

    private val viewModel: ChatViewModel by activityViewModels()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            var clearEverywhereChecked = false
            builder.setTitle("Clear chat?")
                .setPositiveButton("Yes") { _, _ ->
                    if (clearEverywhereChecked) {
                        viewModel.senderRoom.value?.let { it1 ->
                            mDatabaseRef.child("chats").child(it1).removeValue()
                        }
                        viewModel.receiverRoom.value?.let { it1 ->
                            mDatabaseRef.child("chats").child(it1).removeValue()
                        }
                    } else {
                        viewModel.senderRoom.value?.let { it1 ->
                            mDatabaseRef.child("chats").child(it1).removeValue()
                        }
                    }
                }
                .setNegativeButton("Cancel") { _, _ ->
                    // User cancelled the dialog.
                }
                .setMultiChoiceItems(arrayOf("Clear everywhere"), null) { _, _, _ ->
                    clearEverywhereChecked = true
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}