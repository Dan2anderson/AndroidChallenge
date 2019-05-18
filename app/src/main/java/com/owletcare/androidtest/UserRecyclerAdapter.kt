package com.owletcare.androidtest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.owletcare.androidtest.extensions.loadWebImageAsync
import com.owletcare.androidtest.redux.Store
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * UserRecyclerAdapter.kt
 * OwletBuggyAndroid
 *
 * Created by kvanry on 4/15/19.
 * Copyright (c) 2019. Owlet Care. All rights reserved worldwide.
 */
class UserRecyclerAdapter(private val store: Store<State>) :
    RecyclerView.Adapter<UserRecyclerAdapter.UserViewHolder>() {

    private val usersDisplayed: ArrayList<User> = arrayListOf()

    val subscriber: (ArrayList<User>) -> Unit = { users ->
        val keepUsers = arrayListOf<User>()
        val deletedUserIndexMap = mutableMapOf<Int, User>()

        //determine which users we need to keep and which should be removed.
        usersDisplayed.apply {
            forEach {
                if (users.contains(it)) {
                    keepUsers.add(it)
                } else {
                    deletedUserIndexMap[indexOf(it)] = it
                }
            }
        }
        //remove users
        deletedUserIndexMap.forEach {
            usersDisplayed.removeAt(it.key)
            notifyItemRemoved(it.key)
        }
        //determine which users we need to add
        //add users
        users.forEach {
            if (!keepUsers.contains(it)) {
                val addedIndex = usersDisplayed.size
                usersDisplayed.add(it)
                notifyItemInserted(addedIndex)
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        store.subscribe(subscriber) { it.users }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_list_item, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return usersDisplayed.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(usersDisplayed[position])
    }

    override fun onViewRecycled(holder: UserViewHolder) {
        holder.job?.cancel()
        holder.view.findViewById<ImageView>(R.id.user_profilePicture).setImageBitmap(null)
        holder.view.findViewById<TextView>(R.id.user_name).text = ""
        holder.view.findViewById<ImageView>(R.id.user_delete).setOnClickListener(null)
    }

    inner class UserViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var job: Job? = null
        fun bind(user: User) {
            view.findViewById<TextView>(R.id.user_name).text = user.name
            val profilePic = view.findViewById<ImageView>(R.id.user_profilePicture)
            profilePic.setImageBitmap(null)
            job = MainScope().launch {
                profilePic.loadWebImageAsync(user.profilePicture, ProfilePicCache.getLru())
            }
            view.findViewById<ImageView>(R.id.user_delete).setOnClickListener {
                store.dispatch(UsersAction.RemoveUser(user))
            }
        }
    }
}