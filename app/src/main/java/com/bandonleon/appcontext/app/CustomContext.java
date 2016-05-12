package com.bandonleon.appcontext.app;

import android.content.Context;
import android.content.ContextWrapper;

import com.bandonleon.appcontext.network.Api;

/**
 * Created by dombhuphaibool on 5/10/16.
 */
public abstract class CustomContext extends ContextWrapper {

    private Api mApi;

    public CustomContext(Context baseContext) {
        super(baseContext);
    }

    public void init() {
        recreateApi();
    }

    protected void recreateApi() {
        mApi = createApi();
    }

    Api getApi() {
        return mApi;
    }

    protected abstract Api createApi();
}
