package com.sergi.notifylocation.Remote;

public class ApiUtils {

    private ApiUtils() {}

    public static final String BASE_URL = "https://notifylocation.herokuapp.com/";

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
