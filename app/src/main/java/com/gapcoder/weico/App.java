package com.gapcoder.weico;

import android.app.Application;

import com.gapcoder.weico.Utils.CrashHandler;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by gapcoder on 2018/5/31.
 */
public class App extends Application {

    public Thread.UncaughtExceptionHandler handler;

    public static App ins;

    @Override
    public void onCreate() {
        super.onCreate();
        ins=this;
        LeakCanary.install(this);
       // CrashHandler ins=new CrashHandler(this);
       //// Thread t=Thread.currentThread();
      //  handler=t.getUncaughtExceptionHandler();
      //  t.setUncaughtExceptionHandler(ins);
    }
}
