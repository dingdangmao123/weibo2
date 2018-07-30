package com.gapcoder.weico;

import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import com.gapcoder.weico.General.Base;
import com.gapcoder.weico.Utils.DiskLRU;
import com.gapcoder.weico.Utils.FileUtils;
import com.gapcoder.weico.Utils.Pool;
import com.gapcoder.weico.Utils.StoreSize;
import com.gapcoder.weico.Utils.T;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Store extends Base {


    @BindView(R.id.cache)
    TextView cache;

    @BindView(R.id.clear)
    Button clear;

    @BindView(R.id.total1)
    TextView total1;
    @BindView(R.id.useable1)
    TextView useable1;
/*    @BindView(R.id.total2)
    TextView total2;
    @BindView(R.id.useable2)
    TextView useable2;*/


    @OnClick(R.id.clear)
    void OnClick() {

        clear.setText("清除中...");
        Pool.run(()->{

            boolean f=FileUtils.delete(DiskLRU.getCacheDir(Store.this));
            String res="";
            if(f)
                res="删除成功";
            else
                res="删除失败";
            String s=res;
            UI(()->{
                T.show2(Store.this,s);
                clear.setText("清除缓存");
                cache.setText("微博缓存空间: 0B");
            });

        });

    }

    @Override
    public void init() {

        useable1.setText("SD卡可用空间: " + StoreSize.getSDAvailableSize(this));
        total1.setText("SD卡全部空间: " + StoreSize.getSDTotalSize(this));

       // useable2.setText("手机内部可用空间: " + StoreSize.getRomAvailableSize(this));
       // total2.setText("手机内部全部空间: " + StoreSize.getRomTotalSize(this));

        Pool.run(()->{
                String res= Formatter.formatFileSize(Store.this,FileUtils.getSize(DiskLRU.getCacheDir(Store.this)));
                UI(()->{cache.setText("微博缓存空间: "+res);});
        });

    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_store);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
