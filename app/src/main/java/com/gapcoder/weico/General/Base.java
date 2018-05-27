package com.gapcoder.weico.General;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.gapcoder.weico.Config;
import com.gapcoder.weico.R;
import com.gapcoder.weico.Utils.ActivityList;
import com.gapcoder.weico.Utils.T;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import butterknife.ButterKnife;

/**
 * Created by suxiaohui on 2018/3/1.
 */

public class Base extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TypeFace();
        super.onCreate(savedInstanceState);
        ActivityList.add(this);
        setContentView();
        ButterKnife.bind(this);
        Toolbar t=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(t);
        ActionBar ab=getSupportActionBar();
        if(ab!=null){
            ab.setDisplayHomeAsUpEnabled(true);
        }
        init();
    }

    void TypeFace(){

        final Typeface typeface = Typeface.createFromAsset(getAssets(), "fz.TTF");

        LayoutInflaterCompat.setFactory(LayoutInflater.from(this), new LayoutInflaterFactory()
        {
            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs)
            {
                AppCompatDelegate delegate = getDelegate();
                View view = delegate.createView(parent, name, context, attrs);

                if ( view!= null && (view instanceof TextView))
                {
                    ((TextView) view).setTypeface(typeface);
                }
                return view;
            }
        });

    }


    public void init(){

    }

    public void setContentView(){

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                onItemSelected(item.getItemId());
        }
        return true;
    }

    public  void onItemSelected(int id){

    }
    public void UI(Runnable r){
        runOnUiThread(r);
    }

    public boolean check(final SysMsg m){
        if (!m.getCode().equals(Config.SUCCESS)) {
            UI(new Runnable() {
                @Override
                public void run() {
                    T.show(Base.this, m.getMsg());
                }
            });
            return false;
        }
        return true;
    }
   public  boolean check(final SysMsg m, final SmartRefreshLayout rf){
        if (!m.getCode().equals(Config.SUCCESS)) {
            UI(new Runnable() {
                @Override
                public void run() {
                    SmartRefresh(rf);
                    T.show(Base.this, m.getMsg());
                }
            });
            return false;
        }
        return true;
    }
    public  void SmartRefresh(final SmartRefreshLayout rf){
                    if(rf!=null) {
                        if(rf.isRefreshing())
                            rf.finishRefresh(true);
                        if(rf.isLoading())
                            rf.finishLoadmore(true);
                    }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityList.remove(this);
    }
}
