package com.bandonleon.appcontext.context;

import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by dombhuphaibool on 5/10/16.
 */
@MainThread
public class CustomContext extends ContextWrapper implements ResourceCreationListener {

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
            if (ensureResouresAreReady(resources, listener) > 0) {
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

    private int ensureResouresAreReady(@ResourceTypes int resources, ResourcesListener listener) {
        List<ContextResource> resourcesToCreate = new ArrayList<>();
        // TODO: We can probably use Java 8 streaming api to filter what we need here...
        for (ContextResource contextResource : mResources.values()) {
            // ResourceDescription resourceDesc = contextResource.getDescription();
            @ResourceTypes int id = contextResource.getDescription().getId();
            if ((resources & id) == id && contextResource.getResource() == null) {
                resourcesToCreate.add(contextResource);
            }
        }

        int numResourcesToCreate = resourcesToCreate.size();
        CountDownLatch readySignal = new CountDownLatch(numResourcesToCreate);
        for (ContextResource contextResource : resourcesToCreate) {
            if (contextResource.isCreationInProgress()) {
                contextResource.addWaiter(readySignal);
            } else {
                contextResource.create(mTaskExecutor, readySignal, this);
            }
        }

        // TODO: Start a thread to wait on a CountdownLatch and then call the line below...
        listener.onResourcesReady();

        return numResourcesToCreate;
    }

    protected void recreateResource(@ResourceType int type) {
        if (!mResources.containsKey(type)) {
            throw new IllegalStateException("ResourceDescription with id: " + type + " does not exist. Did you forget to call addResource()?");
        }
        mResources.get(type).create(mTaskExecutor, null, this);
    }

    private ExecutorService mTaskExecutor;

    @Override
    public void onResourceCreated(@ResourceType int type) {
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
        private Future<Object> mCreationTask;
        private List<CountDownLatch> mWaiters;

        public ContextResource(@NonNull ResourceDescription desc) {
            mDescription = desc;
            mResource = null;
            mCreationTask = null;
        }

        private void create(ExecutorService taskExecutor, final CountDownLatch readySignal, final ResourceCreationListener listener) {
            if (mDescription.useMainThreadForCreation()) {
                mResource = mDescription.create();
                listener.onResourceCreated(mDescription.getId());
            } else {
                Future<Object> creatorTask = taskExecutor.submit(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        Object resource = mDescription.create();
                        if (readySignal != null) {
                            readySignal.countDown();
                        }
                        return resource;
                    }
                });

                // @TODO: Create this resource on a worker thread...
                // e.g., call resourceDesc.create() on a worker thread...

                // @TODO: Call resrouceRecreated() when completed...
                // mResource =
                // listener.onResourceCreated()
            }
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

        private void addWaiter(CountDownLatch waiter) {
            if (mWaiters == null) {
                mWaiters = new ArrayList<>();
            }
            mWaiters.add(waiter);
        }

        private void completeCreation() {
            if (mCreationTask == null) {
                throw new IllegalStateException("Cannot call completeCreation when there's no pending task");
            }

            try {
                mResource = mCreationTask.get();
            } catch (InterruptedException interruptedEx) {
                // Ignore exception
            } catch (ExecutionException executionEx) {
                // Ignore exception
            }

            for (CountDownLatch waiter : mWaiters) {
                waiter.countDown();
            }
            mWaiters.clear();
            mWaiters = null;
        }
    }
}
