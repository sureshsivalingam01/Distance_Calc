package com.mrright.distancecalc

import android.app.Application
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

	lateinit var id : String

	override fun onCreate() {
		super.onCreate()
		Places.initialize(applicationContext, resources.getString(R.string.google_maps_key))
		//getId()
	}

	@JvmName("getId1")
	fun getId() = id

	@JvmName("setId1")
	fun setId(id : String) {
		this.id = id
	}

}