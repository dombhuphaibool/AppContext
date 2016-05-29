package com.bandonleon.appcontext.context;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Handler;
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
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by dombhuphaibool on 5/10/16.
 */
@MainThread
public class CustomContext extends ContextWrapper implements ResourceCreationListener {

    /**
     * When calling waitForResources(), the caller must provide a ResourcesListener.
     * When all requested resources are ready, onResourcesReady() will be invoked.
     * If an error occurs, onResourcesError() will be invoked. The return value indicates
     * whether the CustomContext should recover and ignore the error. It also gives the
     * caller a chance to inquiry the state of the CustomContext before returning from
     * onResroucesError().
     */
    public interface ResourcesListener {
        void onResourcesReady(@ResourceTypes int resources);
        boolean onResourcesError(String error);
    }

    /**
     * A description of managed resources. Clients should modify ResourceType and ResourceTypes
     * annotations to include all candidates of managed resources. The description contains
     * a method for creating the managed resource as well as indicating whether the creation
     * should be done on the main thread or a worker thread.
     */
    public interface ResourceDescription {
        @ResourceType int getId();
        boolean useMainThreadForCreation();
        @NonNull Object create();
    }

    /**
     * CustomContext core code
     */
    private ExecutorService mTaskExecutor;
    private Handler mMainHandler;
    private Map<Integer, ContextResource> mResources;
    private @ResourceTypes int mResourcesReady;

    public CustomContext(Context baseContext) {
        super(baseContext);

        // TODO: Specify a ThreadFactory to customize the thread name
        mTaskExecutor = Executors.newCachedThreadPool();
        mMainHandler = new Handler(getMainLooper());
        mResources = new HashMap<>();
        mResourcesReady = 0;
    }

    /**
     * addResource() - This method must be called to add all possible managed resources
     *
     * @param description - A resource description
     */
    public void addResource(@NonNull ResourceDescription description) {
        if (mResources.containsKey(description.getId())) {
            throw new IllegalStateException("ResourceDescription with id: " + description.getId() + " already exists");
        }
        mResources.put(description.getId(), new ContextResource(description));
    }

    /**
     * getResourceDescription()
     *
     * @param type
     * @return The resource description of a given resource type
     */
    public @NonNull ResourceDescription getResourceDescription(@ResourceType int type) {
        return getContextResource(type).getDescription();
    }

    /**
     * getResource()
     *
     * @param type
     * @return The manage resource of a given resource type
     */
    public @Nullable Object getResource(@ResourceType int type) {
        return getContextResource(type).getResource();
    }

    /**
     * waitForResources() - Waits for all the requested resources to be created. Calls back
     * on the ResourcesListener interface.
     *
     * @param resources
     * @param listener
     */
    public void waitForResources(@ResourceTypes int resources, @NonNull ResourcesListener listener) {
        waitForResources(resources, listener, false);
    }

    /**
     * waitForResources() - Waits for all the requested resources to be created. Calls back
     * on the ResourcesListener interface.
     *
     * @param resources
     * @param listener
     * @param throwOnError
     */
    public void waitForResources(@ResourceTypes int resources, @NonNull ResourcesListener listener, boolean throwOnError) {
        /*
         * If all resources are ready, call listener.onResourcesReady() right away,
         * otherwise call ensureResouresAreReady(). ensureResouresAreReady() will return the
         * number of resources that needs to be created. If this number is equal to zero, it means
         * that our bookkeping is off and there was some internal error. i.e., mResourcesReady flag
         * doesn't properly match the state of resources that are ready.
         */
        if ((mResourcesReady & resources) == resources) {
            listener.onResourcesReady(resources);
        } else if (ensureResouresAreReady(resources, listener) == 0) {
            String errorStr = "Internal Error! mResourcesReady does not match the actual state of resources";
            if (throwOnError) {
                throw new RuntimeException(errorStr);
            } else {
                boolean recoverFromError = listener.onResourcesError(errorStr);
                if (recoverFromError) {
                    mResourcesReady |= resources;
                }
            }
        }
    }

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

