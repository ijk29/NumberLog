package com.novelijk.numberlog.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novelijk.numberlog.data.DAO
import com.novelijk.numberlog.data.DataPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val dao: DAO) : ViewModel() {

    private val homeEventsChannel =
        Channel<HomeEvents>() //Channel to trigger events in the HomeFragment
    val homeEvents = homeEventsChannel.receiveAsFlow() // exposing channel as a flow to the Fragment

    fun onDataEntered(num: String) {
        if (num.isNotBlank()) {
            viewModelScope.launch {
                dao.insert(DataPoint(num.toFloat()))
                homeEventsChannel.send(HomeEvents.ShowValueSavedMessage("Value Saved"))
            }
        } else {
            viewModelScope.launch {
                homeEventsChannel.send(HomeEvents.InvalidInputMessage("Value cannot be blank"))
            }

        }
    }

    sealed class HomeEvents {
        data class InvalidInputMessage(val msg: String) : HomeEvents()
        data class ShowValueSavedMessage(val msg: String) : HomeEvents()
    }
}