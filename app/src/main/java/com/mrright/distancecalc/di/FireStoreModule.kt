package com.mrright.distancecalc.di

import com.google.firebase.firestore.FirebaseFirestore
import com.mrright.distancecalc.utils.constants.Collection
import com.mrright.distancecalc.utils.constants.DOCUMENT_ID
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FireStoreModule {


	@Database
	@Provides
	@Singleton
	fun provideDb(
		fireStore : FirebaseFirestore,
	) = fireStore.collection(Collection.DISTANCE_CALCULATOR.value).document(DOCUMENT_ID)


}