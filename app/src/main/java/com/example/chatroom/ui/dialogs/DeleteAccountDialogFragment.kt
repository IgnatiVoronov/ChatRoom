package com.example.chatroom.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.chatroom.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DeleteAccountDialogFragment : DialogFragment() {

    @Inject
    lateinit var mAuth: FirebaseAuth

    @Inject
    lateinit var mDatabaseRef: DatabaseReference
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Delete your account?")
                .setPositiveButton("Yes") { _, _ ->
                    mAuth.currentUser?.delete()
                    mDatabaseRef.child("user").child(mAuth.currentUser!!.uid).removeValue()
                    findNavController().navigate(R.id.action_listOfUsersFragment_to_logInFragment)
                }
                .setNegativeButton("No") { _, _ ->
                    // User cancelled the dialog.
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}