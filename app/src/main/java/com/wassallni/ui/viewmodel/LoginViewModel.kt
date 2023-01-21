package com.wassallni.ui.viewmodel

import android.app.Activity
import android.util.Patterns
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.wassallni.R
import com.wassallni.data.datasource.LoginDataStore
import com.wassallni.data.repository.LoginRepository
import com.wassallni.firebase.authentication.PhoneAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    // Register Fragment LiveData
    var phoneNumber:String?=null

    private val _usernameError = MutableLiveData<Int>()
    val usernameError: LiveData<Int>
        get() = _usernameError

    private val _phoneNumberError = MutableLiveData<Int>()
    val phoneNumberError: LiveData<Int>
        get() = _phoneNumberError

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean>
        get() = _loading

    val verificationCompleted = loginRepository.verificationCompleted
    val verificationFailed = loginRepository.verificationFailed
    val codeSent = loginRepository.codeSent

    // Verification Fragment LiveData
    private val _counter=MutableLiveData<String>()
    val counter:LiveData<String>
    get() = _counter

    private val _timeOut=MutableLiveData<Boolean>(false)
    val timeOut:LiveData<Boolean>
        get() = _timeOut

    fun verifyPhoneNumber(name: String, phone: String) {

        if (!isNameValid(name))
            _usernameError.value = R.string.invalid_username
        else if (!isPhoneNumberValid(phone))
            _phoneNumberError.value = R.string.invalid_phone_number
        else {
            phoneNumber=phone
            _loading.value = true
            loginRepository.verifyPhoneNumber(phone)
        }
    }

    // Validation
    private fun isNameValid(name: String): Boolean {
        return name.trim().length >= 2
    }

    private fun isPhoneNumberValid(phone: String): Boolean {
        return Patterns.PHONE.matcher(phone).matches()
    }

    fun startCounter(){
        _timeOut.value=false
        viewModelScope.launch (Dispatchers.Default){
        var totalSeconds=120
            withContext(Dispatchers.Default) {
                repeat(120) {
                    var minutes = totalSeconds / 60
                    var seconds = totalSeconds % 60
                    _counter.postValue(
                        "${String.format("%02d", minutes)}:${String.format("%02d", seconds)}")
                 //   Log.e("startCounter: ",time.value.toString() )
                    delay(1000)
                    totalSeconds--
                }
            }
            _timeOut.postValue(true)
        }
    }

    fun resendVerificationCode(){
        loginRepository.verifyPhoneNumber(phoneNumber!!)
    }

    fun signInWithFirebase(smsCode:String){
        loginRepository.signInWithFirebase(smsCode)
    }

    companion object {
        fun getFactory(activity: Activity) = object : ViewModelProvider.AndroidViewModelFactory() {

            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {

                    val phoneAuth=PhoneAuth(activity)
                    val loginDataStore=LoginDataStore(phoneAuth)
                    val loginRepository=LoginRepository(loginDataStore)
                    return LoginViewModel(loginRepository) as T
                }
                throw IllegalArgumentException("invalid viewModel")
            }
        }
    }
}
