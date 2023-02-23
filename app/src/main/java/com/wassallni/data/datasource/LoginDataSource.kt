package com.wassallni.data.datasource

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.wassallni.R
import com.wassallni.data.model.LoggedInUser
import com.wassallni.data.repository.VerificationCallbacks
import com.wassallni.firebase.authentication.PhoneAuth
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


class LoginDataSource(private val phoneAuth: PhoneAuth, private val context: Context) :
    VerificationCallbacks {


    private val TAG = "LoginDataSource"

    init {
        phoneAuth.setNotifier(this)
    }

    private val ngrok="https://15c8-41-68-221-8.eu.ngrok.io"
    private val url = "$ngrok/api/v1/users/"
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()


    private val loginService: LoginService = retrofit.create(LoginService::class.java)

    private var phoneNumber: String? = null

    private val _verificationCompleted = MutableLiveData<Boolean>(false)
    val verificationCompleted: LiveData<Boolean>
        get() = _verificationCompleted

    private val _loginCompleted = MutableLiveData<Boolean>()
    val loginCompleted: LiveData<Boolean>
        get() = _loginCompleted

    private val _codeSent = MutableLiveData<Boolean>(false)
    val codeSent: LiveData<Boolean>
        get() = _codeSent

    private val _onFailure = MutableLiveData<String>()
    val onFailure: LiveData<String>
        get() = _onFailure

    fun sendVerificationCode(phoneNumber: String) {
        this.phoneNumber = phoneNumber
        phoneAuth.sendVerificationCode(phoneNumber)
    }

    fun verifyWithFirebase(smsCode: String) {
        val credential = phoneAuth.getCredential(smsCode)
        phoneAuth.verifyWithFirebase(credential)
    }

    override fun onVerificationCompleted() {
        _verificationCompleted.value = true
    }

    override fun onVerificationFailed(error: String) {
        _onFailure.value = error
    }

    override fun onCodeSent() {
        _codeSent.value = true
    }

     fun makeLoginRequest(params: HashMap<String, Any>) {

        try {
            val task = loginService.login(params).execute()
            if (task.isSuccessful) {
                val responseParams = task.body()
                val name = params["name"] as String
                cacheUserCredential(responseParams!!, name)
                _loginCompleted.postValue(true)
            } else {
                val body: String? = task.errorBody()?.string()
                if (body != null) {
                    handleErrorBody(body)
                }
            }

        } catch (ex: Exception) {
            handleExceptionError(ex)
        }
    }

    private fun cacheUserCredential(responseParams: Map<String, Any>, name: String) {
        val token = responseParams["token"] as String
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn",true)
        editor.putString("token", token)
        editor.putString("name", name)
        editor.apply()

        LoggedInUser.getInstance(name, token)
    }

    private fun handleErrorBody(body: String) {
        val root = JSONObject(body)
        val err = root.getJSONObject("err")
        val errors = err.getJSONObject("errors")
        errors.keys().forEach {
            val errorMsg = "$it is not correct"
            _onFailure.postValue(errorMsg)
        }

        Log.e(TAG, "Fail: $body")
    }

    private fun handleExceptionError(ex: Exception) {
        var errorMsg: String? = null
        errorMsg = if (ex.message != null)
            ex.message
        else
            context.getString(R.string.error_occurred)

        Log.e(TAG, "catch: ${ex.message}")
        _onFailure.postValue(errorMsg!!)
    }

}

interface LoginService {
    @POST("signUp")
    @JvmSuppressWildcards
    fun login(@Body body: Map<String, Any>): Call<Map<String, Any>>
}
