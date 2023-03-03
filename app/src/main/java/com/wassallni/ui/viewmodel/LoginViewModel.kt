package com.wassallni.ui.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Patterns
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.wassallni.R
import com.wassallni.data.datasource.LoginDataSource
import com.wassallni.data.repository.LoginRepository
import com.wassallni.firebase.authentication.PhoneAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) : ViewModel() {

    // Register Fragment LiveData
    var phoneNumber: String? = null
    var name: String? = null

    private val _usernameError = MutableStateFlow<Int>(0)
    val usernameError: StateFlow<Int>
        get() = _usernameError

    private val _phoneNumberError = MutableStateFlow<Int>(0)
    val phoneNumberError: StateFlow<Int>
        get() = _phoneNumberError

    val loginUiState = loginRepository.loginUiState

    // Verification Fragment LiveData
    private val _counter = MutableStateFlow<String>("")
    val counter: StateFlow<String>
        get() = _counter

    private val _timeOut = MutableStateFlow<Boolean>(false)
    val timeOut: StateFlow<Boolean>
        get() = _timeOut

    suspend fun verifyPhoneNumber(name: String, phone: String) {

        if (!isNameValid(name))
            _usernameError.emit(R.string.invalid_username)
        else if (!isPhoneNumberValid(phone))
            _phoneNumberError.emit(R.string.invalid_phone_number)
        else {
            this.name = name
            phoneNumber = phone
            loginRepository.sendVerificationCode(phone)
        }
    }

    // Validation
    private fun isNameValid(name: String): Boolean {
        return name.trim().length >= 2
    }

    private fun isPhoneNumberValid(phone: String): Boolean {
        return Patterns.PHONE.matcher(phone).matches()
    }

    @SuppressLint("SuspiciousIndentation")
    suspend fun startCounter() {
        _timeOut.value = false
        var totalSeconds = 120
        var minutes: Int
        var seconds: Int

        repeat(120) {
            minutes = totalSeconds / 60
            seconds = totalSeconds % 60
            _counter.emit(
                "${String.format("%02d", minutes)}:${String.format("%02d", seconds)}"
            )
            delay(1000)
            totalSeconds--
        }
        _timeOut.emit(true)

    }

    suspend fun resendVerificationCode() {
        loginRepository.sendVerificationCode(phoneNumber!!)
    }

    suspend fun makeLoginRequest() {
        loginRepository.makeLoginRequest(name!!, phoneNumber!!)
    }

    suspend fun verifyWithFirebase(smsCode: String) {
        loginRepository.verifyWithFirebase(smsCode)
    }

    fun resetUiState() {
        _usernameError.value=0
        _phoneNumberError.value=0
        loginRepository.resetUiState()
    }

}
