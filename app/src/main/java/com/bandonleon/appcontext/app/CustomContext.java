package com.bandonleon.appcontext.app;

import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.IntDef;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import com.bandonleon.appcontext.network.api.Api;
import com.bandonleon.appcontext.network.image.ImageLoader;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by dombhuphaibool on 5/10/16.
 */
@MainThread
public abstract class CustomContext extends ContextWrapper {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag = true, value = {ResourceType.API, ResourceType.IMAGE_LOADER})
    public @interface ResourceType {
        public static final int API = 1;
        public static final int IMAGE_LOADER = 1 << 1;
    }

    public interface ResourceListener {
        void onResourceReady();
        void onResourceError();
    }

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

    private boolean isResourceRequested(@ResourceType int requested, @ResourceType int check) {
        return (requested & check) == check;
    }

    public void waitForResources(@ResourceType int resources, ResourceListener listener) {
        // @TODO: call isResourceRequested() for each resource type and check if it's ready
        // e.g., if (isResourceRequested(resources, ResourceType.API)) ...
        listener.onResourceReady();
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

    protected abstract @NonNull Api createApi();

    protected abstract @NonNull ImageLoader createImageLoader();
}
