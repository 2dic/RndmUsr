package com.example.rndmusr.presentation.main

import android.content.Intent
import android.provider.Settings
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.rndmusr.R
import com.example.rndmusr.databinding.FragmentMainBinding
import com.example.rndmusr.presentation.list.UserListFragment
import com.example.rndmusr.domain.utils.NetworkUtils
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

    private val settingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        checkInternetAndRetry()
    }

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
            animateButtonClick(view){
                checkInternetAndRetry()
            }
        }

        binding.btnBackToUsers.setOnClickListener {
            navigateToUserList()
        }

        binding.btnSaveUser.setOnClickListener {view ->
            animateButtonClick(view) {
                saveCurrentUser()
            }
        }
    }

    private fun checkInternetAndRetry() {
        if (!NetworkUtils.isInternetAvailable(requireContext())) {
            showInternetDialog()
        }else{
            generateUser()
        }
    }

    private fun showInternetDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("No Internet Connection")
            .setMessage("Please check your internet connection and try again. Would you like to open network settings?")
            .setPositiveButton("Open Settings") { dialog, which ->
                openNetworkSettings()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .setNeutralButton("Retry") { dialog, which ->
                checkInternetAndRetry()
            }
            .show()
    }

    private fun openNetworkSettings() {
        try {
            val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            settingsLauncher.launch(intent)
        } catch (_: Exception) {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            settingsLauncher.launch(intent)
        }
    }

    private fun animateButtonClick(view: View, action: () -> Unit) {
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
                action()
            }
            .start()
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
        if (!NetworkUtils.isInternetAvailable(requireContext())) {
            showErrorState("No internet connection. Tap to retry.")
            return
        }
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
        binding.errorText.visibility = View.GONE
        binding.emptyState?.visibility = View.VISIBLE
        binding.userContainer.visibility = View.GONE
        binding.userContent.visibility = View.GONE
        binding.btnSaveUser.isEnabled = false
    }

    private fun showLoadingState() {
        binding.progressBar.visibility = View.VISIBLE
        binding.errorText.visibility = View.GONE
        binding.emptyState?.visibility = View.GONE
        binding.userContainer.visibility = View.VISIBLE
        binding.userContent.visibility = View.GONE
        binding.btnSaveUser.isEnabled = false
    }

    private fun showSuccessState(user: com.example.rndmusr.domain.model.User) {
        binding.progressBar.visibility = View.GONE
        binding.errorText.visibility = View.GONE
        binding.emptyState?.visibility = View.GONE
        binding.userContainer.visibility = View.VISIBLE
        binding.userContent.visibility = View.VISIBLE
        binding.btnSaveUser.isEnabled = true

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
        binding.errorText.visibility = View.VISIBLE
        binding.errorText.text = message
        binding.emptyState?.visibility = View.GONE
        binding.userContainer.visibility = View.GONE
    }

    private fun showMessage(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}