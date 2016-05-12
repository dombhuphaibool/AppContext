package com.bandonleon.appcontext.network.retrofit;

import com.bandonleon.appcontext.network.Api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.Response;

/**
 * Created by dombhuphaibool on 5/11/16.
 */
public interface GetInfoApi {
    @GET(Api.ENDPOINT_GET_INFO)
    Call<JsonObject> call(@Query(Api.QUERY_PARAM_ID) int id,
                          @Query(Api.QUERY_PARAM_NAME) String name,
                          @Query(Api.QUERY_PARAM_QUANTITY) int quantity);
}
