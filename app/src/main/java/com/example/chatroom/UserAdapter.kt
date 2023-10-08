package com.example.chatroom

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatroom.databinding.UserLayoutBinding

class UserAdapter(val context: Context, val userList: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(private val binding: UserLayoutBinding) :
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
        val currentUser = userList[position]
        holder.textName.text = currentUser.userName

        /*holder.itemView.setOnClickListener {
            val intent = Intent(context, ListOfUsersFragment::class.java)

            intent.putExtra("name", currentUser.userName)
            intent.putExtra("uid", currentUser.uid)

            context.startActivity(intent)
        }*/
    }

    override fun getItemCount(): Int {
        return userList.size
    }

}