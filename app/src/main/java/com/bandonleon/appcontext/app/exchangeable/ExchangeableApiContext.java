package com.bandonleon.appcontext.app.exchangeable;

import android.content.Context;
import android.content.res.Resources;

import com.android.volley.toolbox.Volley;
import com.bandonleon.appcontext.R;
import com.bandonleon.appcontext.app.CustomContext;
import com.bandonleon.appcontext.network.api.Api;
import com.bandonleon.appcontext.network.api.retrofit.ApiRetrofit;
import com.bandonleon.appcontext.network.api.volley.ApiVolley;
import com.bandonleon.appcontext.network.image.ImageLoader;
import com.bandonleon.appcontext.network.image.fresco.FrescoImageLoader;
import com.bandonleon.appcontext.network.image.glide.GlideImageLoader;
import com.bandonleon.appcontext.network.image.picasso.PicassoImageLoader;
import com.bandonleon.appcontext.network.image.volley.VolleyImageLoader;

/**
 * Created by dombhuphaibool on 5/12/16.
 */
public class ExchangeableApiContext extends CustomContext {
    private enum ApiType {
        Volley,
        Retrofit
    }

    private enum ImageLoaderType {
        Volley,
        Picasso,
        Glide,
        Fresco
    }

    private ApiType mApiType;
    private ImageLoaderType mImageLoaderType;

    String mApiVolleyStr;
    String mApiRetrofitStr;
    String mImageLoaderVolleyStr;
    String mImageLoaderPicassoStr;
    String mImageLoaderGlideStr;
    String mImageLoaderFrescoStr;

    public ExchangeableApiContext(Context baseContext) {
        super(baseContext);

        mApiType = ApiType.Volley;
        mImageLoaderType = ImageLoaderType.Volley;

        Resources res = getResources();
        mApiVolleyStr = res.getString(R.string.network_api_volley);
        mApiRetrofitStr = res.getString(R.string.network_api_retrofit);
        mImageLoaderVolleyStr = res.getString(R.string.image_loader_volley);
        mImageLoaderPicassoStr = res.getString(R.string.image_loader_piccaso);
        mImageLoaderGlideStr = res.getString(R.string.image_loader_glide);
        mImageLoaderFrescoStr = res.getString(R.string.image_loader_fresco);
    }

    void setApiType(String apiTypeStr) {
        boolean apiChanged = true;
        if (mApiVolleyStr.equals(apiTypeStr) && mApiType != ApiType.Volley) {
            mApiType = ApiType.Volley;
        } else if (mApiRetrofitStr.equals(apiTypeStr) && mApiType != ApiType.Retrofit) {
            mApiType = ApiType.Retrofit;
        } else {
            apiChanged = false;
        }

        if (apiChanged) {
            recreateApi();
        }
    }

    void setImageLoaderType(String imageLoaderType) {
        boolean imageLoaderChanged = true;
        if (mImageLoaderVolleyStr.equals(imageLoaderType) && mImageLoaderType != ImageLoaderType.Volley) {
            mImageLoaderType = ImageLoaderType.Volley;
        } else if (mImageLoaderPicassoStr.equals(imageLoaderType) && mImageLoaderType != ImageLoaderType.Picasso) {
            mImageLoaderType = ImageLoaderType.Picasso;
        } else if (mImageLoaderGlideStr.equals(imageLoaderType) && mImageLoaderType != ImageLoaderType.Glide) {
            mImageLoaderType = ImageLoaderType.Glide;
        } else if (mImageLoaderFrescoStr.equals(imageLoaderType) && mImageLoaderType != ImageLoaderType.Fresco) {
            mImageLoaderType = ImageLoaderType.Fresco;
        } else {
            imageLoaderChanged = false;
        }

        if (imageLoaderChanged) {
            recreateImageLoader();
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

    @Override
    protected ImageLoader createImageLoader() {
        ImageLoader imageLoader = null;
        switch (mImageLoaderType) {
            case Picasso:
                imageLoader = createPicassoImageLoader();
                break;

            case Glide:
                imageLoader = createGlideImageLoader();
                break;

            case Fresco:
                imageLoader = createFrescoImageLoader();
                break;

            case Volley:
            default:
                imageLoader = createVolleyImageLoader();
                break;
        }
        return imageLoader;
    }

    private ImageLoader createVolleyImageLoader() {
        return new VolleyImageLoader(getApplicationContext());
    }

    private ImageLoader createPicassoImageLoader() {
        return new PicassoImageLoader(getApplicationContext());
    }

    private ImageLoader createGlideImageLoader() {
        return new GlideImageLoader(getApplicationContext());
    }

    private ImageLoader createFrescoImageLoader() {
        return new FrescoImageLoader(getApplicationContext());
    }
}
