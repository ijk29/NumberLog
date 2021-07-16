package com.novelijk.numberlog.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.novelijk.numberlog.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentHomeBinding.inflate(LayoutInflater.from(container?.context), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.homeEvents.collect { event ->
                when (event) {
                    is HomeViewModel.HomeEvents.InvalidInputMessage -> Toast.makeText(
                        requireContext(),
                        event.msg,
                        Toast.LENGTH_SHORT
                    ).show()
                    is HomeViewModel.HomeEvents.ShowValueSavedMessage -> {
                        Toast.makeText(requireContext(), event.msg, Toast.LENGTH_SHORT).show()
                        binding.apply {
                            editTextNumberDecimal2.text.clear()
                            val inm =
                                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            inm.hideSoftInputFromWindow(
                                requireActivity().currentFocus?.windowToken,
                                InputMethodManager.HIDE_NOT_ALWAYS
                            )
                        }
                    }
                }
            }
        }

        binding.apply {
            floatingActionButton2.setOnClickListener(View.OnClickListener {
                viewModel.onDataEntered(
                    editTextNumberDecimal2.text.toString()
                )
            })
        }
    }
}