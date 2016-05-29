package com.bandonleon.appcontext.app;

import android.app.Application;
import android.content.Context;

import com.bandonleon.appcontext.context.CustomContext;

/**
 * Created by dombhuphaibool on 5/10/16.
 */
public abstract class CustomApplication extends Application {

    private static CustomApplication sAppInstance;

    static CustomApplication getInstance() {
        return sAppInstance;
    }

    public static CustomContext getCustomContext() {
        CustomApplication app = getInstance();
        if (app == null) {
            throw new IllegalStateException("CustomApplication.onCreate() has not been called yet");
        }
        Context context = app.getBaseContext();
        if (context == null || !(context instanceof CustomContext)) {
            throw new IllegalStateException("CustomApplication's context is null or not a CustomContext");
        }
        return (CustomContext) context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sAppInstance = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        CustomContext customContext = new CustomContext(base);
        addResourcesDescription(customContext);
        super.attachBaseContext(customContext);
    }

    /**
     * This method should be overridden by the Flavor's application to provide all
     * descriptions of managed resources
     *
     * @param customContext
     */
    protected abstract void addResourcesDescription(CustomContext customContext);
}
