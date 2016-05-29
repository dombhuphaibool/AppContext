package com.bandonleon.appcontext.app;

import android.support.annotation.NonNull;

import com.bandonleon.appcontext.context.CustomContext;
import com.bandonleon.appcontext.context.ResourceType;
import com.bandonleon.appcontext.network.api.volley.ApiVolley;
import com.bandonleon.appcontext.network.image.volley.VolleyImageLoader;

/**
 * Created by dom on 5/28/16.
 */
public class MainApplication extends CustomApplication {

    @Override
    protected void addResourcesDescription(CustomContext customContext) {
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
    }
}
