package com.francescoxx.plango.frontend.remote;

import com.francescoxx.plango.frontend.poi.Poi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
/*
    Poi Service
    Contains just a method to get all pois available (cant modifiy those ones from a client!)
 */

public interface PoiService {

    //GET-ALL
    @GET("/pois")
    Call<List<Poi>> getAll();
}
