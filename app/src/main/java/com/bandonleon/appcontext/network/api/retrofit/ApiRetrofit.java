package com.bandonleon.appcontext.network.api.retrofit;

import android.net.Uri;

import com.bandonleon.appcontext.network.api.Api;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dombhuphaibool on 5/10/16.
 */
public class ApiRetrofit implements Api {

    private Retrofit mRetrofit;

    public ApiRetrofit() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(getBaseBuilder().build().toString())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private Uri.Builder getBaseBuilder() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(Api.SCHEME).authority(Api.HOST);
        return builder;
    }

    public void getInfo(int id, String name, int quantity, ResponseListener<JSONObject> listener) {
        GetInfoApi getInfoApi = mRetrofit.create(GetInfoApi.class);
        Call<JsonObject> call = getInfoApi.call(id, name, quantity);
        call.enqueue(new JSONCallListener(listener));
    }

    /**
     * JSONCallListener: Proxy class to convert a Retrofit response/error to Api.ResponseListener,
     *          which is network api agnostic.
     */
    private static class JSONCallListener implements Callback<JsonObject> {
        private ResponseListener<JSONObject> mListener;

        public JSONCallListener(ResponseListener<JSONObject> listener) {
            mListener = listener;
        }

        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            // We need to convert com.google.gson.JsonObject to org.json.JSONObject
            // @TODO: Figure out why Retrofit doesn't handle JSONOjbect...
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(response.body().toString());
                jsonObject.put("api", "Retrofit");
            } catch (JSONException ex) {
                // @TODO: report exception
            }
            mListener.onSuccess(jsonObject);
        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {
            mListener.onError(t.toString());
        }
    }
}
