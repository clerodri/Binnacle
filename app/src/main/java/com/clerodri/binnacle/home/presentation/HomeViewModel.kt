package com.clerodri.binnacle.home.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clerodri.binnacle.home.domain.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor () : ViewModel() {

//    // Event channel to send events to The UI
//    private val _eventChannel = Channel<HomeScreenEvent>()
//    internal fun getEventChannel() = _eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(HomeScreenViewState())
    val state: StateFlow<HomeScreenViewState> = _state.asStateFlow()

    private val _routes = MutableStateFlow<List<Route>>(emptyList())
    val routes: StateFlow<List<Route>> = _routes.asStateFlow()


    init {
        //here you must load data from repository or other source
        fetchRoutes()
    }

    private fun fetchRoutes() {
        viewModelScope.launch {
            try {
                val items = listOf(
                    Route("1", "SOLAR 14 FAM.HOOLIGAN"),
                    Route("3", "SOLAR Y FAM.HIGGINS"),
                    Route("5", "SOLAR 51"),
                    Route("6", "CANCHA DE TENNIS")
                )
                _routes.value = items.sortedBy{ it.id.toInt() }

            }catch (e:Exception){
                Log.e("HomeViewModel", "Error fetching routes: ${e.message}")
            }
        }
    }

    // Handle user events
    fun onEvent(event: HomeViewModelEvent) {
        when (event) {
            HomeViewModelEvent.OnCheckIn -> {
                // onZoomAll()  <- example
            }

            HomeViewModelEvent.MakeReport -> {}
            HomeViewModelEvent.OnCheckOut -> {}
            HomeViewModelEvent.StartRound -> {
                _state.update {
                    it.copy(isStarted = true, isRoundBtnEnabled = false)
                }

            }
            HomeViewModelEvent.StopRound -> {
                _state.update {
                    it.copy(  currentIndex = 0, isStarted = false)
                }
            }

            HomeViewModelEvent.UpdateIndex -> {
                if (_state.value.currentIndex == _routes.value.size - 2){
                    _state.update {
                        it.copy(isRoundBtnEnabled = true)
                    }
                }
                _state.update {
                    it.copy(currentIndex = it.currentIndex + 1)
                }
            }
        }
    }


//    private fun onZoomAll() {  <- example
//        sendScreenEvent(MountainsScreenEvent.OnZoomAll)
//    }

    // Send events back to the UI via the event channel
//    private fun sendScreenEvent(event: HomeScreenEvent) {
//        viewModelScope.launch {
//            _eventChannel.send(event)
//        }
//    }


}