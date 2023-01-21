package com.wassallni.data.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wassallni.data.repository.VerificationCallbacks
import com.wassallni.firebase.authentication.PhoneAuth

class LoginDataStore(private val phoneAuth: PhoneAuth) : VerificationCallbacks {

    init {
        phoneAuth.setNotifier(this)
    }

    private var phoneNumber: String? = null
    private val _verificationCompleted = MutableLiveData<Boolean>(false)
    val verificationCompleted: LiveData<Boolean>
        get() = _verificationCompleted

    private val _codeSent = MutableLiveData<Boolean>(false)
    val codeSent: LiveData<Boolean>
        get() = _codeSent

    private val _verificationFailed = MutableLiveData<String>()
    val verificationFailed: LiveData<String>
        get() = _verificationFailed

    fun verifyPhoneNumber(phoneNumber: String) {
        this.phoneNumber=phoneNumber
        phoneAuth.verifyPhoneNumber(phoneNumber)
    }

    fun signInWithFirebase(smsCode: String) {
        val credential = phoneAuth.getCredential(smsCode)
        phoneAuth.signInWithFirebase(credential)
    }

    override fun onVerificationCompleted() {
        _verificationCompleted.value = true
    }

    override fun onVerificationFailed(error: String) {
        _verificationFailed.value = error
    }

    override fun onCodeSent() {
        _codeSent.value = true

    }
}