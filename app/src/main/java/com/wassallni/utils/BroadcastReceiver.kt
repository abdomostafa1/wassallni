package com.wassallni.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BroadcastReceiver(private var mainView: BroadcastObserver): BroadcastReceiver() {
    override fun onReceive(p0: Context?, intent: Intent?) {
        if(intent?.action=="android.location.PROVIDERS_CHANGED")
            notifySubscriber()
    }

    private fun notifySubscriber(){
        mainView.onGpsBroadcastResponse()
    }
}

interface BroadcastObserver {
     fun onGpsBroadcastResponse()
}
