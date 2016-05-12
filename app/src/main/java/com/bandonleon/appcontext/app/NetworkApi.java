package com.bandonleon.appcontext.app;

import com.bandonleon.appcontext.network.Api;

/**
 * Created by dombhuphaibool on 5/10/16.
 */
public class NetworkApi {
    public static Api get(CustomContext context) {
        return context.getApi();
    }

    public static Api get() {
        return get(CustomApplication.getCustomContext());
    }
}
