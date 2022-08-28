package com.wassallni

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyBroadcastReceiver: BroadcastReceiver() {
    var subscribers=ArrayList<MapsFragment>()
    override fun onReceive(p0: Context?, intent: Intent?) {
        if(intent?.action=="android.location.PROVIDERS_CHANGED")
            notifySubscribers()
    }
    fun addSubscriber(sub:MapsFragment){
        subscribers.add(sub)
    }
    fun removeSubscriber(sub:MapsFragment){
        subscribers.remove(sub)
    }
    fun notifySubscribers(){
        for(sub in subscribers)
            sub.getLocation()
    }
}