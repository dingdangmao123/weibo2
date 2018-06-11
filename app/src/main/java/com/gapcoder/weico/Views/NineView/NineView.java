package com.gapcoder.weico.Views.NineView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gapcoder.weico.Config;
import com.gapcoder.weico.R;
import com.gapcoder.weico.Utils.Compress;
import com.gapcoder.weico.Utils.Curl;
import com.gapcoder.weico.Utils.DiskLRU;
import com.gapcoder.weico.Utils.Image;
import com.gapcoder.weico.Utils.Pool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by gapcoder on 2018/5/30.
 */


public class NineView extends ViewGroup {


    private Context context;
    private List<String> url = null;
    private int singleW = 300;
    private int singleH = 300;
    private NineClick touch;



    private int gap = 8;
    private int size = 0;
    private int l = 0;
    private int w = 0;
    private int h = 0;

    public NineView(Context context) {
        this(context, null);
    }

    public NineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int width = widthSize;
        int height = 0;

        if (url == null) {
            setMeasuredDimension(width, height);
            return;
        }
        if (url.size() == 1) {

            MyImage c = (MyImage) getChildAt(0);
            int w = c.getW();
            int h = c.getH();

            if(w>h){

                if(w<singleW){
                    this.w=w;
                    this.h=h;
                }else{
                    this.w=singleW;
                    this.h=singleW*h/w;
                }

            }else{

                if(h<singleH){
                    this.w=w;
                    this.h=h;
                }else{
                    this.h=singleH;
                    this.w=singleH*w/h;
                }
            }

            height=this.h;

            Log.i("tag", this.w + " "+this.h);
        } else if (url.size() == 4) {
            width = widthSize;
            size = (width - 2 * gap) / 3;
            l = 2;
            height = size * 2 + gap;
        } else {
            width = widthSize;
            size = (width - 2 * gap) / 3;
            l = (url.size() - 1) / 3 + 1;
            height = size * l + (l - 1) * gap;
        }
        setMeasuredDimension(width, height);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if (url == null)
            return;

        int ll = url.size();

        if (ll == 1) {

            View c = getChildAt(0);
            c.layout(0, 0, w, h);
            load(context, (ImageView) c, c.getTag().toString());

        } else if (url.size() == 4) {

            int left = 0;
            int top = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View c = getChildAt(i);
                if (i == 2) {
                    left = 0;
                    top = size + gap;
                }
                c.layout(left, top, left + size, top + size);
                load(context, (ImageView) c, c.getTag().toString());
                left = left + size + gap;
            }
        } else {
            int left = 0;
            int top = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View c = getChildAt(i);
                if (i == 3 || i == 6) {
                    left = 0;
                    top = (size + gap) * (i / 3);
                }
                c.layout(left, top, left + size, top + size);
                load(context, (ImageView) c, c.getTag().toString());
                left = left + size + gap;
            }
        }


    }

    void load(Context context, ImageView iv, String u) {
        if (this.url.size()== 1)
            displayOneImage(getContext(), iv, u, w, h);
        else
            displayOneImage(getContext(), iv, u,size,size);
    }

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(Activity ins, List<String> url) {
        this.url = url;
        int l = url.size();
        if (l == 0)
            return;

        int c=getChildCount();
        if(c>l){
            removeViews(l,c-l);
        }else if(c<l){
            for(int i=c;i<l;i++){
                MyImage iv=new MyImage(context);
                if(touch!=null){

                    iv.setOnLongClickListener((View v)->{
                        return touch.OnLongClick(v);
                    });

                    iv.setOnClickListener((View v)->{touch.OnClick(v);});
                }
                addView(iv);
            }
        }

        for (int i=0;i<l;i++) {
            MyImage iv = (MyImage) getChildAt(i);
            String u = url.get(i);
            String[] n = u.split("\\.");
            n = n[n.length - 2].split("_");
            iv.setW(Integer.parseInt(n[n.length - 2]));
            iv.setH(Integer.parseInt(n[n.length - 1]));
            iv.setTag(u);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setImageResource(R.drawable.holder);
        }
        /*removeAllViews();

        for (String u : url) {
            Log.i("tag", u);
            MyImage iv = new MyImage(context);
            String[] n = u.split("\\.");
            n = n[n.length - 2].split("_");
            iv.setW(Integer.parseInt(n[n.length - 2]));
            iv.setH(Integer.parseInt(n[n.length - 1]));
            iv.setTag(u);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setImageResource(R.drawable.holder);
            if(touch!=null){

                iv.setOnLongClickListener((View v)->{
                    return touch.OnLongClick(v);
                });

                iv.setOnClickListener((View v)->{touch.OnClick(v);});
            }

            addView(iv);*/


        //}
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }

    public int getSize() {
        return size;
    }

    public void displayOneImage(Context context, ImageView iv, String url, int w, int h) {
        Image.down(context,iv,url,w,h);
    }

    static class MyImage extends ImageView {

        private int w;

        private int h;

        public MyImage(Context context) {
            this(context, null);
        }


        public MyImage(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public MyImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public int getW() {
            return w;
        }

        public void setW(int w) {
            this.w = w;
        }

        public int getH() {
            return h;
        }

        public void setH(int h) {
            this.h = h;
        }
    }

    public NineClick getTouch() {
        return touch;
    }

    public void setTouch(NineClick touch) {
        this.touch = touch;
    }

   public interface NineClick{
        void OnClick(View v);
        boolean OnLongClick(View v);
    }

}
