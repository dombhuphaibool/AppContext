package com.bandonleon.appcontext.network.image.fresco;

import android.content.Context;
import android.widget.ImageView;

import com.bandonleon.appcontext.network.image.ImageLoader;
import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by dombhuphaibool on 5/12/16.
 */
public class FrescoImageLoader implements ImageLoader {
    private Context mFrescoContext;

    public FrescoImageLoader(Context context) {
        mFrescoContext = context;
        Fresco.initialize(mFrescoContext);
    }

    @Override
    public void load(String url, ImageView imageView) {
        // @TODO: Since Fresco recommends using DraweeView
        // @TODO: Need to do research on getting the image pipeline
    }
}
