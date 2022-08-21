package com.wassallni

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class LoginViewModel : ViewModel() {

    var mutableDataCounterDown=MutableLiveData<String>()
    var mutableDataTvResendCodeVisibility=MutableLiveData<Int>()

    private var handler = Handler(Looper.getMainLooper())
    var seconds:Int=45
    companion object {
        var time_Out:Boolean=false
    }

    init {
        mutableDataTvResendCodeVisibility.value=View.INVISIBLE
    }
    fun resetCountDown() {
        //verificationLayout.error = null
        seconds = 45
        mutableDataTvResendCodeVisibility.value=View.INVISIBLE
        time_Out =false
        handler.postDelayed(runnable, 0)
    }

    private val runnable =object : Runnable {
        override fun run() {
            val minutes=0
             mutableDataCounterDown.value= String.format("%02d",minutes)+":"+String.format("%02d",seconds)
             --seconds
            if (seconds != -1)
                countDown()
            else
                notifyTimeOut()
        }
    }

    public fun countDown() {
        handler.postDelayed(runnable, 1000)
    }
    public fun startCounter() {

        handler.postDelayed(runnable, 0)
    }

    private fun notifyTimeOut() {
        handler.removeCallbacks(runnable)
        time_Out =true
        mutableDataTvResendCodeVisibility.value= View.VISIBLE

    }

}