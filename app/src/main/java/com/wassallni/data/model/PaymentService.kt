package com.wassallni.data.model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

@JvmSuppressWildcards
interface PaymentService {

    @POST("auth/tokens")
    fun authenticateRequest(@Header("Content-Type") contentType:String ,@Body body:Map<String, Any>): Call<Map<Any, Any>>

    @POST("ecommerce/orders")
    fun orderRegistrationApi(@Header("Content-Type") contentType:String,@Body body: Map<String, Any>): Call<Map<Any, Any>>

    @POST("acceptance/payment_keys")
    fun requestPaymentKey(@Header("Content-Type") contentType:String,@Body body:PaymentRequest): Call<Map<Any, Any>>

}