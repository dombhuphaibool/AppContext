package com.bandonleon.appcontext.context;

import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dombhuphaibool on 5/10/16.
 */
@MainThread
public class CustomContext extends ContextWrapper {

    public interface ResourcesListener {
        void onResourcesReady();
        void onResourcesError();
    }

    public interface ResourceDescription {
        @ResourceType int getId();
        boolean useMainThreadForCreation();
        @NonNull Object create();
    }

    private Map<Integer, ContextResource> mResources;
    private @ResourceTypes int mResourcesReady;

    public CustomContext(Context baseContext) {
        super(baseContext);
        mResources = new HashMap<>();
        mResourcesReady = 0;
    }

    /**
     *
     * @param description
     */
    public void addResource(@NonNull ResourceDescription description) {
        if (mResources.containsKey(description.getId())) {
            throw new IllegalStateException("ResourceDescription with id: " + description.getId() + " already exists");
        }
        mResources.put(description.getId(), new ContextResource(description));
    }

    /**
     *
     * @param type
     * @return
     */
    public @NonNull ResourceDescription getResourceDescription(@ResourceType int type) {
        return getContextResource(type).getDescription();
    }

    /**
     *
     * @param type
     * @return
     */
    public @Nullable Object getResource(@ResourceType int type) {
        return getContextResource(type).getResource();
    }

    /**
     *
     * @param resources
     * @param listener
     */
    public void waitForResources(@ResourceTypes int resources, ResourcesListener listener) {
        waitForResources(resources, listener, false);
    }

    public void waitForResources(@ResourceTypes int resources, ResourcesListener listener, boolean throwOnError) {
        if ((mResourcesReady & resources) == resources) {
            listener.onResourcesReady();
        } else {
            if (ensureResouresAreReady(resources, listener)) {
                // TODO: Start a thread to wait on a CountdownLatch and then call the line below...
                listener.onResourcesReady();
            } else {
                // Internal error! mResourcesReady flag doesn't properly match the state
                // of resources that are ready.
                if (throwOnError) {
                    throw new RuntimeException("Internal Error! mResourcesReady does not match the actual state of resources");
                } else {
                    // TODO: For an app, we can just log a handled exception to the crash reporter
                    // TODO: and call the following line, since it's a recoverable error
                    mResourcesReady |= resources;
                    listener.onResourcesReady();
                }
            }
        }
    }

    private boolean ensureResouresAreReady(@ResourceTypes int resources, ResourcesListener listener) {
        boolean resourcesNotReadyFound = false;
        for (ContextResource contextResource : mResources.values()) {
            ResourceDescription resourceDesc = contextResource.getDescription();
            @ResourceTypes int id = resourceDesc.getId();
            if ((resources & id) == id && contextResource.getResource() == null) {
                resourcesNotReadyFound = true;
                if (contextResource.isCreationInProgress()) {
                    // @TODO: attach a listener to the task
                } else {
                    recreateResourceInternal(resourceDesc);
                }
            }
        }
        return resourcesNotReadyFound;
    }

    protected void recreateResource(@ResourceType int type) {
        if (!mResources.containsKey(type)) {
            throw new IllegalStateException("ResourceDescription with id: " + type + " does not exist. Did you forget to call addResource()?");
        }
        recreateResourceInternal(mResources.get(type).getDescription());
    }

    private void recreateResourceInternal(@NonNull ResourceDescription resourceDesc) {
        if (resourceDesc.useMainThreadForCreation()) {
            Object resource = resourceDesc.create();
            resourceRecreated(resourceDesc.getId(), resource);
        } else {
            // @TODO: Create this resource on a worker thread...
            // e.g., call resourceDesc.create() on a worker thread...

            // @TODO: Call resrouceRecreated() when completed...
        }
    }

    private void resourceRecreated(@ResourceType int type, Object resource) {
        mResources.get(type).setResource(resource);
        mResourcesReady |= type;
    }

    private @NonNull ContextResource getContextResource(@ResourceType int type) {
        if (!mResources.containsKey(type)) {
            throw new IllegalStateException("ResourceDescription with id: " + type + " does not exist. Did you forget to call addResource()?");
        }
        return mResources.get(type);
    }

    private static class ContextResource {
        private final ResourceDescription mDescription;
        private Object mResource;
        private Runnable mCreationTask;

        public ContextResource(@NonNull ResourceDescription desc) {
            mDescription = desc;
            mResource = null;
            mCreationTask = null;
        }

        private void setResource(Object resource) {
            mResource = resource;
        }

        private @NonNull ResourceDescription getDescription() {
            return mDescription;
        }

        private @Nullable Object getResource() {
            return mResource;
        }

        private boolean isCreationInProgress() {
            return mCreationTask != null;
        }
    }
}
