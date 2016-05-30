package com.bandonleon.appcontext.app;

import com.bandonleon.appcontext.context.CustomContext;
import com.bandonleon.appcontext.context.ResourceType;
import com.bandonleon.appcontext.network.api.Api;
import com.bandonleon.appcontext.network.image.ImageLoader;

/**
 * Created by dombhuphaibool on 5/10/16.
 */
public class NetworkUtil {
    public static Api getApi(CustomContext context) {
        return (Api) context.getResource(ResourceType.API);
    }

    public static Api getApi() {
        return getApi(CustomApplication.getCustomContext());
    }

    public static ImageLoader getImageLoader(CustomContext context) {
        return (ImageLoader) context.getResource(ResourceType.IMAGE_LOADER);
    }

    public static ImageLoader getImageLoader() {
        return getImageLoader(CustomApplication.getCustomContext());
    }
}
