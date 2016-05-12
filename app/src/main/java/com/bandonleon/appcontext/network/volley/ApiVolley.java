package com.bandonleon.appcontext.network.volley;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bandonleon.appcontext.network.Api;

import org.json.JSONObject;

/**
 * Created by dombhuphaibool on 5/10/16.
 */
public class ApiVolley implements Api {

    private RequestQueue mRequestQueue;

    public ApiVolley(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    private Uri.Builder getBaseBuilder() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(Api.SCHEME).authority(Api.HOST);
        return builder;
    }

    public void getInfo(int id, String name, int quantity, ResponseListener<JSONObject> listener) {
        Uri.Builder builder = getBaseBuilder();
        builder.appendPath(ENDPOINT_GET_INFO);
        builder.appendQueryParameter(QUERY_PARAM_ID, String.valueOf(id));
        builder.appendQueryParameter(QUERY_PARAM_NAME, name);
        builder.appendQueryParameter(QUERY_PARAM_QUANTITY, String.valueOf(quantity));
        String apiEndpoint = builder.build().toString();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiEndpoint, null,
                new SuccessListener<>(listener), new ErrorListener<>(listener));
        mRequestQueue.add(request);
    }

    /**
     * SuccessListener: Proxy class to proxy a successful response to Api.ResponseListener
     *
     * @param <T>
     */
    private static class SuccessListener<T> implements Response.Listener<T> {
        private ResponseListener<T> mListener;

        public SuccessListener(ResponseListener<T> listener) {
            mListener = listener;
        }

        @Override
        public void onResponse(T response) {
            mListener.onSuccess(response);
        }
    }

    /**
     * ErrorListener: Proxy class to proxy an error to Api.ResponseListener
     *
     * @param <T>
     */
    private static class ErrorListener<T> implements Response.ErrorListener {
        private ResponseListener<T> mListener;

        public ErrorListener(ResponseListener<T> listener) {
            mListener = listener;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            mListener.onError(error.toString());
        }
    }
}
