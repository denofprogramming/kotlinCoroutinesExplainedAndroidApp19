package com.denofprogramming.mycoroutineapplication.repository.time

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.denofprogramming.mycoroutineapplication.shared.Resource
import com.denofprogramming.mycoroutineapplication.shared.uilt.logMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.util.*


class DefaultClock {


    val time: LiveData<Resource<Long>>
        get() = _time

    // If we want to cancel 'the Clock' from running we could do it via this Job.
    private var _job = Job()
    private val _time = MutableLiveData<Resource<Long>>().apply { value = Resource.none() }


    //TODO - 10 At some point we want to Cancel the clock from running lets implement it here.
    fun cancel() {
    }

    //TODO - 1 The start function needs to support Coroutines, so lets first make it suspendable.
    //TODO - 2 We what to ensure our start function is 'main-safe', lets switch the Thread context.
    //TODO - 3 We need our context to contain a Job that maybe Cancelled at some time.
    //TODO - 4 We need to always ensure our suspendable functions are 'cooperative' with Coroutines.
    //TODO - 5 Lets capture any CancellationExceptions and logMessage to the console.
    fun start() {
        tikTok()
    }

    //TODO - 6 Our tikTok function needs to support Coroutines and therefore needs to be suspendable
    // as we want to delay(1000) or 1 second for each iteration.
    private fun tikTok() {
        System.currentTimeMillis().run {
            logMessage("time is: $this")
            notifyTime(Resource.success(this))
        }
        //TODO - 7 We need to delay out 'tikTok' by 1000 milliseconds (1 second)
        //delay(1000)
    }

    //timeStampToTime simulates a long running process, the delay(50) simulates this.
    //TODO 8 - Lets make Coroutine friendly and 'main-safe'.
    fun timeStampToTime(timestamp: Resource<Long>): String {

        delay(50) // simulate a long calculation...
        timestamp.data?.let {
            val date = Date(it)
            //TODO 15 - Lets simulate a failure by throwing the IllegalStateException.
            // Just uncomment the IllegalStateException code.
            //throw IllegalStateException("Time ran out!!")
            val convertedTime = SimpleDateFormat("HH:mm:ss").format(date)
            logMessage("time is: $convertedTime")
            return convertedTime
        }
        return timestamp.message
    }


    //TODO - 9 We need to ensure our notifyTime is main-safe, we want to use .value NOT postValue on
    // the LiveData variable _time. Therefore, we need to ensure our notifyTime supports Coroutines.
    private fun notifyTime(resource: Resource<Long>) {
        logMessage("notifyTime: $resource")
        _time.value = resource
    }


    companion object {
        fun build(): DefaultClock {
            return DefaultClock()
        }
    }

}