package com.example.rndmusr.presentation.list

import com.example.rndmusr.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rndmusr.databinding.FragmentUserListBinding
import com.example.rndmusr.presentation.details.UserDetailsFragment
import com.example.rndmusr.presentation.list.adapter.UserAdapter
import com.example.rndmusr.presentation.main.MainFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserListFragment : Fragment() {

    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserListViewModel by viewModels()

    private lateinit var adapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = UserAdapter(
            onItemClick = { user ->
                navigateToUserDetails(user.id)
            },
            onDeleteClick = { user ->
                viewModel.deleteUser(user)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@UserListFragment.adapter
        }
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

    private fun handleUiState(state: UserListUiState) {
        when (state) {
            is UserListUiState.Loading -> showLoadingState()
            is UserListUiState.Empty -> showEmptyState()
            is UserListUiState.Success -> showSuccessState(state.users)
            is UserListUiState.Error -> showErrorState(state.message)
        }
    }

    private fun showLoadingState() {
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.emptyState.visibility = View.GONE
    }

    private fun showEmptyState() {
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.emptyState.visibility = View.VISIBLE
    }

    private fun showSuccessState(users: List<com.example.rndmusr.domain.model.User>) {
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        binding.emptyState.visibility = View.GONE

        adapter.submitList(users)
    }
    private fun showErrorState(errorMessage: String) {
        // Скрываем прогресс бар
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.emptyState.visibility = View.GONE

        Snackbar.make(binding.root, "Error: $errorMessage", Snackbar.LENGTH_LONG).show()

    }
    private fun navigateToGenerator() {
        val mainFragment = MainFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, mainFragment)
            .addToBackStack("generator")
            .commit()
    }
    private fun setupClickListeners() {
        binding.fabAdd.setOnClickListener {
            navigateToGenerator()
        }
    }

    private fun navigateToUserDetails(userId: String) {
        val userDetailsFragment = UserDetailsFragment.newInstance(userId)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, userDetailsFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}