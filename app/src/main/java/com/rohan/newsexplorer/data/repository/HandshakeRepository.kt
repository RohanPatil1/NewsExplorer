package com.rohan.newsexplorer.data.repository

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.rohan.newsexplorer.R
import java.util.concurrent.TimeUnit


object HandshakeRepository {

    val instance = FirebaseRemoteConfig.getInstance()

//    private val _handshakeLiveData = MutableLiveData<HandshakeData>()
//    val handshakeLiveData: LiveData<HandshakeData> = _handshakeLiveData

    fun init() {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(12)
            .build()
        instance.setConfigSettingsAsync(configSettings)
        instance.setDefaultsAsync(R.xml.remote_config_defaults)
        fetch()
        Log.d("Application", "Handshake Init Done")
    }

    private fun fetch() {
        val fetch = instance.fetch(TimeUnit.HOURS.toSeconds(12))
        fetch.addOnCompleteListener {
            if (it.isSuccessful) {
                instance.activate()
                Log.d("Application", "Fetch and activate succeeded")

            } else {
                Log.d("Application", "Fetch failed")
            }
        }
    }

    fun getDiscoverTitle(): String = instance.getString("discover_title")

    fun getTipTitle(): String = instance.getString("tip_title")

    fun isDiscoverEnabled(): Boolean = instance.getBoolean("is_discover_enabled")

}