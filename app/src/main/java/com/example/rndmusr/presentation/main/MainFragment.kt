package com.example.rndmusr.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.rndmusr.R
import com.example.rndmusr.databinding.FragmentMainBinding
import com.example.rndmusr.presentation.list.UserListFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    private val nationalities = arrayOf(
        "Any", "AU", "BR", "CA", "CH", "DE", "DK", "ES", "FI", "FR", "GB", "IE", "IN", "IR", "MX", "NL", "NO", "NZ", "RS", "TR", "UA", "US"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupNationalitySpinner()
        observeViewModel()
    }

    private fun setupUI() {
        binding.btnGenerate.setOnClickListener {view ->
            view.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction {
                    view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()
            generateUser()
        }

        binding.btnViewUsers.setOnClickListener {
            navigateToUserList()
        }

        binding.btnSaveUser.setOnClickListener {view ->
            view.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction {
                    view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()
            saveCurrentUser()
        }
    }

    private fun setupNationalitySpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            nationalities
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerNationality.adapter = adapter
    }

    private fun generateUser() {
        val gender = when (binding.radioGroupGender.checkedRadioButtonId) {
            binding.radioMale.id -> "male"
            binding.radioFemale.id -> "female"
            else -> null
        }

        val nationality = if (binding.spinnerNationality.selectedItemPosition > 0) {
            binding.spinnerNationality.selectedItem as String
        } else {
            null
        }

        viewModel.generateUser(gender, nationality)
    }

    private fun saveCurrentUser() {
        val currentState = viewModel.uiState.value
        if (currentState is MainUiState.Success) {
            viewModel.saveUser(currentState.user)
            showMessage("User saved successfully!")
        }
    }

    private fun navigateToUserList() {
        val userListFragment = UserListFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, userListFragment)
            .addToBackStack("user_list")
            .commit()
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

    private fun handleUiState(state: MainUiState) {
        when (state) {
            is MainUiState.Idle -> showIdleState()
            is MainUiState.Loading -> showLoadingState()
            is MainUiState.Success -> showSuccessState(state.user)
            is MainUiState.Error -> showErrorState(state.message)
        }
    }

    private fun showIdleState() {
        binding.progressBar.visibility = View.GONE
        binding.userContainer.visibility = View.GONE
        binding.errorText.visibility = View.GONE
    }

    private fun showLoadingState() {
        binding.progressBar.visibility = View.VISIBLE
        binding.userContainer.visibility = View.GONE
        binding.errorText.visibility = View.GONE
    }

    private fun showSuccessState(user: com.example.rndmusr.domain.model.User) {
        binding.progressBar.visibility = View.GONE
        binding.userContainer.visibility = View.VISIBLE
        binding.errorText.visibility = View.GONE

        // Загрузка изображения
        Glide.with(this)
            .load(user.picture)
            .circleCrop()
            .into(binding.ivUser)

        binding.tvName.text = user.fullName
        binding.tvEmail.text = user.email
        binding.tvPhone.text = user.phone
    }

    private fun showErrorState(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.userContainer.visibility = View.GONE
        binding.errorText.visibility = View.VISIBLE
        binding.errorText.text = message
    }

    private fun showMessage(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}