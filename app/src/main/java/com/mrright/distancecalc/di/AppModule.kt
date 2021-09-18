package com.mrright.distancecalc.di

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.mrright.distancecalc.BuildConfig
import com.mrright.distancecalc.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


	@MapsApiKey
	@Provides
	@Singleton
	fun provideGoogleMapsKey(
		@ApplicationContext
		context : Context,
	) : String = context.getString(R.string.google_maps_key)


	@BaseUrl
	@Provides
	@Singleton
	fun provideBaseUrl() : String = BuildConfig.BASE_URL


    @Provides
    @Singleton
    fun providePlacesClient(
        @ApplicationContext
        context: Context,
    ): PlacesClient = Places.createClient(context)


    @Provides
    @Singleton
    fun provideGeocoder(
        @ApplicationContext
        context: Context
    ): Geocoder = Geocoder(context)


    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(
        @ApplicationContext
        context: Context
    ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)


}