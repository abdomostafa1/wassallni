 package com.wassallni.firebase.authentication

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Icon
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.wassallni.R
import com.wassallni.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var preferences: SharedPreferences
    private val channelId = "driver rate channel"

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("TAG", "From: ${message.from}")

        sendNotification(message)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val editor = preferences.edit()
        editor.putString("fcmToken", token)
        editor.apply()
    }

    private fun sendNotification(message: RemoteMessage) {
        val data = message.data
        val type = data["type"] as String
        when (type) {
            "rateDriver" -> {
                val intent = createRateIntent(data)
                val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                else
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                val title = getString(R.string.Thank_you_for_using_app)
                val contentText = getString(R.string.rate_trip_driver)
                val notification = createRateNotification(pendingIntent, title, contentText)
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notificationId = 12345
                notificationManager.notify(notificationId, notification)

            }
            "driverArrival" -> {
                createDriverArrivalNotification()
            }
            else -> return
        }
    }

    private fun createDriverArrivalNotification() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("rateDriverIntent",false)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_IMMUTABLE)
        else
            PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val title = getString(R.string.dear_customer)
        val contentText = getString(R.string.driver_arrived_station)
        val notification = createRateNotification(pendingIntent, title, contentText)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1234567
        notificationManager.notify(notificationId, notification)

    }

    private fun createRateIntent(data: MutableMap<String, String>): Intent {
        val tripId = data["tripId"] as String
        val driverId = data["driverId"] as String
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra("rateDriverIntent", true)
        intent.putExtra("tripId", tripId)
        intent.putExtra("driverId", driverId)
        return intent
    }

    private fun createRateNotification(
        pendingIntent: PendingIntent,
        title: String,
        contentText: String
    ): Notification {

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(contentText)
            )
            .setContentText(contentText)
        notificationBuilder.setContentTitle(title)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationBuilder.setLargeIcon(
                Icon.createWithResource(
                    this,
                    R.drawable.bus
                )
            )
        }
        return notificationBuilder.build()
    }

}
