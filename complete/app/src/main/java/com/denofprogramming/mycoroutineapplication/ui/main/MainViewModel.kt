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

            try{
                val deferred = viewModelScope.async {
                //  val deferred = async { //subtle difference
                    _clock.timeStampToTime(it)
                }
                val time = deferred.await()
                logMessage("currentTimeTransformed time is $time")
                timeFormatted.value = time
            }catch(e :Exception){
                logMessage("Oops! Problem getting timestamp: ${e.localizedMessage}")
            }

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
        _clock.cancel()
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
        viewModelScope.launch {
            _clock.start()
        }
    }


}