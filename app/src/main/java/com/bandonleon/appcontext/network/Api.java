package com.bandonleon.appcontext.network;

import org.json.JSONObject;

/**
 * Created by dombhuphaibool on 5/10/16.
 */
public interface Api {
    String SCHEME = "https";
    String HOST = "bandonleon-sandbox.appspot.com";

    String ENDPOINT_GET_INFO = "get_info";

    String QUERY_PARAM_ID = "id";
    String QUERY_PARAM_NAME = "name";
    String QUERY_PARAM_QUANTITY = "quantity";

    // https://host.com/get_info?id=5&name=test&quantity=10
    void getInfo(int id, String name, int quantity, ResponseListener<JSONObject> listener);

    interface ResponseListener<T> {
        void onSuccess(T response);
        void onError(String error);
    }
}