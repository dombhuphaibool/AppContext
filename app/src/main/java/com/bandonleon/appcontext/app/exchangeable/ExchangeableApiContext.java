package com.bandonleon.appcontext.app.exchangeable;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.bandonleon.appcontext.R;
import com.bandonleon.appcontext.context.CustomContext;
import com.bandonleon.appcontext.context.ResourceType;
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

    private final String mApiVolleyStr;
    private final String mApiRetrofitStr;
    private final String mImageLoaderVolleyStr;
    private final String mImageLoaderPicassoStr;
    private final String mImageLoaderGlideStr;
    private final String mImageLoaderFrescoStr;

    public ExchangeableApiContext(Context baseContext) {
        super(baseContext);

        Resources res = getResources();
        mApiVolleyStr = res.getString(R.string.network_api_volley);
        mApiRetrofitStr = res.getString(R.string.network_api_retrofit);
        mImageLoaderVolleyStr = res.getString(R.string.image_loader_volley);
        mImageLoaderPicassoStr = res.getString(R.string.image_loader_piccaso);
        mImageLoaderGlideStr = res.getString(R.string.image_loader_glide);
        mImageLoaderFrescoStr = res.getString(R.string.image_loader_fresco);

        addResource(new ApiModule(baseContext));
        addResource(new ImageLoaderModule(baseContext));
    }

    void setApiType(String apiTypeStr) {
        boolean apiChanged = true;
        ApiModule apiModule = (ApiModule) getResourceDescription(ResourceType.API);
        ApiType apiType = apiModule.getApiType();
        if (mApiVolleyStr.equals(apiTypeStr) && apiType != ApiType.Volley) {
            apiType = ApiType.Volley;
        } else if (mApiRetrofitStr.equals(apiTypeStr) && apiType != ApiType.Retrofit) {
            apiType = ApiType.Retrofit;
        } else {
            apiChanged = false;
        }

        if (apiChanged) {
            apiModule.setApiType(apiType);
            recreateResource(ResourceType.API);
        }
    }

    void setImageLoaderType(String imgLoaderTypeStr) {
        boolean imageLoaderChanged = true;
        ImageLoaderModule imgLoaderModule = (ImageLoaderModule) getResourceDescription(ResourceType.IMAGE_LOADER);
        ImageLoaderType imgLoaderType = imgLoaderModule.getImageLoaderType();
        if (mImageLoaderVolleyStr.equals(imgLoaderTypeStr) && imgLoaderType != ImageLoaderType.Volley) {
            imgLoaderType = ImageLoaderType.Volley;
        } else if (mImageLoaderPicassoStr.equals(imgLoaderTypeStr) && imgLoaderType != ImageLoaderType.Picasso) {
            imgLoaderType = ImageLoaderType.Picasso;
        } else if (mImageLoaderGlideStr.equals(imgLoaderTypeStr) && imgLoaderType != ImageLoaderType.Glide) {
            imgLoaderType = ImageLoaderType.Glide;
        } else if (mImageLoaderFrescoStr.equals(imgLoaderTypeStr) && imgLoaderType != ImageLoaderType.Fresco) {
            imgLoaderType = ImageLoaderType.Fresco;
        } else {
            imageLoaderChanged = false;
        }

        if (imageLoaderChanged) {
            imgLoaderModule.setImageLoaderType(imgLoaderType);
            recreateResource(ResourceType.IMAGE_LOADER);
        }
    }

    private static class ApiModule implements ResourceDescription {

        private final Context mContext;
        private ApiType mApiType;

        public ApiModule(@NonNull Context context) {
            mContext = context;
            mApiType = ApiType.Volley;
        }

        public void setApiType(@NonNull ApiType apiType) {
            mApiType = apiType;
        }

        public ApiType getApiType() {
            return mApiType;
        }

        @Override
        public int getId() {
            return ResourceType.API;
        }

        @Override
        public boolean useMainThreadForCreation() {
            return true;
        }

        @Override
        public @NonNull Object create() {
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

        private
        @NonNull
        Api createApiVolley() {
            return new ApiVolley(mContext.getApplicationContext());
        }

        private
        @NonNull
        Api createApiRetrofit() {
            return new ApiRetrofit();
        }
    }

    private static class ImageLoaderModule implements ResourceDescription {

        private final Context mContext;
        private ImageLoaderType mImageLoaderType;

        public ImageLoaderModule(@NonNull Context context) {
            mContext = context;
            mImageLoaderType = ImageLoaderType.Volley;
        }

        public void setImageLoaderType(@NonNull ImageLoaderType imageLoaderType) {
            mImageLoaderType = imageLoaderType;
        }

        public ImageLoaderType getImageLoaderType() {
            return mImageLoaderType;
        }

        @Override
        public int getId() {
            return ResourceType.IMAGE_LOADER;
        }

        @Override
        public boolean useMainThreadForCreation() {
            return true;
        }

        @Override
        public @NonNull Object create() {
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
            return new VolleyImageLoader(mContext.getApplicationContext());
        }

        private ImageLoader createPicassoImageLoader() {
            return new PicassoImageLoader(mContext.getApplicationContext());
        }

        private ImageLoader createGlideImageLoader() {
            return new GlideImageLoader(mContext.getApplicationContext());
        }

        private ImageLoader createFrescoImageLoader() {
            return new FrescoImageLoader(mContext.getApplicationContext());
        }
    }
}
