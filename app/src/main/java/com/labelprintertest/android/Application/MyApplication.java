package com.labelprintertest.android.Application;

import androidx.multidex.MultiDexApplication;

import com.labelprintertest.android.Common.Common;

/**
 *
 * Application of App
 * */
public class MyApplication extends MultiDexApplication {

    public void onCreate(){
        super.onCreate();

        Common.myApp = this;

//        FontsOverride.setDefaultFont(this, "SERIF", "fonts/HelveticaNeueMed.ttf");
    }

}
