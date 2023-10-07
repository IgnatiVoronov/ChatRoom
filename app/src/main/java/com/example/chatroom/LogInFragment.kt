package com.example.chatroom

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chatroom.databinding.FragmentLogInBinding
import com.google.firebase.auth.FirebaseAuth

class LogInFragment : Fragment() {

    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLogInBinding.inflate(inflater, container, false)
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
        binding.logInScreenSignUpButton.setOnClickListener {
            findNavController().navigate(R.id.action_logInFragment_to_signUpFragment)
        }

        mAuth = FirebaseAuth.getInstance()

        binding.logInScreenLogInButton.setOnClickListener {
            val email = binding.logInScreenEmailEditText.text.toString()
            val password = binding.logInScreenPasswordEditText.text.toString()
            login(email, password)
        }
    }

    private fun login(email: String, password: String) {
        //logic of log in user
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //finish()
                    findNavController().navigate(R.id.action_logInFragment_to_chatFragment)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(requireContext(), "User does not exist", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
}