package com.novelijk.numberlog.ui.deleteAllDialogue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novelijk.numberlog.data.DAO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAllViewModel @Inject constructor(private val dao: DAO) : ViewModel() {

    fun onDeleteConfirmClicked() {
        viewModelScope.launch {
            dao.deleteAll()
        }
    }
}