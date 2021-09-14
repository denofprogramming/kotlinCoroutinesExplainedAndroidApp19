package com.denofprogramming.mycoroutineapplication.repository.time

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.denofprogramming.mycoroutineapplication.shared.Resource
import com.denofprogramming.mycoroutineapplication.shared.uilt.logMessage
import kotlinx.coroutines.*
import java.util.*


class DefaultClock {


    val time: LiveData<Resource<Long>>
        get() = _time

    private var _job = Job()
    private val _time = MutableLiveData<Resource<Long>>().apply { value = Resource.none() }


    fun cancel() {
        _job.cancel(CancellationException("The end of Time!!"))
    }


    suspend fun start() {
        try {
            withContext((Dispatchers.Default + _job)) {
                while (isActive) {
                    tikTok()
                }
            }
        } catch (ce: CancellationException) {
            logMessage("Clock Cancelled with ${ce.localizedMessage} ")
        }

    }

    private suspend fun tikTok() {
        System.currentTimeMillis().run {
            logMessage("time is: $this")
            notifyTime(Resource.success(this))
        }
        delay(1000)
    }

    suspend fun timeStampToTime(timestamp: Resource<Long>): String =
        withContext(Dispatchers.Default) {
            delay(50) // simulate a long calculation...
            timestamp.data?.let {
                val date = Date(it)
                //throw IllegalStateException("Time ran out!!")
                val convertedTime = SimpleDateFormat("HH:mm:ss").format(date)
                logMessage("time is: $convertedTime")
                return@withContext convertedTime
            }
            return@withContext timestamp.message
        }


    private suspend fun notifyTime(resource: Resource<Long>) {
        withContext(Dispatchers.Main) {
            logMessage("notifyTime: $resource")
            _time.value = resource
        }
    }


    companion object {
        fun build(): DefaultClock {
            return DefaultClock()
        }
    }

}