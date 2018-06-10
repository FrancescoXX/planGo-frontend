package com.francescoxx.plango.frontend.remote;

import com.francescoxx.plango.frontend.model.Daytrip;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/*
    Client-Service model
    Contains all the Calls to Server @RestController
    thanks retrofit
 */
public interface DaytripService {

    // {userId} is the userId to pass to get HIS Daytrips
    @GET("/users/jwtusers/{userId}/daytrips")
    Call<List<Daytrip>> getAllDayTrips(@Path("userId") int userId);

    @POST("/users/jwtusers/{userId}/daytrips")
    Call<Void> createDaytrip(@Path("userId") int userId, @Body Daytrip daytrip);

    @DELETE("/users/jwtusers/{userId}/daytrips/{daytripId}")
    Call<Void> deleteDaytrip(@Path("userId") int userId, @Path("daytripId") int daytripId);
}
