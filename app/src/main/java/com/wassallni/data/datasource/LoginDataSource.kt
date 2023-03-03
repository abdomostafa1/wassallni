package com.wassallni.data.datasource

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.wassallni.R
import com.wassallni.data.model.LoggedInUser
import com.wassallni.data.repository.VerificationCallbacks
import com.wassallni.firebase.authentication.PhoneAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Inject


class LoginDataSource @Inject constructor(private val phoneAuth: PhoneAuth, @ApplicationContext private val context: Context) :
    VerificationCallbacks {


    private val TAG = "LoginDataSource"
    @Inject
    lateinit var loginService:LoginService
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val _loginUiState= MutableStateFlow<LoginUiState>(LoginUiState.InitialState)
    val loginUiState:StateFlow<LoginUiState>
    get() = _loginUiState

    init {
        phoneAuth.setNotifier(this)
    }


    private var phoneNumber: String? = null

    suspend fun sendVerificationCode(phoneNumber: String) {
        _loginUiState.emit(LoginUiState.Loading)
        this.phoneNumber = phoneNumber
        phoneAuth.sendVerificationCode(phoneNumber)
    }

    suspend fun verifyWithFirebase(smsCode: String) {
        _loginUiState.emit(LoginUiState.Loading)
        val credential = phoneAuth.getCredential(smsCode)
        phoneAuth.verifyWithFirebase(credential)
    }

    override  fun onVerificationCompleted() {
        runBlocking {
        _loginUiState.emit(LoginUiState.VerificationSuccess)
        }
    }

    override  fun onVerificationFailed(error: String) {
        runBlocking {
            _loginUiState.emit(LoginUiState.Error(error))
        }
    }

    override  fun onCodeSent() {
        runBlocking {
        _loginUiState.emit(LoginUiState.CodeSent)
        }
    }

    suspend fun makeLoginRequest(params: HashMap<String, Any>) {

        try {
            _loginUiState.emit(LoginUiState.Loading)
            val task = loginService.login(params).execute()
            if (task.isSuccessful) {
                val responseParams = task.body()
                val name = params["name"] as String
                cacheUserCredential(responseParams!!, name)
                _loginUiState.emit(LoginUiState.LoginSuccess)
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
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.putString("token", token)
        editor.putString("name", name)
        editor.apply()

    }

    private suspend fun handleErrorBody(body: String) {
        val root = JSONObject(body)
        val err = root.getJSONObject("err")
        val errors = err.getJSONObject("errors")
        errors.keys().forEach {
            val errorMsg = "$it is not correct"
            _loginUiState.emit(LoginUiState.Error(errorMsg))
        }

        Log.e(TAG, "Fail: $body")
    }

    private suspend fun handleExceptionError(ex: Exception) {
        var errorMsg: String? = null
        errorMsg = if (ex.message != null)
            ex.message
        else
            context.getString(R.string.error_occurred)

        Log.e(TAG, "catch: ${ex.message}")
        _loginUiState.emit(LoginUiState.Error(errorMsg!!))
    }

    fun resetUiState() {
        _loginUiState.value=LoginUiState.InitialState


    }

}

sealed class LoginUiState(){
    object InitialState:LoginUiState()
    object Loading:LoginUiState()
    object CodeSent:LoginUiState()
    object VerificationSuccess:LoginUiState()
    object LoginSuccess:LoginUiState()
    data class Error(val errorMsg:String):LoginUiState()
}
interface LoginService {
    @POST("signUp")
    @JvmSuppressWildcards
    fun login(@Body body: Map<String, Any>): Call<Map<String, Any>>
}
