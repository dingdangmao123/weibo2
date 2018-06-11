package com.gapcoder.weico.Utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

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
        PrintWriter p=null;
        T.show2(context, "Sorry,程序崩溃了!");
        try {

            if (!f.exists())
                f.createNewFile();

            p=new PrintWriter(new FileWriter(f));

        } catch (Exception ee) {

        }finally {

            e.printStackTrace(p);
            p.close();
        }

        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
