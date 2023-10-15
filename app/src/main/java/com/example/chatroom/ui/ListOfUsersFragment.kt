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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatroom.R
import com.example.chatroom.data.User
import com.example.chatroom.ui.adapters.UserAdapter
import com.example.chatroom.databinding.FragmentListOfUsersBinding
import com.example.chatroom.domain.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ListOfUsersFragment : Fragment() {
    private var _binding: FragmentListOfUsersBinding? = null
    private val binding get() = _binding!!


    private lateinit var adapter: UserAdapter

    @Inject
    lateinit var userList: ArrayList<User>

    @Inject
    lateinit var mAuth: FirebaseAuth

    @Inject
    lateinit var mDatabaseRef: DatabaseReference

    private val viewModel: ChatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListOfUsersBinding.inflate(inflater, container, false)
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
        //show action bar
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "ChatRoom"
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        createMenu()

        adapter = UserAdapter(requireContext(), userList, viewModel)

        binding.userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.userRecyclerView.adapter = adapter

        getDatabaseUsers()
    }

    private fun createMenu() {
        //the menu for log out
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                if (menuItem.itemId == R.id.logOut) {
                    mAuth.signOut()
                    findNavController().navigate(R.id.action_listOfUsersFragment_to_logInFragment)

                    return true
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun getDatabaseUsers() {
        mDatabaseRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)

                    if (mAuth.currentUser?.uid != currentUser?.uid) {
                        currentUser?.let { userList.add(it) }
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}