package com.clerodri.binnacle.home.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor () : ViewModel() {

//    // Event channel to send events to The UI
//    private val _eventChannel = Channel<HomeScreenEvent>()
//    internal fun getEventChannel() = _eventChannel.receiveAsFlow()




    init {
        //here you must load data from repository or other source
        val example:Boolean = false
    }

    // Handle user events
    fun onEvent(event: HomeViewModelEvent) {
        when (event) {
            HomeViewModelEvent.OnCheckIn -> {
                // onZoomAll()  <- example
            }

            HomeViewModelEvent.MakeReport -> {}
            HomeViewModelEvent.OnCheckOut -> {}
            HomeViewModelEvent.StartRound -> {}
            HomeViewModelEvent.StopRound -> {}
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