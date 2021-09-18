package com.mrright.distancecalc.di

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
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
        fireStore: FirebaseFirestore,
    ) = fireStore.collection(Collection.DISTANCE_CALCULATOR.value)
        .document(DOCUMENT_ID)


    @Area
    @Provides
    @Singleton
    fun provideArea(
        @Database
        db: DocumentReference,
    ): CollectionReference = db.collection(Collection.AREA.value)


    @Country
    @Provides
    @Singleton
    fun provideCountry(
        @Database
        db: DocumentReference,
    ): CollectionReference = db.collection(Collection.COUNTRY.value)


    @History
    @Provides
    @Singleton
    fun provideHistory(
        @Database
        db: DocumentReference,
    ): CollectionReference = db.collection(Collection.HISTORY.value)


    @Location
    @Provides
    @Singleton
    fun provideLocation(
        @Database
        db: DocumentReference,
    ): CollectionReference = db.collection(Collection.LOCATION.value)


    @Truck
    @Provides
    @Singleton
    fun provideTruck(
        @Database
        db: DocumentReference,
    ): CollectionReference = db.collection(Collection.TRUCK.value)


}