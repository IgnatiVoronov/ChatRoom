package com.example.chatroom.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatroom.R
import com.example.chatroom.data.User
import com.example.chatroom.databinding.FragmentListOfUsersBinding
import com.example.chatroom.domain.ChatViewModel
import com.example.chatroom.ui.adapters.UserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ListOfUsersFragment : Fragment() {
    private var _binding: FragmentListOfUsersBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var userList: ArrayList<User>

    @Inject
    lateinit var mAuth: FirebaseAuth

    @Inject
    lateinit var mDatabaseRef: DatabaseReference

    private lateinit var adapter: UserAdapter
    private val viewModel: ChatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListOfUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            setUpViews()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun setUpViews() = coroutineScope{

        val job = launch{
            viewModel.getCurrentUserName()
        }
        job.join()
        //show action bar
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            viewModel.currentUserName.value
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        createMenu(mAuth)

        adapter = UserAdapter(requireContext(), userList, viewModel)

        binding.userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.userRecyclerView.adapter = adapter

        viewModel.getDatabaseUsers(userList, adapter)
    }

    private fun createMenu(mAuth: FirebaseAuth) {
        //the menu for log out
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.list_of_users_menu, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                if (menuItem.itemId == R.id.logOut) {
                    mAuth.signOut()
                    findNavController().navigate(R.id.action_listOfUsersFragment_to_logInFragment)
                    return true
                } else if (menuItem.itemId == R.id.deleteAccount) {
                    mAuth.currentUser?.delete()
                    mDatabaseRef.child("user").child(mAuth.currentUser!!.uid).removeValue()
                    findNavController().navigate(R.id.action_listOfUsersFragment_to_logInFragment)
                    return true
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}