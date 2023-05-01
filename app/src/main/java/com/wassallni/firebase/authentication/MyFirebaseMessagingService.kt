package com.wassallni.firebase.authentication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Icon
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.RemoteViews.RemoteView
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
    lateinit var  preferences: SharedPreferences
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("TAG", "From: ${message.from}")

        sendNotification(message)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val editor=preferences.edit()
        editor.putString("fcmToken",token)
    }

    private fun sendNotification(message: RemoteMessage) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra("rateDriverIntent",true)
        intent.putExtra("tripId","tripId:12345")
        intent.putExtra("driverId","driverId:678910")
        val pendingIntent= PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_IMMUTABLE)
        val channelId = "fcm_default_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH)
            channel.description="channel description"
            notificationManager.createNotificationChannel(channel)
        }


        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setSmallIcon(R.drawable.ic_feedback)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("يسطا انت فين رد يجدع عيب وربنا هزعلك مني علي الاخر براحتك"))
            .setContentText("يسطا انت فين رد")
        notificationBuilder.setContentTitle("عبدو مصطفي")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationBuilder.setLargeIcon(Icon.createWithResource(this,R.drawable.profile_image))
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

}
