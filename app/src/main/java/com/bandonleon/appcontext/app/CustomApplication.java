package com.bandonleon.appcontext.app;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.bandonleon.appcontext.context.CustomContext;
import com.bandonleon.appcontext.context.ResourceType;
import com.bandonleon.appcontext.network.api.volley.ApiVolley;
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
        return (CustomContext) context;
    }

    // @TODO: We can either do this or expose access to the CustomContext
    public static void waitForResources(@ResourceType int resources, CustomContext.ResourcesListener listener) {
        waitForResources(resources, listener, true);
    }
    public static void waitForResources(@ResourceType int resources, CustomContext.ResourcesListener listener, boolean throwOnError) {
        getInstance().getCustomContext().waitForResources(resources, listener, throwOnError);
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
        CustomContext customContext = new CustomContext(base);
        customContext.addResource(new CustomContext.ResourceDescription() {
            @Override
            public int getId() {
                return ResourceType.API;
            }

            @Override
            public boolean useMainThreadForCreation() {
                return true;
            }

            @NonNull
            @Override
            public Object create() {
                return new ApiVolley(getApplicationContext());
            }
        });
        customContext.addResource(new CustomContext.ResourceDescription() {
            @Override
            public int getId() {
                return ResourceType.IMAGE_LOADER;
            }

            @Override
            public boolean useMainThreadForCreation() {
                return true;
            }

            @NonNull
            @Override
            public Object create() {
                return new VolleyImageLoader(getApplicationContext());
            }
        });
        return customContext;
    }
}
