package com.gapcoder.weico.Utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by gapcoder on 2018/5/31.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private Context context;


    public CrashHandler(Context context) {
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        Log.i("tag", "crashhandler");
        File f = new File(context.getCacheDir(), "crash.log");
        T.show2(context, "Sorry,程序崩溃了!");
        try {

            if (!f.exists())
                f.createNewFile();

            FileWriter w = new FileWriter(f);
            w.write(e.toString());
            w.close();

        } catch (Exception ee) {
            Log.i("tag", ee.toString());
        }
        Log.i("tag", e.toString());

    }
}
