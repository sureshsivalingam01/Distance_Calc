package com.mrright.distancecalc.data.api


import com.mrright.distancecalc.data.api.responses.DirectionsDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleMapsService {

	@GET("maps/api/directions/json")
	suspend fun getDirection(
		@Query("mode") mode : String,
		@Query("origin") origin : String,
		@Query("destination") destination : String,
		@Query("key") key : String,
		@Query("alternatives") alternatives : Boolean,
	) : Response<DirectionsDTO>

}

