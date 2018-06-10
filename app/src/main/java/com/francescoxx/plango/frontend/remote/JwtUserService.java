package com.francescoxx.plango.frontend.remote;

import com.francescoxx.plango.frontend.model.JwtUser;
import com.francescoxx.plango.frontend.model.Token;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/*
    Client-Service model
    Contains all the Calls to Server @RestController
 */

public interface JwtUserService {


    @GET("/secret")
    Call<JwtUser> getSecretId(@Header("Authorization") String auth, @Path("name") String user);

    @POST("/token")
    Call<Token> login(@Body JwtUser jwtUser);

    //Post method to create a new user. returns void for now
    @POST("/users/jwtusers")
    Call<Void> register(@Body JwtUser jwtUser);

    //get a jwtUser by his name
    @GET("users/jwtusers/byName/{name}")
    Call<JwtUser> getJwtUserByName(@Path("name") String user);
}
