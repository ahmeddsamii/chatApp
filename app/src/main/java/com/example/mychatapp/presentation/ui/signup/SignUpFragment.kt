package com.example.mychatapp.presentation.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.mychatapp.databinding.FragmentSignUpBinding
import com.example.mychatapp.utils.UIState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var binding:FragmentSignUpBinding
    private val signUpViewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSignUpState()

        binding.signUpBtn.setOnClickListener{
            signUp()
        }
    }

    private fun signUp(){
        signUpViewModel.signUp(binding.signUpEmail.text.toString(), binding.signUpPassword.text.toString(), binding.signUpEtName.text.toString())
    }

    private fun observeSignUpState(){
        lifecycleScope.launch(Dispatchers.IO) {
            signUpViewModel.signUpState.collect{state ->
                when(state){
                    is UIState.OnFailure -> {
                        withContext(Dispatchers.Main){
                            binding.progressBar.visibility = View.GONE
                            Snackbar.make(requireView(), state.errorMessage, 2000).show()
                        }
                    }
                    UIState.OnIdle -> {}
                    UIState.OnLoading -> {
                        withContext(Dispatchers.Main){
                            binding.progressBar.visibility = View.VISIBLE
                        }
                    }
                    is UIState.OnSuccess<*> -> {
                        withContext(Dispatchers.Main){
                            binding.progressBar.visibility = View.GONE
                            Snackbar.make(requireView(), "User crated successfully", 2000).show()
                            clearAllFields()
                        }
                    }
                }
            }
        }
    }

    private fun clearAllFields(){
        binding.signUpEmail.text.clear()
        binding.signUpPassword.text.clear()
        binding.signUpEtName.text.clear()
    }

}