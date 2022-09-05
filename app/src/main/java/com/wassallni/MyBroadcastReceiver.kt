package com.wassallni

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyBroadcastReceiver(var mainView:MyBroadcastObserver): BroadcastReceiver() {
    override fun onReceive(p0: Context?, intent: Intent?) {
        if(intent?.action=="android.location.PROVIDERS_CHANGED")
            notifySubscriber()
    }

    fun notifySubscriber(){
        mainView.onResponse()
    }
}