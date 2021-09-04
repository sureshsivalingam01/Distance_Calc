package com.mrright.distancecalc.di

import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.firestore.DocumentReference
import com.mrright.distancecalc.data.api.GoogleMapsService
import com.mrright.distancecalc.data.api.repositories.MapsRepoImpl
import com.mrright.distancecalc.data.api.repositories.MapsRepository
import com.mrright.distancecalc.data.api.repositories.PlacesRepoImpl
import com.mrright.distancecalc.data.api.repositories.PlacesRepository
import com.mrright.distancecalc.data.firestore.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object RepoModule {

	@Provides
	@ViewModelScoped
	fun provideMapsRepo(
		service : GoogleMapsService,
		@MapsApiKey
		mapsApiKey : String,
	) : MapsRepository = MapsRepoImpl(service, mapsApiKey)


	@Provides
	@ViewModelScoped
	fun providePlacesRepo(
		placesClient : PlacesClient,
	) : PlacesRepository = PlacesRepoImpl(placesClient)


	@Provides
	@ViewModelScoped
	fun provideDbRepo(
		@Database
		db : DocumentReference,
	) : DbRepository = DbRepoImpl(db)

	@Provides
	@ViewModelScoped
	fun provideAreaRepo(
		@Database
		db : DocumentReference,
	) : AreaRepository = AreaRepoImpl(db)


	@Provides
	@ViewModelScoped
	fun provideLocationRepo(
		@Database
		db : DocumentReference,
	) : LocationRepository = LocationRepoImpl(db)


	@Provides
	@ViewModelScoped
	fun provideTruckRepo(
		@Database
		db : DocumentReference,
	) : TruckRepository = TruckRepoImpl(db)

}