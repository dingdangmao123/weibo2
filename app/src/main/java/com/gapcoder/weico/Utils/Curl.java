package com.gapcoder.weico.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.gapcoder.weico.Index.Model.WeicoModel;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by suxiaohui on 2018/3/3.
 */

public class Curl {
    public static String getText(String link) {

        StringBuilder sb = new StringBuilder();
        HttpURLConnection con = null;
        try {
            URL url = new URL(link);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent",
                    "android curl");
            InputStream in = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
        } catch (Exception e) {
            Log.i("error", e.toString());
        } finally {
            if (con != null)
                con.disconnect();
        }
        return sb.toString();

    }

    public static Object getText(String link, Class<?> clz) {

        StringBuilder sb = new StringBuilder();
        HttpURLConnection con = null;
        Object tl=null;
        try {
            URL url = new URL(link);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent",
                    "android curl");
            InputStream in = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            Gson gson = new Gson();
            tl= gson.fromJson(sb.toString(), clz);
        } catch (Exception e) {
            Log.i("error", e.toString());
        } finally {
            if (con != null)
                con.disconnect();
        }

        return tl;

    }

    public static Bitmap getImage(String link) {
        HttpURLConnection urlConn = null;
        Bitmap bitmap = null;
        try {
            URL imgUrl = new URL(link);
            urlConn = (HttpURLConnection) imgUrl
                    .openConnection();
            urlConn.setRequestProperty("User-Agent", "android curl");
            urlConn.setDoInput(true);
            urlConn.connect();
            InputStream is = urlConn.getInputStream();
            bitmap = BitmapFactory.decodeStream(new BufferedInputStream(is));
            is.close();
        } catch (Exception e) {
            Log.i("fetchPic", e.toString());
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }
        return bitmap;
    }

    public static boolean getImage(String link, OutputStream out) {
        HttpURLConnection urlConn = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bout = null;
        try {
            URL imgUrl = new URL(link);
            urlConn = (HttpURLConnection) imgUrl
                    .openConnection();
            urlConn.setRequestProperty("User-Agent", "android curl");
            urlConn.setDoInput(true);
            urlConn.connect();
            InputStream is = urlConn.getInputStream();
            bis = new BufferedInputStream(is);
            bout = new BufferedOutputStream(out);
            int b = 0;
            while ((b = bis.read()) != -1)
                bout.write(b);

            bis.close();
            bout.close();
            return true;

        } catch (Exception e) {
            Log.i("fetchPic", e.toString());
            return false;
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }
    }
}
