package com.bandonleon.appcontext.app;

import android.app.Application;
import android.content.Context;

import com.bandonleon.appcontext.network.api.Api;
import com.bandonleon.appcontext.network.api.volley.ApiVolley;
import com.bandonleon.appcontext.network.image.ImageLoader;
import com.bandonleon.appcontext.network.image.volley.VolleyImageLoader;

/**
 * Created by dombhuphaibool on 5/10/16.
 */
public class CustomApplication extends Application {

    private static CustomApplication sAppInstance;

    static CustomApplication getInstance() {
        return sAppInstance;
    }

    static protected CustomContext getCustomContext() {
        Context context = getInstance().getBaseContext();
        if (context == null || !(context instanceof CustomContext)) {
            throw new IllegalStateException("CustomApplication's context is null or not a CustomContext");
        }
        return (CustomContext) context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sAppInstance = this;
        getCustomContext().init();
    }

    @Override
    protected void attachBaseContext(Context base) {
        CustomContext customContext = createCustomContext(base);
        super.attachBaseContext(customContext);
    }

    protected CustomContext createCustomContext(Context base) {
        return new CustomContext(base) {
            @Override
            protected Api createApi() {
                return new ApiVolley(getApplicationContext());
            }

            @Override
            protected ImageLoader createImageLoader() {
                return new VolleyImageLoader(getApplicationContext());
            }
        };
    }
}
