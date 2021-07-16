package com.novelijk.numberlog.ui.history

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.novelijk.numberlog.databinding.FragmentHistoryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.io.OutputStream

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    lateinit var binding: FragmentHistoryBinding
    private val viewModel: HistoryViewModel by viewModels()
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                try {
                    val uri: Uri = result.data?.data as Uri
                    val outputStream: OutputStream? =
                        requireActivity().contentResolver.openOutputStream(uri)
                    if (outputStream != null) {
                        outputStream.write(viewModel.exportString.toByteArray())
                        outputStream.close()
                        viewModel.onExportSuccess()
                    }

                } catch (e: Exception) {
                    Toast.makeText(requireActivity(), "export failed", Toast.LENGTH_SHORT).show()
                }

            }
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHistoryBinding.inflate(
            LayoutInflater.from(container?.context),
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.historyEvents.collect { event ->
                when (event) {
                    is HistoryViewModel.HistoryEvents.NavigateToDeleteAllprompt -> {
                        val action =
                            HistoryFragmentDirections.actionNavigationHistoryToDeleteAllDialogue()
                        findNavController().navigate(action)
                    }
                    is HistoryViewModel.HistoryEvents.ShowNoValuesToDeleteMessage ->
                        Toast.makeText(requireContext(), event.msg, Toast.LENGTH_SHORT).show()
                    is HistoryViewModel.HistoryEvents.ShowUndoDeleteMessage ->
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG)
                            .setAction("undo") {
                                viewModel.onUndoDeleteClicked(event.dataPoint)
                            }.show()
                    is HistoryViewModel.HistoryEvents.StartActivityForResultWithCode -> {
                        resultLauncher.launch(event.intent)
                    }
                    is HistoryViewModel.HistoryEvents.ShowExportSuccessMessage ->
                        Toast.makeText(requireActivity(), event.msg, Toast.LENGTH_LONG).show()
                    is HistoryViewModel.HistoryEvents.ShowNoValueToExportMessage ->
                        Toast.makeText(requireActivity(), event.msg, Toast.LENGTH_SHORT).show()

                }
            }
        }
        val adapter = HistoryAdapter()
        binding.apply {
            historyRecyclerView.adapter = adapter
            historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            deleteAllFloatingActionButton.setOnClickListener(View.OnClickListener {
                viewModel.onDeleteAllButtonClicked()
            })
            exportFloatingActionButton.setOnClickListener(View.OnClickListener {
                viewModel.onExportButtonClicked()
            })


        }
        viewModel.history.observe(viewLifecycleOwner, Observer { History ->
            adapter.submitData(viewLifecycleOwner.lifecycle, History)
        })

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val dataPoint =
                    adapter.snapshot().get(viewHolder.absoluteAdapterPosition)
                if (dataPoint != null) {
                    viewModel.onValueSwiped(dataPoint)
                }
            }

        }).attachToRecyclerView(binding.historyRecyclerView)


    }
}