package com.gapcoder.weico;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.gapcoder.weico.General.Base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Photo extends Base {


    @BindView(R.id.container)
    ViewPager container;

    @BindView(R.id.cur)
    TextView cur;

    List<String> url = new ArrayList<>();

    @Override
    public void init() {

        Bundle b=getIntent().getExtras();
        String t=b.getString("url");
        if (t.length() == 0)
            return;

        int i=b.getInt("index",1);

        String from=b.getString("from","");
        Log.i("tag",from);
        url = Arrays.asList(t.split(","));
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = new PhotoFragment();
                Bundle bundle = new Bundle();
                bundle.putString("url", url.get(position));
                bundle.putString("from",from);
                fragment.setArguments(bundle);
                return fragment;
            }
            @Override
            public int getCount() {
                return url.size();
            }
        };

        container.setAdapter(adapter);
        container.setCurrentItem(i);
        container.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                cur.setText((position+1)+"/"+url.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        cur.setText((i+1)+"/"+url.size());

    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_photo);
    }

    @Override
    public void beforeView(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);

    }

}
