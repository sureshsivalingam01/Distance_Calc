package com.mrright.distancecalc.di

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseUrl


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MapsApiKey


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Database


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Area


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Country


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class History


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Location


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Truck