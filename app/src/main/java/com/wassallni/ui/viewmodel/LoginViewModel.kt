package com.wassallni.ui.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) :
    ViewModel() {

    // Register Fragment LiveData
    var phoneNumber: String? = null
    var name: String? = null

    private val _usernameError = MutableStateFlow<Int>(0)
    val usernameError = _usernameError.asStateFlow()

    private val _phoneNumberError = MutableStateFlow<Int>(0)
    val phoneNumberError = _phoneNumberError.asStateFlow()

    val loginUiState = loginRepository.loginUiState

    // Verification Fragment LiveData
    private val _counter = MutableStateFlow<String>("")
    val counter = _counter.asStateFlow()

    private val _timeOut = MutableStateFlow<Boolean>(false)
    val timeOut = _timeOut.asStateFlow()

    suspend fun verifyPhoneNumber(name: String, phone: String, activity: Activity) {

        if (!isNameValid(name))
            _usernameError.emit(R.string.invalid_username)
        else if (!isPhoneNumberValid(phone))
            _phoneNumberError.emit(R.string.invalid_phone_number)
        else {
            this.name = name
            phoneNumber = phone
            loginRepository.sendVerificationCode(phone, activity)
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

    suspend fun resendVerificationCode(activity: Activity) {
        loginRepository.sendVerificationCode(phoneNumber!!, activity)
    }

    suspend fun makeLoginRequest() {
        Log.e("TAG", "name:$name ")
        Log.e("TAG", "phone:$phoneNumber ")
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.makeLoginRequest(name!!, phoneNumber!!)
        }

    }

    suspend fun verifyWithFirebase(smsCode: String, activity: Activity) {
        loginRepository.verifyWithFirebase(smsCode, activity)
    }

    fun resetUiState() {
        _usernameError.value = 0
        _phoneNumberError.value = 0
        loginRepository.resetUiState()
    }

}
