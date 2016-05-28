package com.bandonleon.appcontext.context;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by dombhuphaibool on 5/27/16.
 *
 * Modify this file to add more resource types
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({ResourceType.API, ResourceType.IMAGE_LOADER})
public @interface ResourceType {
    public static final int API = 1;
    public static final int IMAGE_LOADER = 1 << 1;
}
