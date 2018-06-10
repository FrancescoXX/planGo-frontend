package com.francescoxx.plango.frontend.remote;

import com.francescoxx.plango.frontend.model.Step;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface StepService {

    //GET-ALL {userId} is the userId to pass to get HIS Daytrips
    @GET("/users/jwtusers/{userId}/daytrips/{daytripId}/steps")
    Call<List<Step>> getAll(@Path("userId") int userId, @Path("daytripId") int daytripId );

    //POST-ONE
    @POST("/users/jwtusers/{userId}/daytrips/{daytripId}/steps")
    Call<Void> addStepToDaytrip(@Body Step step, @Path("userId") int userId, @Path("daytripId") int daytripId );
}
