package com.wassallni.ui.fragment.main_graph

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.paymob.acceptsdk.IntentConstants
import com.paymob.acceptsdk.PayActivity
import com.paymob.acceptsdk.PayActivityIntentKeys
import com.paymob.acceptsdk.PayResponseKeys
import com.paymob.acceptsdk.SaveCardResponseKeys
import com.paymob.acceptsdk.ThreeDSecureWebViewActivty
import com.paymob.acceptsdk.ToastMaker
import com.wassallni.BuildConfig
import com.wassallni.R
import com.wassallni.databinding.FragmentTempBinding
import com.wassallni.ui.viewmodel.BookVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking


private const val TAG = "TempFragment"
class TempFragment : Fragment() {

    lateinit var binding:FragmentTempBinding
    private val bookVM: BookVM by activityViewModels()

    private val paymentLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        handlePaymentResult(it)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentTempBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pay.setOnClickListener {
            runBlocking {
//                val task = async(Dispatchers.IO) {
//                    val apiKey = BuildConfig.PAYMOB_API_KEY
//                    bookVM.getPaymentKey(apiKey)
//                }
//                val paymentKey = task.await()
//
//                openPaymentActivity(paymentKey)

            }

        }
    }
    private fun openPaymentActivity(paymentKey: String) {
        val intent = Intent(requireActivity(), PayActivity::class.java)
        intent.putExtra(PayActivityIntentKeys.PAYMENT_KEY, paymentKey)
        intent.putExtra(PayActivityIntentKeys.THREE_D_SECURE_ACTIVITY_TITLE, "Verification")
        intent.putExtra(PayActivityIntentKeys.SAVE_CARD_DEFAULT, false)
        intent.putExtra(PayActivityIntentKeys.SHOW_SAVE_CARD, false)

        intent.putExtra(
            PayActivityIntentKeys.THEME_COLOR,
            ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark)
        )
        intent.putExtra("ActionBar", false)
        intent.putExtra("language", "ar")
        paymentLauncher.launch(intent)
        val secureIntent= Intent(requireActivity(), ThreeDSecureWebViewActivty::class.java)
        secureIntent.putExtra("ActionBar",false)
    }


    private fun handlePaymentResult(result: ActivityResult) {
            val extras: Bundle? = result.data?.extras
            val resultCode = result.resultCode
            if (resultCode == IntentConstants.USER_CANCELED) {
                // User canceled and did no payment request was fired
                log("User canceled!!")
                ToastMaker.displayShortToast(requireActivity(), getString(R.string.user_canceled));
            } else if (resultCode == IntentConstants.MISSING_ARGUMENT) {
                log("MISSING_ARGUMENT")
                // You forgot to pass an important key-value pair in the intent's extras
                ToastMaker.displayShortToast(requireActivity(), "Missing Argument == " + extras?.getString(
                    IntentConstants.MISSING_ARGUMENT_VALUE));
            } else if (resultCode == IntentConstants.TRANSACTION_ERROR) {
                ToastMaker.displayLongToast(requireActivity(),getString(R.string.TRANSACTION_ERROR))
                // An error occurred while handling an API's response
                ToastMaker.displayShortToast(requireActivity(), "Reason == " + extras?.getString(IntentConstants.TRANSACTION_ERROR_REASON));
            } else if (resultCode == IntentConstants.TRANSACTION_REJECTED) {
                // User attempted to pay but their transaction was rejected
                extras?.getString(PayResponseKeys.DATA_MESSAGE)?.let { log(it) }
                // Use the static keys declared in PayResponseKeys to extract the fields you want
                ToastMaker.displayShortToast(requireActivity(), getString(R.string.TRANSACTION_REJECTED));
            } else if (resultCode == IntentConstants.TRANSACTION_REJECTED_PARSING_ISSUE) {
                log("TRANSACTION_REJECTED_PARSING_ISSUE")
                // User attempted to pay but their transaction was rejected. An error occured while reading the returned JSON
                ToastMaker.displayShortToast(requireActivity(), extras?.getString(IntentConstants.RAW_PAY_RESPONSE));
            } else if (resultCode == IntentConstants.USER_CANCELED_3D_SECURE_VERIFICATION) {
                log("USER_CANCELED_3D_SECURE_VERIFICATION")
                ToastMaker.displayShortToast(requireActivity(), "User canceled 3-d secure verification!!");

                // Note that a payment process was attempted. You can extract the original returned values
                // Use the static keys declared in PayResponseKeys to extract the fields you want
                ToastMaker.displayShortToast(requireActivity(), extras?.getString(PayResponseKeys.PENDING));
            } else if (resultCode == IntentConstants.TRANSACTION_SUCCESSFUL) {
                onSuccessfulTransaction()
                log("TRANSACTION_SUCCESSFUL")
                // User finished their payment successfully
                // Use the static keys declared in PayResponseKeys to extract the fields you want
                ToastMaker.displayShortToast(requireActivity(), extras?.getString(PayResponseKeys.DATA_MESSAGE));
            } else if (resultCode == IntentConstants.TRANSACTION_SUCCESSFUL_PARSING_ISSUE) {
                onSuccessfulTransaction()
                log("TRANSACTION_SUCCESSFUL_PARSING_ISSUE")
                // User finished their payment successfully. An error occured while reading the returned JSON.
                ToastMaker.displayShortToast(requireActivity(), "TRANSACTION_SUCCESSFUL - Parsing Issue");

                // ToastMaker.displayShortToast(this, extras.getString(IntentConstants.RAW_PAY_RESPONSE));
            } else if (resultCode == IntentConstants.TRANSACTION_SUCCESSFUL_CARD_SAVED) {
                onSuccessfulTransaction()
                log("TRANSACTION_SUCCESSFUL_CARD_SAVED")
                // User finished their payment successfully and card was saved.

                // Use the static keys declared in PayResponseKeys to extract the fields you want
                // Use the static keys declared in SaveCardResponseKeys to extract the fields you want
                ToastMaker.displayShortToast(requireActivity(), "Token == " + extras?.getString(
                    SaveCardResponseKeys.TOKEN));
            } else if (resultCode == IntentConstants.USER_CANCELED_3D_SECURE_VERIFICATION_PARSING_ISSUE) {
                onSuccessfulTransaction()
                log("USER_CANCELED_3D_SECURE_VERIFICATION_PARSING_ISSUE")
                ToastMaker.displayShortToast(requireActivity(), "User canceled 3-d scure verification - Parsing Issue!!");

                // Note that a payment process was attempted.
                // User finished their payment successfully. An error occured while reading the returned JSON.
                ToastMaker.displayShortToast(requireActivity(), extras?.getString(IntentConstants.RAW_PAY_RESPONSE));
            }
        }

    fun log(message:String){
        Log.e(tag, "log: $message", )
    }

    private fun onSuccessfulTransaction(){
        ToastMaker.displayShortToast(requireActivity(), getString(R.string.TRANSACTION_SUCCESSFUL));
    }
}