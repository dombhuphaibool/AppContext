package com.bandonleon.appcontext.app;

import com.bandonleon.appcontext.network.api.Api;
import com.bandonleon.appcontext.network.image.ImageLoader;

/**
 * Created by dombhuphaibool on 5/10/16.
 */
public class NetworkUtil {
    public static Api getApi(CustomContext context) {
        return context.getApi();
    }

    public static Api getApi() {
        return getApi(CustomApplication.getCustomContext());
    }

    public static ImageLoader getImageLoader(CustomContext context) {
        return context.getImageLoader();
    }

    public static ImageLoader getImageLoader() {
        return getImageLoader(CustomApplication.getCustomContext());
    }
}
