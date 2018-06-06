package com.gapcoder.weico.Utils;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by gapcoder on 2018/6/3.
 */

public class FileUtils {
    public static long getSize(File f){

        if(!f.isDirectory())
            return f.length();


        File[] list=f.listFiles();
        long sum=0;
        for(File ff:list)
            sum+=getSize(ff);

        return sum;
    }

    public static String format(long s){
        DecimalFormat df = new DecimalFormat("#.00");
        if(s<1024)
            return s+" B";
        else if(s<Math.pow(1024,2))
            return df.format(s/1024.0)+" K";
        else if(s<Math.pow(1024,3))
            return df.format(s/1024.0*1024.0)+" M";
        else
            return df.format(s/1024.0*1024*1024)+" G";

    }

    public static boolean delete(File f){
        if(!f.isDirectory())
            return f.delete();

        boolean res=true;
        File[] list=f.listFiles();
        for(File ff:list)
            res=res&&ff.delete();

        return res&&f.delete();
    }
}
