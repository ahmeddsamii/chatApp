package com.example.mychatapp.presentation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mychatapp.R
import com.example.mychatapp.databinding.FragmentHomeBinding
import com.example.mychatapp.domain.entity.User
import com.example.mychatapp.utils.UIState
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentHome : Fragment(), OnUserClickListener {
    private lateinit var binding:FragmentHomeBinding
    private lateinit var usersAdapter: UsersAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private val homeViewModel:HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        usersAdapter = UsersAdapter(this)
        setupRecyclerViewOfUsers()

        observeUsersState()
        observeLoggedInUserData()
        homeViewModel.getAllUsers()
        homeViewModel.getDataOfLoggedUser()

        binding.logOut.setOnClickListener {
            logout()
        }
    }


    private fun setupRecyclerViewOfUsers(){
        binding.rvUsers.apply {
            this.adapter = usersAdapter
            this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun observeLoggedInUserData(){
        homeViewModel.currentUser.observe(viewLifecycleOwner){currentUser->
            Glide.with(requireContext()).load(currentUser?.imageUrl).into(binding.tlImage)
        }
    }

    override fun onUserSelect(position: Int, user: User) {
        val action = FragmentHomeDirections.actionFragmentHomeToChatFragment(user)
        Navigation.findNavController(requireView()).navigate(action)
    }

    private fun logout(){
        firebaseAuth.signOut()
        goToSignInFragment()

    }

    private fun goToSignInFragment(){
        val action = FragmentHomeDirections.actionFragmentHomeToSignInFragment()
        Navigation.findNavController(requireView()).navigate(action, navOptions {
            popUpTo(R.id.fragmentHome){
                inclusive = true
            }
        })
    }

    private fun observeUsersState(){
        lifecycleScope.launch {
            homeViewModel.allUsers.collect{state->
                when (state){
                    is UIState.OnFailure -> {
                        binding.usersProgressbar.visibility = View.GONE
                        Snackbar.make(requireView(), "Failed to get the users", 2000).show()
                    }
                    UIState.OnIdle -> {}
                    UIState.OnLoading -> {binding.usersProgressbar.visibility = View.VISIBLE}
                    is UIState.OnSuccess<*> -> {
                        binding.usersProgressbar.visibility = View.GONE
                        val data = state.data as MutableList<User>
                        usersAdapter.submitList(data)
                    }
                }
            }
        }
    }
}