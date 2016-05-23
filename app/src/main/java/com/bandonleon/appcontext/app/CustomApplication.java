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

    protected static CustomContext getCustomContext() {
        CustomApplication app = getInstance();
        if (app == null) {
            throw new IllegalStateException("CustomApplication.onCreate() has not been called yet");
        }
        Context context = getInstance().getBaseContext();
        if (context == null || !(context instanceof CustomContext)) {
            throw new IllegalStateException("CustomApplication's context is null or not a CustomContext");
        }
        CustomContext customContext = (CustomContext) context;
        if (!customContext.isInitialized()) {
            customContext.init();
        }
        return customContext;
    }

    /**
     * Components should call this method if they are going to use the CustomContext. This will
     * allow initialization of the CustomContext to be done in advance. However, if the components
     * do not call this method in advance, when it calls getCustomContext(), the initialization
     * will happen lazily. This allows components that do not use the CustomContext resources to
     * avoid the overhead of creating resources that it will not use (ie, if the BroadcastReceiver
     * will not use something in the the CustomContext, etc.
     *
     * For components that will use the CustomContext, this method should be called in its onCreate().
     */
    public static void initCustomContext() {
        getCustomContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sAppInstance = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        CustomContext customContext = createCustomContext(base);
        super.attachBaseContext(customContext);
    }

    /**
     * This method should be overridden by the Flavor's application to provide any
     * specialized CustomContext.
     *
     * @param base - Base context passed to the application in attachBaseContext()
     * @return A CustomContext
     */
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
