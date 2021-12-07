package com.andriawan.divergent_mobile_apps.services

import com.andriawan.divergent_mobile_apps.utils.Constants.Companion.PREFERENCE_FCM_TOKEN
import com.andriawan.divergent_mobile_apps.utils.Constants.Companion.SHARED_PREFERENCE_NAME
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("New Token $token")
        val sharedPref = getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE)
        sharedPref.edit().putString(PREFERENCE_FCM_TOKEN, token).apply()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Timber.d("Message Received: $remoteMessage")
    }
}