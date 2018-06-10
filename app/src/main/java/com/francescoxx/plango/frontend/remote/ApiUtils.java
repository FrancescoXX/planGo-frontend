package com.francescoxx.plango.frontend.remote;

/*
    Utility class to define Retrofit Client Services to use

    WARNING: If you DONT define ObjService Retrofit will throw Error (is null something on Call...)

    API Utils:
    Contains:
        - BASE_URL,
        - CLIENT-SERVICES
 */
public class ApiUtils {

    private static final String BASE_URL = "http://192.168.1.3:8084/";

    //CLIENT-SERVICES with static methods to get instance

    public static com.francescoxx.plango.frontend.remote.JwtUserService getJwtUserService() {
        return com.francescoxx.plango.frontend.remote.RetrofitClient.getClient(BASE_URL).create(com.francescoxx.plango.frontend.remote.JwtUserService.class);
    }

    public static com.francescoxx.plango.frontend.remote.DaytripService getDayTripService(){
        return com.francescoxx.plango.frontend.remote.RetrofitClient.getClient(BASE_URL).create(com.francescoxx.plango.frontend.remote.DaytripService.class);
    }

    public static com.francescoxx.plango.frontend.remote.StepService getStepService(){
        return com.francescoxx.plango.frontend.remote.RetrofitClient.getClient(BASE_URL).create(com.francescoxx.plango.frontend.remote.StepService.class);
    }

    public static com.francescoxx.plango.frontend.remote.PoiService getPoiService(){
        return com.francescoxx.plango.frontend.remote.RetrofitClient.getClient(BASE_URL).create(com.francescoxx.plango.frontend.remote.PoiService.class);
    }
}
