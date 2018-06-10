package com.francescoxx.plango.frontend.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
    SINGLETON DESIGN PATTERN
    Retrofit Client Singleton
 */
class RetrofitClient {

    private static Retrofit retrofit = null;

    static Retrofit getClient(String url) {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
