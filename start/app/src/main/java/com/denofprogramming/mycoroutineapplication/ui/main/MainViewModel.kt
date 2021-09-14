package com.denofprogramming.mycoroutineapplication.ui.main


import android.graphics.Bitmap
import androidx.lifecycle.*
import com.denofprogramming.mycoroutineapplication.network.MockNetworkService
import com.denofprogramming.mycoroutineapplication.repository.image.CoroutineImageRepository
import com.denofprogramming.mycoroutineapplication.repository.time.DefaultClock
import com.denofprogramming.mycoroutineapplication.shared.Resource
import com.denofprogramming.mycoroutineapplication.shared.uilt.logMessage
import kotlinx.coroutines.*


/**
 *
 *
 */
class MainViewModel : ViewModel() {


    private val _clock = DefaultClock.build()

    private val _imageRepository =
        CoroutineImageRepository.build(MockNetworkService.build())

    val image: LiveData<Resource<Bitmap>> get() = _image

    private val _image = MutableLiveData<Resource<Bitmap>>()

    val currentTimeTransformed = _clock.time.switchMap {
        val timeFormatted = MutableLiveData<String>()
        logMessage("time is $it")
        viewModelScope.launch {

            /*Time to think about how to display the time.
            Lets use the 'timeStampToTime' function on the clock, but
            remember it simulates a long running process and we needed to ensure
            it was main-safe.*/

            //TODO 12 - Create a Coroutine which returns a value from _clock.timeStampToTime, consider
            // which Coroutine builder function to use.
            //TODO 13 - Think about which CoroutineScope to use!! Hint hint!!
            //TODO 14 - Using try/Catch, ensure we catch all possible exceptions and log a message
            // to the console when on is caught.
            //TODO 15 - Modify timeStampToTime to throw an IllegalStateException and run the App.
            //TODO 16 - Modify the scope of the Coroutine and run App again. Note the Apps behaviour!
            val time = _clock.timeStampToTime(it)
            logMessage("currentTimeTransformed time is $time")
            timeFormatted.value = time

        }
        timeFormatted
    }


    init {
        startClock()
    }

    fun onButtonClicked() {
        logMessage("Start onButtonClicked()")
        viewModelScope.launch {
            loadImage()
        }
    }

    fun onCancelClicked() {
        _imageRepository.cancel()
        //TODO 17 - When the user clicks 'Cancel', lets cancel the Clock also...
    }

    private suspend fun loadImage() {
        logMessage("Start loadImage()")
        val imageResource = try {
            _imageRepository.fetchImage(_imageRepository.nextImageId())
        } catch (e: Exception) {
            logMessage("loadImage() exception $e")
            Resource.error(e.localizedMessage ?: "No Message")
        }
        showImage(imageResource)
    }

    private suspend fun showImage(imageResource: Resource<Bitmap>) {
        logMessage("showingImage...")
        withContext(Dispatchers.Main) {
            _image.value = imageResource
        }

    }

    private fun startClock() {
        logMessage("Start startClock()")
        //TODO 11 - We need to launch a Coroutine, so we can start the Clock.
        // Think about which CoroutineScope to use and why?
        _clock.start()
    }


}