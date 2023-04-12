package com.wassallni.data.datasource

import com.wassallni.data.model.LoggedInUser
import com.wassallni.data.model.TripService
import javax.inject.Inject

class SupportDataSource @Inject constructor(val tripService: TripService,val loggedInUser: LoggedInUser) {

    fun sendFeedback(message:String):Boolean{
        val token=loggedInUser.getToken()
        val time=System.currentTimeMillis()/1000
        val map=HashMap<String,Any>()
        map["message"]=message
        map["date"]=time
        val request=tripService.sendFeedback(token,map).execute()
        if (request.isSuccessful)
            return true
        else
            throw Exception(request.errorBody()?.string())

    }
}