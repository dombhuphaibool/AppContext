package com.bandonleon.appcontext.app.exchangeable;

import android.content.Context;
import android.content.res.Resources;

import com.bandonleon.appcontext.R;
import com.bandonleon.appcontext.app.CustomContext;
import com.bandonleon.appcontext.network.Api;
import com.bandonleon.appcontext.network.retrofit.ApiRetrofit;
import com.bandonleon.appcontext.network.volley.ApiVolley;

/**
 * Created by dombhuphaibool on 5/12/16.
 */
public class ExchangeableApiContext extends CustomContext {
    private enum ApiType {
        Volley,
        Retrofit
    }

    private ApiType mApiType;
    String mApiVolleyStr;
    String mApiRetrofitStr;

    public ExchangeableApiContext(Context baseContext) {
        super(baseContext);
        mApiType = ApiType.Volley;
        Resources res = getResources();
        mApiVolleyStr = res.getString(R.string.network_api_volley);
        mApiRetrofitStr = res.getString(R.string.network_api_retrofit);
    }

    void setApiType(String apiTypeStr) {
        boolean apiChanged = false;
        if (mApiVolleyStr.equals(apiTypeStr) && mApiType != ApiType.Volley) {
            mApiType = ApiType.Volley;
            apiChanged = true;
        } else if (mApiRetrofitStr.equals(apiTypeStr) && mApiType != ApiType.Retrofit) {
            mApiType = ApiType.Retrofit;
            apiChanged = true;
        }

        if (apiChanged) {
            recreateApi();
        }
    }

    @Override
    protected Api createApi() {
        Api api = null;
        switch (mApiType) {
            case Retrofit:
                api = createApiRetrofit();
                break;

            case Volley:
            default:
                api = createApiVolley();
                break;
        }
        return api;
    }

    private Api createApiVolley() {
        return new ApiVolley(getApplicationContext());
    }

    private Api createApiRetrofit() {
        return new ApiRetrofit();
    }
}
