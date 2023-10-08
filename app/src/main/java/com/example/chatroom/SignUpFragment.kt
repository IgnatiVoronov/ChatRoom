package com.example.chatroom

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.chatroom.databinding.FragmentLogInBinding
import com.example.chatroom.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabaseRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpViews(){
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()

        binding.signUpScreenSignUpButton.setOnClickListener {
            val name = binding.signUpScreenNameEditText.text.toString()
            val email = binding.signUpScreenEmailEditText.text.toString()
            val password = binding.signUpScreenPasswordEditText.text.toString()
            signUp(name, email, password)
        }
    }

    private fun signUp(name: String, email: String, password: String) {
        //logic of creating user
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    mAuth.currentUser?.uid?.let { addUserToDatabase(name, email, it) }
                    findNavController().navigate(R.id.action_signUpFragment_to_listOfUsersFragment)

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(requireContext(), "Some error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String) {
        mDatabaseRef = FirebaseDatabase.getInstance().reference
        mDatabaseRef.child("user").child(uid).setValue(User(name, email, uid))
    }
}