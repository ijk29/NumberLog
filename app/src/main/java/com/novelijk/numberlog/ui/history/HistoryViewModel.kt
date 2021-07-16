package com.novelijk.numberlog.ui.history

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.novelijk.numberlog.data.DAO
import com.novelijk.numberlog.data.DataPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(val dao: DAO) : ViewModel() {


    lateinit var history: LiveData<PagingData<DataPoint>>
    private val historyEventsChannel = Channel<HistoryEvents>()
    val historyEvents = historyEventsChannel.receiveAsFlow()
    var exportString: String = "Export"

    init {
        viewModelScope.launch {
            history = Pager(
                config = PagingConfig(
                    pageSize = 20,
                    maxSize = 10000,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = { dao.getHistoryPagingSource() }
            ).liveData.cachedIn(viewModelScope)
        }
    }

    fun onDeleteAllButtonClicked() {

        viewModelScope.launch {
            val count = dao.getValueCount()
            if (count == 0)
                historyEventsChannel.send(HistoryEvents.ShowNoValuesToDeleteMessage("No values to delete"))
            else
                historyEventsChannel.send(HistoryEvents.NavigateToDeleteAllprompt)

        }

    }

    fun onValueSwiped(dataPoint: DataPoint) {
        viewModelScope.launch {
            dao.delete(dataPoint)
            historyEventsChannel.send(
                HistoryEvents.ShowUndoDeleteMessage(
                    dataPoint,
                    "value deleted"
                )
            )
        }
    }

    fun onUndoDeleteClicked(dataPoint: DataPoint) {
        viewModelScope.launch {
            dao.insert(dataPoint)
        }
    }

    fun onExportButtonClicked() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
            putExtra(Intent.EXTRA_TITLE, "NumberLog export")
        }
        viewModelScope.launch {
            val count = dao.getValueCount()
            if (count !=0){
                val tempValues = dao.getHistory()
                var temString =
                    "Exported from NumberLog app \nDate,Time,Value"
                for (dataPoint in tempValues) {
                    temString =
                        temString + "\n" + dataPoint.dateFormattedToExport + "," + dataPoint.timeFormattedToExport + "," + dataPoint.value.toString()
                }
                exportString = temString
                historyEventsChannel.send(HistoryEvents.StartActivityForResultWithCode(intent))
            }
            else {
                historyEventsChannel.send(HistoryEvents.ShowNoValueToExportMessage("No values to Export"))
            }



        }

    }

    fun onExportSuccess() {
        viewModelScope.launch {
            historyEventsChannel.send(HistoryEvents.ShowExportSuccessMessage("Values successfully saved as .csv file"))
        }
    }


    sealed class HistoryEvents {
        object NavigateToDeleteAllprompt : HistoryEvents()
        data class ShowNoValuesToDeleteMessage(val msg: String) : HistoryEvents()
        data class ShowUndoDeleteMessage(val dataPoint: DataPoint, val msg: String) : HistoryEvents()
        data class StartActivityForResultWithCode(val intent: Intent) : HistoryEvents()
        data class ShowNoValueToExportMessage(val msg:String): HistoryEvents()
        data class ShowExportSuccessMessage(val msg: String) : HistoryEvents()
    }

}