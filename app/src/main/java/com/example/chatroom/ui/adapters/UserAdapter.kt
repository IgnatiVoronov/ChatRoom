package com.example.chatroom.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatroom.data.User
import com.example.chatroom.databinding.UserLayoutBinding
import com.example.chatroom.domain.ChatViewModel
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.chatroom.R


class UserAdapter(
    val context: Context,
    private val userList: ArrayList<User>,
    private val viewModel: ChatViewModel
) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(binding: UserLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val textName = binding.nameTextView
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {
        val binding = UserLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        var navController: NavController?
        val currentUser = userList[position]
        holder.textName.text = currentUser.userName

        holder.itemView.setOnClickListener {

            viewModel.setName(currentUser.userName.toString())
            viewModel.setUid(currentUser.uid.toString())
            viewModel.setFCNToken(currentUser.fcmToken.toString())

            navController = Navigation.findNavController(holder.itemView)
            navController!!.navigate(R.id.action_listOfUsersFragment_to_chatFragment)

        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

}