package com.example.mychatapp.presentation.ui.signin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.navOptions
import com.example.mychatapp.R
import com.example.mychatapp.databinding.FragmentSignInBinding
import com.example.mychatapp.utils.UIState
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private val signInViewModel: SignInViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (checkUserExistence()){
            goToHomeFragment(requireView())
        } else{

            observeSignInState()

            binding.signInTextToSignUp.setOnClickListener {
                goToSignUpFragment(it)
            }

            binding.loginButton.setOnClickListener{
                signIn()
            }
        }

    }

    private fun goToHomeFragment(view: View) {
        val action = SignInFragmentDirections.actionSignInFragmentToFragmentHome()
        Navigation.findNavController(view).navigate(action, navOptions {
            popUpTo(R.id.signInFragment) {
                inclusive = true
            }
        })
    }

    private fun goToSignUpFragment(view: View) {
        val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
        Navigation.findNavController(view).navigate(action)
    }

    private fun signIn() {
        signInViewModel.signIn(binding.loginetemail.text.toString(), binding.loginetpassword.text.toString())
    }

    private fun observeSignInState() {
        lifecycleScope.launch(Dispatchers.IO) {
            signInViewModel.signInState.collect { state ->
                when (state) {
                    is UIState.OnFailure -> {
                        withContext(Dispatchers.Main){
                            binding.progressBar.visibility = View.GONE
                            Snackbar.make(requireView(), state.errorMessage, 2000).show()
                        }

                    }

                    UIState.OnLoading -> {
                        withContext(Dispatchers.Main){
                            binding.progressBar.visibility = View.VISIBLE
                        }
                    }

                    is UIState.OnSuccess<*> -> {
                        withContext(Dispatchers.Main){
                            binding.progressBar.visibility = View.GONE
                            goToHomeFragment(requireView())
                        }

                    }

                    UIState.OnIdle -> {}
                }
            }
        }
    }


    private fun checkUserExistence():Boolean{
        return auth.currentUser != null
    }

}