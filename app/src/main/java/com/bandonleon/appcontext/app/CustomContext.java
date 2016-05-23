package com.bandonleon.appcontext.app;

import android.content.Context;
import android.content.ContextWrapper;

import com.bandonleon.appcontext.network.api.Api;
import com.bandonleon.appcontext.network.image.ImageLoader;

/**
 * Created by dombhuphaibool on 5/10/16.
 */
public abstract class CustomContext extends ContextWrapper {

    private boolean mInitialized;
    private Api mApi;
    private ImageLoader mImageLoader;

    public CustomContext(Context baseContext) {
        super(baseContext);
        mInitialized = false;
    }

    public void init() {
        recreateApi();
        recreateImageLoader();
        mInitialized = true;
    }

    public boolean isInitialized() {
        return mInitialized;
    }

    protected void recreateApi() {
        mApi = createApi();
    }

    protected void recreateImageLoader() {
        mImageLoader = createImageLoader();
    }

    Api getApi() {
        return mApi;
    }

    ImageLoader getImageLoader() {
        return mImageLoader;
    }

    protected abstract Api createApi();

    protected abstract ImageLoader createImageLoader();
}
