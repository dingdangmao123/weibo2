package com.gapcoder.weico;

import android.app.Application;

import com.gapcoder.weico.Utils.CrashHandler;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by gapcoder on 2018/5/31.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        CrashHandler ins=new CrashHandler(this);
        Thread t=Thread.currentThread();
        t.setUncaughtExceptionHandler(ins);
    }
}
