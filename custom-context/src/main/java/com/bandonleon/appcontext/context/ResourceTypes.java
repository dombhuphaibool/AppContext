package com.bandonleon.appcontext.context;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by dombhuphaibool on 5/27/16.
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef(flag = true, value = {ResourceType.API, ResourceType.IMAGE_LOADER})
public @interface ResourceTypes {}
