package com.example.chatroom.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatroom.data.Message
import com.example.chatroom.databinding.FragmentChatBinding
import com.example.chatroom.domain.ChatViewModel
import com.example.chatroom.ui.adapters.MessageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var messageList: ArrayList<Message>

    @Inject
    lateinit var mDatabaseRef: DatabaseReference

    private lateinit var messageAdapter: MessageAdapter

    private val viewModel: ChatViewModel by activityViewModels()

    private var receiverRoom: String? = null
    private var senderRoom: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        messageAdapter = MessageAdapter(requireContext(), messageList)

        val receiverUid = viewModel.uid.value
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        (binding.chatRecyclerView.layoutManager as LinearLayoutManager).stackFromEnd = true
        binding.chatRecyclerView.adapter = messageAdapter
        binding.chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)

        viewModel.addDateToRecyclerView(
            senderRoom!!,
            messageList,
            messageAdapter,
            mDatabaseRef,
            binding.chatRecyclerView
        )

        viewModel.addMessageToDatabase(
            senderUid!!,
            senderRoom!!,
            receiverRoom!!,
            binding.sendButtonImageView,
            binding.messageBoxEditText,
            mDatabaseRef
        )
    }


}