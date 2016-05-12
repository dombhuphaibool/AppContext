package com.bandonleon.appcontext.network.image.volley;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bandonleon.appcontext.network.image.ImageLoader;

/**
 * Created by dombhuphaibool on 5/12/16.
 */
public class VolleyImageLoader implements ImageLoader {

    private com.android.volley.toolbox.ImageLoader mImageLoader;

    public VolleyImageLoader(Context context) {
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        mImageLoader = new com.android.volley.toolbox.ImageLoader(requestQueue,
                new com.android.volley.toolbox.ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> mCache = new LruCache<>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return mCache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        mCache.put(url, bitmap);
                    }
                });
    }

    @Override
    public void load(String url, ImageView imageView) {
        int defaultImageResId = 0;  // R.drawable.def_image
        int errorImageResId = 0;    // R.drawable.err_image
        mImageLoader.get(url, com.android.volley.toolbox.ImageLoader.getImageListener(imageView,
                defaultImageResId, errorImageResId));
    }
}
