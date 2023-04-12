package com.wassallni.data.repository

import com.wassallni.data.datasource.SupportDataSource
import javax.inject.Inject

class SupportRepo @Inject constructor(val supportDataSource: SupportDataSource){

    fun sendFeedback(message:String):Boolean{
        return supportDataSource.sendFeedback(message)
    }
}