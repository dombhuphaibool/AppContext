package com.bandonleon.appcontext.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.test.AndroidTestCase;
import android.util.Log;

import com.bandonleon.appcontext.context.CustomContext;
import com.bandonleon.appcontext.context.CustomContext.ResourceDescription;
import com.bandonleon.appcontext.context.CustomContext.ResourcesListener;
import com.bandonleon.appcontext.context.ResourceTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by dom on 5/30/16.
 */
public class CustomContextTest extends AndroidTestCase {

    private static final String TAG = "CustomContextTest";

    private CustomContext mCustomContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Context baseContext = getContext();
        mCustomContext = new CustomContext(baseContext);
        setContext(mCustomContext);
    }

    public void testTooManyResources() throws Exception {
        try {
            int id = 1;
            for (int i = 0; i < Integer.SIZE + 1; ++i) {
                mCustomContext.addResource(new TestResourceDescription(id << i));
            }
            assertTrue("Was able to add more than " + Integer.SIZE + " resources", false);
        } catch (IllegalStateException ex) {
            assertTrue(true);
        }
    }

    public void testDuplicateResources() throws Exception {
        try {
            mCustomContext.addResource(new TestResourceDescription(1));
            mCustomContext.addResource(new TestResourceDescription(1));
            assertTrue("Was able to add duplicate resources", false);
        } catch (IllegalStateException ex) {
            assertTrue(true);
        }
    }

    /**
     * Resources are created in the order of their id. This tests that a creation of a group
     * of resources of the first request does not block subsequent request of smaller ids which
     * is a subset of previous request (where the previous request is still ongoing).
     *
     * @throws Exception
     */
    public void testResourceCreationOnWorkerThread() throws Exception {
        final int SECOND_MSEC = 1000;
        int desc1Id = 1;
        int desc2Id = 1 << 1;
        int desc3Id = 1 << 2;
        int desc4Id = 1 << 3;
        int desc5Id = 1 << 4;
        int desc6Id = 1 << 5;
        TestResourceDescription desc1 = new TestResourceDescription(desc1Id, false, 2 * SECOND_MSEC);
        TestResourceDescription desc2 = new TestResourceDescription(desc2Id, false, 3 * SECOND_MSEC);
        TestResourceDescription desc3 = new TestResourceDescription(desc3Id, false, 8 * SECOND_MSEC);
        TestResourceDescription desc4 = new TestResourceDescription(desc4Id, false, 6 * SECOND_MSEC);
        TestResourceDescription desc5 = new TestResourceDescription(desc5Id, false, 1 * SECOND_MSEC);
        TestResourceDescription desc6 = new TestResourceDescription(desc6Id, false, 4 * SECOND_MSEC);

        mCustomContext.addResource(desc1);
        mCustomContext.addResource(desc2);
        mCustomContext.addResource(desc3);
        mCustomContext.addResource(desc4);
        mCustomContext.addResource(desc5);
        mCustomContext.addResource(desc6);

        TestResourcesRequest req1 = new TestResourcesRequest(desc1Id | desc2Id | desc3Id, 6);   // ~8 secs
        TestResourcesRequest req2 = new TestResourcesRequest(desc2Id | desc1Id, 3);             // ~3 secs
        TestResourcesRequest req3 = new TestResourcesRequest(desc2Id | desc4Id, 5);             // ~6 secs
        TestResourcesRequest req4 = new TestResourcesRequest(desc1Id, 2);                       // ~2 secs
        TestResourcesRequest req5 = new TestResourcesRequest(desc5Id, 1);                       // ~1 secs
        TestResourcesRequest req6 = new TestResourcesRequest(desc6Id | desc2Id, 4);             // ~4 secs

        final int numRequests = 6;
        final CountDownLatch doneSignal = new CountDownLatch(numRequests);
        List<TestResourcesRequest> completionList = new ArrayList<>();

        sendResourcesRequest(req1, doneSignal, completionList);
        sendResourcesRequest(req2, doneSignal, completionList);
        sendResourcesRequest(req3, doneSignal, completionList);
        sendResourcesRequest(req4, doneSignal, completionList);
        sendResourcesRequest(req5, doneSignal, completionList);
        sendResourcesRequest(req6, doneSignal, completionList);

        doneSignal.await();

        int completedId = 0;
        for (TestResourcesRequest req : completionList) {
            ++completedId;
            assertEquals("Resources request completed out of expected sequence, expected: " +
                    completedId + ", actual: " + req.completionId, completedId, req.completionId);
        }
    }

    private void sendResourcesRequest(final TestResourcesRequest request,
            final CountDownLatch doneSignal,
            final List<TestResourcesRequest> completionList) {

        final long startTime = System.currentTimeMillis();
        mCustomContext.waitForResources(request.resourcesIds, new ResourcesListener() {
            @Override
            public void onResourcesReady(@ResourceTypes int resources) {
                completionList.add(request);
                Log.d(TAG, "Resources request " + request.completionId + " is now ready, took " +
                        String.valueOf(System.currentTimeMillis() - startTime) + " msec.");
                doneSignal.countDown();
            }

            @Override
            public boolean onResourcesError(String error) {
                return false;
            }
        });
    }

    private static class TestResourcesRequest {
        final int resourcesIds;
        final int completionId;

        public TestResourcesRequest(int resourcesIds, int completionId) {
            this.resourcesIds = resourcesIds;
            this.completionId = completionId;
        }
    }

    private static class TestResourceDescription implements ResourceDescription {

        private final int mId;
        private final boolean mUseMainThread;
        private final Runnable mCreationRunnable;

        public TestResourceDescription(int id) {
            this(id, true, 0);
        }

        public TestResourceDescription(int id, boolean useMainThread, final int creationDurationMsec) {
            mId = id;
            mUseMainThread = useMainThread;
            mCreationRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        if (creationDurationMsec > 0) {
                            Thread.sleep(creationDurationMsec);
                        }
                    } catch (InterruptedException ignored) {

                    }
                }
            };
        }

        @Override
        public int getId() {
            return mId;
        }

        @Override
        public boolean useMainThreadForCreation() {
            return mUseMainThread;
        }

        @NonNull
        @Override
        public Object create() {
            if (mCreationRunnable != null) {
                mCreationRunnable.run();
            }
            return new Object();
        }
    }
}