    private int ensureResouresAreReady(@ResourceTypes final int resources, @NonNull final ResourcesListener listener) {
        final List<ContextResource> resourcesToCreate = new ArrayList<>();
        // TODO: We can probably use Java 8 streaming api to filter what we need here...
        for (ContextResource contextResource : mResources.values()) {
            @ResourceTypes int id = contextResource.getDescription().getId();
            if ((resources & id) == id && contextResource.getResource() == null) {
                resourcesToCreate.add(contextResource);
            }
        }

        // List resourcesToCreate contains all resources that we need to create
        int numResourcesToCreate = resourcesToCreate.size();
        final CountDownLatch resourcesCreatedSignal = new CountDownLatch(numResourcesToCreate);
        for (ContextResource contextResource : resourcesToCreate) {
            contextResource.recreate(mMainHandler, mTaskExecutor, resourcesCreatedSignal, listener, this);
        }

        mTaskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    resourcesCreatedSignal.await();
                } catch (InterruptedException ex) {
                    // TODO: Do we need to do something else here?
                    listener.onResourcesError("Fatal error! InterruptedException caught while waiting for resources creation to finish");
                }

                // All the resources have been created, notify on the main thread
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onResourcesReady(resources);
                    }
                });
            }
        });

        return numResourcesToCreate;
    }

    /**
     *
     * @param type
     */
    public void recreateResourceOnMainThread(@ResourceType int type) {
        if (!mResources.containsKey(type)) {
            throw new IllegalStateException("ResourceDescription with id: " + type + " does not exist. Did you forget to call addResource()?");
        }
        mResources.get(type).recreateOnMainThread(this);
    }

    /**
     *
     */
    private static class ContextResource {
        private final ResourceDescription mDescription;
        private Object mResource;
        private Future<Object> mCreationTask;
        private List<CountDownLatch> mWaiters;

        public ContextResource(@NonNull ResourceDescription desc) {
            mDescription = desc;
            mResource = null;
            mCreationTask = null;
            mWaiters = null;
        }

        private void recreateOnMainThread(@NonNull ResourceCreationListener listener) {
            mResource = mDescription.create();
            listener.onResourceCreated(mDescription.getId());
        }

        private void recreate(@NonNull final Handler mainHandler,
                @NonNull ExecutorService taskExecutor,
                @NonNull final CountDownLatch resourcesCreatedSignal,
                @NonNull final ResourcesListener resourcesListener,
                @NonNull final ResourceCreationListener creationListener) {

            if (isCreationInProgress()) {
                addWaiter(resourcesCreatedSignal);
                return;
            }

            if (mDescription.useMainThreadForCreation()) {
                recreateOnMainThread(creationListener);
            } else {
                mResource = null;
                mCreationTask = taskExecutor.submit(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        Object resource = mDescription.create();
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                completeCreationFromWorkerThread(resourcesCreatedSignal, resourcesListener, creationListener);
                            }
                        });
                        return resource;
                    }
                });
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

        private void addWaiter(@NonNull CountDownLatch waiter) {
            if (mWaiters == null) {
                mWaiters = new ArrayList<>();
            }
            mWaiters.add(waiter);
        }

        /**
         * This method should only be called if the resource was created on the worker thread
         *
         * @param resourcesCreatedSignal
         * @param resourcesListener
         * @param creationListener
         */
        private void completeCreationFromWorkerThread(
                @NonNull CountDownLatch resourcesCreatedSignal,
                @NonNull ResourcesListener resourcesListener,
                @NonNull ResourceCreationListener creationListener) {

            if (mCreationTask == null) {
                throw new IllegalStateException("Cannot call completeCreationFromWorkerThread when there's no pending task");
            }

            try {
                mResource = mCreationTask.get();
                creationListener.onResourceCreated(mDescription.getId());
            } catch (InterruptedException interruptedEx) {
                resourcesListener.onResourcesError("Fatal Error! InterruptedException caught while trying to retrieve newly created resource");
            } catch (ExecutionException executionEx) {
                resourcesListener.onResourcesError("Fatal Error! ExecutionException caught while trying to retrieve newly created resource");
            } finally {
                mCreationTask = null;

                for (CountDownLatch waiter : mWaiters) {
                    waiter.countDown();
                }
                mWaiters.clear();
                mWaiters = null;

                resourcesCreatedSignal.countDown();
            }
        }
    }
}
