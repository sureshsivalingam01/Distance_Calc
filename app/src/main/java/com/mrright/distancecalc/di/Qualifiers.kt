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