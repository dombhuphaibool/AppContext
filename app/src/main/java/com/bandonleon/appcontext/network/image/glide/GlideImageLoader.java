package com.bandonleon.appcontext.network.image.glide;

import android.content.Context;
import android.widget.ImageView;

import com.bandonleon.appcontext.network.image.ImageLoader;
import com.bumptech.glide.Glide;

/**
 * Created by dombhuphaibool on 5/12/16.
 */
public class GlideImageLoader implements ImageLoader {
    private Context mGlideContext;

    public GlideImageLoader(Context context) {
        mGlideContext = context;
    }

    @Override
    public void load(String url, ImageView imageView) {
        Glide.with(mGlideContext).load(url).into(imageView);
    }
}
