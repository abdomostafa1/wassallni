package com.wassallni.data.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
class LoggedInUser {
    private var token: String = ""
    private var displayName: String = ""

    private constructor()

    companion object {
    private var instance: LoggedInUser? = null
        fun getInstance(displayName: String = "", token: String = ""): LoggedInUser {
            if (instance == null) {
                instance = LoggedInUser()
                instance!!.displayName = displayName
                instance!!.token = token
            }
            return instance as LoggedInUser
        }

    }
    fun getToken(): String {
        return token
    }

    fun getName(): String {
        return displayName
    }
}