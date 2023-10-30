package com.example.chatroom.ui

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatroom.R
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
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_chatFragment_to_listOfUsersFragment)
        }

        val applicationInfo: ApplicationInfo = requireActivity().packageManager
            .getApplicationInfo(requireActivity().packageName, PackageManager.GET_META_DATA)
        val apiKey = applicationInfo.metaData.getString("keyValue")
        apiKey?.let { viewModel.setApiKey(it) }

        messageAdapter = MessageAdapter(requireContext(), messageList)

        val receiverUid = viewModel.uid.value
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        viewModel.setSenderRoom(receiverUid + senderUid)
        viewModel.setReceiverRoom(senderUid + receiverUid)

        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        (binding.chatRecyclerView.layoutManager as LinearLayoutManager).stackFromEnd = true
        binding.chatRecyclerView.adapter = messageAdapter
        binding.chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)

        createMenu()

        viewModel.addDateToRecyclerView(
            messageList,
            messageAdapter,
            binding.chatRecyclerView
        )

        viewModel.addMessageToDatabase(
            senderUid!!,
            binding.sendButtonImageView,
            binding.messageBoxEditText
        )
    }

    private fun createMenu() {
        //the menu for log out
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.chat_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                if (menuItem.itemId == R.id.clearChat) {
                    ClearChatDialogFragment().show(
                        requireActivity().supportFragmentManager,
                        "CLEAR_CHAT_DIALOG"
                    )
                    return true
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}