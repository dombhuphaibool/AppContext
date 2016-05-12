package com.bandonleon.appcontext.network.api.retrofit;

import com.bandonleon.appcontext.network.api.Api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by dombhuphaibool on 5/11/16.
 */
public interface GetInfoApi {
    @GET(Api.ENDPOINT_GET_INFO)
    Call<JsonObject> call(@Query(Api.QUERY_PARAM_ID) int id,
                          @Query(Api.QUERY_PARAM_NAME) String name,
                          @Query(Api.QUERY_PARAM_QUANTITY) int quantity);
}
