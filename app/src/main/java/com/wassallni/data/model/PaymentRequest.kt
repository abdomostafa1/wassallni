package com.wassallni.data.model

import com.squareup.moshi.Json

data class PaymentRequest (
    @Json(name = "auth_token")
    val authToken: String,

    @Json(name = "amount_cents")
    val amountCents: String,

    val expiration: Long,

    @Json(name = "order_id")
    val orderID: String,

    @Json(name = "billing_data")
    val billingData: BillingData,

    val currency: String="EGP",

    @Json(name = "integration_id")
    val integrationID: Long,

)

data class BillingData (
    val apartment: String="NA",
    val email: String,
    val floor: String="NA",

    @Json(name = "first_name")
    val firstName: String,

    val street: String="NA",
    val building: String="NA",

    @Json(name = "phone_number")
    val phoneNumber: String,

    @Json(name = "shipping_method")
    val shippingMethod: String="NA",

    @Json(name = "postal_code")
    val postalCode: String="NA",

    val city: String="NA",
    val country: String="NA",

    @Json(name = "last_name")
    val lastName: String,

    val state: String="NA"
)

