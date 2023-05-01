package com.wassallni.firebase.authentication

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (message.data.isNotEmpty()) {
            showDialog(message.toString())
        }
    }

    private fun showDialog(str:String){

        Log.e("remote message ", str )

    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }

    override fun onMessageSent(msgId: String) {
        super.onMessageSent(msgId)
    }

    override fun onSendError(msgId: String, exception: Exception) {
        super.onSendError(msgId, exception)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}

/*
private fun getLocation(callback: (latlng: LatLng) -> Unit) {
    Log.e("getLocation() ", " do it ")

    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    )

        return

    fusedLocationProviderClient.getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY,
        object : CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                CancellationTokenSource().token

            override fun isCancellationRequested() = false
        })
        .addOnSuccessListener { location: Location? ->
            if (location == null)
                Toast.makeText(this, "open your fucking Gps now", Toast.LENGTH_SHORT).show()
            else {
                val lat = location.latitude
                val lng = location.longitude

                val latLng = LatLng(lat, lng)
                currentLocation= PlaceInfo(latLng)
                callback.invoke(latLng)
            }

        }

}

        val builder = AlertDialog.Builder(this)

        builder.setMessage("our service isn't available you are out of our range but we will do it soon")
            .setTitle("Sorry")

        builder.setIcon(R.drawable.ic_unavailable)
        builder.setPositiveButton("ok", DialogInterface.OnClickListener() { dialog, which ->
            dialog.dismiss()
        })

        builder.show()

 private fun showUserLocationOnMap(latLng: LatLng, durationMs: Int) {

        Log.e("showUserLocationOnMap ", " do it ")
        //mMap.addMarker(MarkerOptions().position(latLng).title("Marker in zawiaa"))
        var ffll = CameraUpdateFactory.newLatLngZoom(latLng, 15.0f)

        googleMap.animateCamera(ffll, durationMs, object : GoogleMap.CancelableCallback {
            override fun onCancel() {}
            override fun onFinish() {}
        })

    }

 */