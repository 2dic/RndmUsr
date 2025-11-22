package com.example.rndmusr.presentation.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rndmusr.databinding.ItemUserBinding
import com.example.rndmusr.domain.model.User

class UserAdapter(
    private val onItemClick: (User) -> Unit,
    private val onDeleteClick: (User) -> Unit
) : ListAdapter<User, UserAdapter.UserViewHolder>(UserDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UserViewHolder(
        private val binding: ItemUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            with(binding) {
                // Загрузка изображения
                Glide.with(ivUser.context)
                    .load(user.picture)
                    .circleCrop()
                    .into(ivUser)

                tvName.text = user.fullName
                tvEmail.text = user.email
                tvPhone.text = user.phone

                // Обработчик клика на элемент
                root.setOnClickListener {
                    onItemClick(user)
                }

                // Обработчик клика на удаление
                btnDelete.setOnClickListener {
                    onDeleteClick(user)
                }
            }
        }
    }

    object UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}