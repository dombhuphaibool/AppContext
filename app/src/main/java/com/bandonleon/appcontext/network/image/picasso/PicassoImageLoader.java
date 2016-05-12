package com.bandonleon.appcontext.network.image.picasso;

import android.content.Context;
import android.widget.ImageView;

import com.bandonleon.appcontext.network.image.ImageLoader;
import com.squareup.picasso.Picasso;

/**
 * Created by dombhuphaibool on 5/12/16.
 */
public class PicassoImageLoader implements ImageLoader {
    private Context mPicassoContext;

    public PicassoImageLoader(Context context) {
        mPicassoContext = context;
    }

    @Override
    public void load(String url, ImageView imageView) {
        Picasso.with(mPicassoContext).load(url).into(imageView);
    }
}
