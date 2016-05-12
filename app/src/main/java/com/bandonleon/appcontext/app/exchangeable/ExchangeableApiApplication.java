package com.bandonleon.appcontext.app.exchangeable;

import android.content.Context;

import com.bandonleon.appcontext.app.CustomApplication;
import com.bandonleon.appcontext.app.CustomContext;

/**
 * Created by dombhuphaibool on 5/12/16.
 */
public class ExchangeableApiApplication extends CustomApplication {

    public static void switchNetworkApi(String apiTypeStr) {
        CustomContext customContext = CustomApplication.getCustomContext();
        if (customContext instanceof ExchangeableApiContext) {
            ExchangeableApiContext exchangeableApiContext = (ExchangeableApiContext) customContext;
            exchangeableApiContext.setApiType(apiTypeStr);
        }
    }

    @Override
    protected CustomContext createCustomContext(Context base) {
        return new ExchangeableApiContext(base);
    }
}