package com.bandonleon.appcontext.app.exchangeable;

import com.bandonleon.appcontext.app.MainApplication;
import com.bandonleon.appcontext.context.CustomContext;
import com.bandonleon.appcontext.context.ResourceType;

/**
 * Created by dombhuphaibool on 5/12/16.
 */
public class ExchangeableApiApplication extends MainApplication {

    private static ExchangeableApiApplication sAppInstance;

    // TODO: Clean this up!
    public static void switchNetworkApi(String apiTypeStr) {
        sAppInstance.mResourcesManager.setApiType(getCustomContext(), apiTypeStr);
    }

    // TODO: Clean this up!
    public static void switchImageLoader(String imageLoaderTypeStr) {
        sAppInstance.mResourcesManager.setImageLoaderType(getCustomContext(), imageLoaderTypeStr);
    }

    private ResourcesManager mResourcesManager;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppInstance = this;
    }

    @Override
    protected void addResourcesDescription(CustomContext customContext) {
        mResourcesManager = new ResourcesManager(customContext);
        customContext.addResource(mResourcesManager.createApiModule(customContext));
        customContext.addResource(mResourcesManager.createImageLoaderModule(customContext));
    }
}