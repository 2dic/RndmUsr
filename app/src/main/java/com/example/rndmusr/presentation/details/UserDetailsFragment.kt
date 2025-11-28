package com.example.rndmusr.presentation.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.rndmusr.R
import com.example.rndmusr.databinding.FragmentUserDetailsBinding
import com.example.rndmusr.presentation.list.UserListFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserDetailsFragment : Fragment() {

    private var _binding: FragmentUserDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserDetailsViewModel by viewModels()
    private val userId: String by lazy {
        arguments?.getString(ARG_USER_ID) ?: ""
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUser()
        observeViewModel()
        setupClickListeners()
    }

    private fun loadUser() {
        viewModel.loadUser(userId)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    handleUiState(state)
                }
            }
        }
    }

    private fun handleUiState(state: UserDetailsUiState) {
        when (state) {
            is UserDetailsUiState.Loading -> showLoadingState()
            is UserDetailsUiState.Success -> showSuccessState(state.user)
            is UserDetailsUiState.Error -> showErrorState(state.message)
        }
    }

    private fun showLoadingState() {
        binding.progressBar.visibility = View.VISIBLE
        binding.contentLayout.visibility = View.GONE
        binding.errorText.visibility = View.GONE
    }

    private fun showSuccessState(user: com.example.rndmusr.domain.model.User) {
        binding.progressBar.visibility = View.GONE
        binding.contentLayout.visibility = View.VISIBLE
        binding.errorText.visibility = View.GONE

        // Загрузка изображения
        Glide.with(this)
            .load(user.picture)
            .circleCrop()
            .into(binding.ivUser)

        // Личная информация
        binding.tvName.text = user.fullName
        binding.tvGender.text = "Gender: ${user.gender}"
        binding.tvNationality.text = "Nationality: ${user.nationality}"
        binding.tvBirthday.text = "Age: ${user.age} years"

        // Контактная информация
        binding.tvEmail.text = user.email
        binding.tvPhone.text = user.phone
        binding.tvCell.text = user.cell

        // Адрес
        binding.tvAddress.text = user.street
        binding.tvCity.text = user.city
        binding.tvCountry.text = user.country
        binding.tvPostcode.text = "Postcode: ${user.postcode}"
    }

    private fun navigateToUserList() {
        val userListFragment = UserListFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, userListFragment)
            .addToBackStack("user_list")
            .commit()
    }

    private fun setupClickListeners() {
        binding.fabBack.setOnClickListener {
            navigateToUserList()
        }
    }

    private fun showErrorState(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.contentLayout.visibility = View.GONE
        binding.errorText.visibility = View.VISIBLE
        binding.errorText.text = message
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        private const val ARG_USER_ID = "user_id"

        fun newInstance(userId: String): UserDetailsFragment {
            return UserDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USER_ID, userId)
                }
            }
        }
    }
}