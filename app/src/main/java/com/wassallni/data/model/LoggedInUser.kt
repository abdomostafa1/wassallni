package com.wassallni.data.model

/**
 * Data class that captures user information for logged in users retrieved start LoginRepository
 */
class LoggedInUser {
    private var token: String = ""
    private var displayName: String = ""

    constructor(token:String,displayName:String){
        this.token=token
        this.displayName=displayName
    }
    fun getToken(): String {
        return token
    }

    fun getName(): String {
        return displayName
    }
}