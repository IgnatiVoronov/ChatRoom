package com.example.chatroom.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.chatroom.data.Message
import com.example.chatroom.databinding.FragmentChatBinding
import com.example.chatroom.domain.ChatViewModel
import com.example.chatroom.ui.adapters.MessageAdapter
import com.google.firebase.database.DatabaseReference

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDatabaseRef: DatabaseReference
    private val viewModel: ChatViewModel by activityViewModels()

    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
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

    private fun setUpViews() {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = viewModel.userName.value

    }
}