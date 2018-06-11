package com.gapcoder.weico.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.disklrucache.DiskLruCache;
import com.gapcoder.weico.Config;
import com.gapcoder.weico.PhotoFragment;
import com.gapcoder.weico.R;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by suxiaohui on 2018/3/3.
 */

public class Image {

    private static LruCache<String, Bitmap> ins;
    private static ExecutorService pool = Executors.newFixedThreadPool(4);
    private static DiskLruCache disklru = null;

    private synchronized static LruCache<String, Bitmap> getInstance(Context context) {
        if (ins == null) {
            int max = (int) (Runtime.getRuntime().maxMemory() / 1024) / 8;
            ins = new LruCache<String, Bitmap>(max) {
                @Override
                protected int sizeOf(String k, Bitmap bit) {
                    return bit.getRowBytes() * bit.getHeight() / 1024;
                }
            };
            DiskLRU.init(context);
            disklru = DiskLRU.getInstance();
        }
        return ins;
    }

    public static Bitmap get(Context context, String k) {
        return getInstance(context).get(k);
    }

    public synchronized static void put(Context context, String k, Bitmap v) {
        getInstance(context).put(k, v);
    }

    public static void down(Activity context, ImageView v, String url, int w, int h) {
        Bitmap bit = get(context, url);
        if (bit != null) {
            Log.i("Unit", "getFromCache " + url);
            try {
                if (url.equals(v.getTag()))
                    v.setImageBitmap(bit);
            } catch (Exception e) {
                Log.i("Unit", e.toString());
            }
        } else {
            String key = DiskLRU.Hash(url);
            try {

                DiskLruCache.Value value = disklru.get(key);
                if (value != null) {
                    File file = value.getFile(0);
                    Log.i("disklru", file.getAbsolutePath());
                    Bitmap b = Compress.decodeFile(file.getAbsolutePath(), w, h);
                    put(context, url, b);
                    if (url.equals(v.getTag()))
                        v.setImageBitmap(b);
                    return;
                }
            } catch (Exception e) {
                Log.i("LRU", e.toString());
            }
            getFromUrl(context, v, url, w, h);
        }
    }

    public static void down(Activity context, ImageView v, String url) {
        Bitmap bit = get(context, url);
        if (bit != null) {
            Log.i("Unit", "getFromCache " + url);
            try {
                v.setImageBitmap(bit);
            } catch (Exception e) {
                Log.i("Unit", e.toString());
            }
        } else {
            getFromUrl2(context, v, url);
        }
    }

    public static void getFromDisk(Activity context, Runnable ins, ImageView iv, String url, int w, int h) {
        Pool.run(() -> {
            Log.i("tag",url);
            Bitmap bit=getInstance(context).get("big"+url);
            if(bit==null){
                File f = new File(
                        DiskLRU.getCacheDir(context) + File.separator + url);
                if (!f.exists()) {
                    try {
                        f.createNewFile();
                        Curl.getImage(Config.photo + url, new FileOutputStream(f));
                    } catch (Exception e) {
                        Log.i("tag", e.toString());
                    }
                }
                bit = Compress.decodeFile(f.getAbsolutePath(), w, h);
                if(bit!=null)
                    getInstance(context).put("big"+url,bit);
            }
            if(bit!=null) {
                Bitmap b=bit;
                ((Activity) context).runOnUiThread(() -> {
                    iv.setImageBitmap(b);
                    ins.run();
                });

            }
        });
    }

    public static String getPath(String url) {

        String key = DiskLRU.Hash(url);
        String path = "";

        try {

            DiskLruCache.Value value = disklru.get(key);

            if (value != null) {

                File file = value.getFile(0);
                path = file.getAbsolutePath();

            }

        } catch (Exception e) {

        }

        return path;
    }

    public static File getFile(String url) {

        String key = DiskLRU.Hash(url);
        String path = "";

        try {

            DiskLruCache.Value value = disklru.get(key);

            if (value != null) {

                File file = value.getFile(0);
                return file;

            }

        } catch (Exception e) {

        }

        return null;
    }

    public static void getFromUrl2(final Activity context, final ImageView im, final String url) {
        Log.i("Unit", "getFromUrl " + url);
        pool.execute(() -> {
            final Bitmap bit = Curl.getImage(url);
            if (bit == null) {
                Log.i("Unit", "null bitmap");
            } else if (bit != null) {
                put(context, url, bit);
                context.runOnUiThread(() -> {
                    if (url.equals(im.getTag().toString())) {
                        try {
                            im.setImageBitmap(bit);
                        } catch (Exception e) {
                            Log.i("Unit", e.toString());
                        }
                    }
                });
            }
        });

    }

    public static void getFromUrl(final Activity context, final ImageView im, final String url, int w, int h) {
        Log.i("Unit", "getFromUrl " + url);
        try {
            DiskLruCache.Editor edit = disklru.edit(DiskLRU.Hash(url));
            if (edit == null)
                return;
            File f = edit.getFile(0);
            downImage(context, im, url, edit, new FileOutputStream(f), w, h);
        } catch (Exception e) {
            Log.i("Unit", e.toString());
        }
    }

    public static void delete(final Activity context, final String url) {
        Log.i("tag", "delete from cache:" + url);
        getInstance(context).remove(url);
        try {
            DiskLruCache.Editor edit = disklru.edit(DiskLRU.Hash(url));
            if (edit == null)
                return;
            File f = edit.getFile(0);
            if (f.exists()) {
                f.delete();
            }
        } catch (Exception e) {
            Log.i("Unit", e.toString());
        }
    }

    public static void downImage(final Context context, final ImageView im, final String url, final DiskLruCache.Editor edit, final OutputStream out, int w, int h) {

        pool.execute(() -> {

            try {

                //if(get(context,url))

                if (Curl.getImage(url, out))
                    edit.commit();
                else
                    edit.abort();
                disklru.flush();

                String key = DiskLRU.Hash(url);
                DiskLruCache.Value value = disklru.get(key);
                File file = value.getFile(0);
                Bitmap b = Compress.decodeFile(file.getAbsolutePath(), w, h);
                put(context, url, b);
                ((Activity) context).runOnUiThread(() -> {
                    if (url.equals(im.getTag()))
                        im.setImageBitmap(b);
                });


            } catch (Exception e) {
                Log.i("Unit", e.toString());
            }

        });
    }

    public static void down(Context context, ImageView iv, String url, int w, int h) {

        Pool.run(() -> {
            Log.i("tag",url);
            Bitmap bit=getInstance(context).get(url);
            if(bit==null){
                File f = new File(
                        DiskLRU.getCacheDir(context) + File.separator + url);
                if (!f.exists()) {
                    try {
                        f.createNewFile();
                        Curl.getImage(Config.photo + url, new FileOutputStream(f));
                    } catch (Exception e) {
                        Log.i("tag", e.toString());
                    }
                }
                bit = Compress.decodeFile(f.getAbsolutePath(), w, h);
                if(bit!=null)
                    getInstance(context).put(url,bit);
            }
            if(bit!=null) {
                Bitmap b=bit;
                ((Activity) context).runOnUiThread(() -> {
                    iv.setImageBitmap(b);
                });

            }
        });

    }
}

