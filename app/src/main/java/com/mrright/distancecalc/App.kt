package com.mrright.distancecalc

import android.app.Application
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

	override fun onCreate() {
		super.onCreate()
		Places.initialize(applicationContext, resources.getString(R.string.google_maps_key))
	}

}

