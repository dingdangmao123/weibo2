package com.gapcoder.weico;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;

import com.gapcoder.weico.General.Base;
import com.gapcoder.weico.General.SysMsg;
import com.gapcoder.weico.General.URLService;
import com.gapcoder.weico.Utils.Pool;
import com.gapcoder.weico.Utils.T;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

public class Passwd extends Base {


    @BindView(R.id.psd1)
    EditText psd1;
    @BindView(R.id.psd2)
    EditText psd2;

    @BindView(R.id.ok)
    Button ok;

    String p="";


    @OnClick(R.id.ok)
    void add(){


        String p1=psd1.getText().toString();
        String p2=psd2.getText().toString();

        if(!p1.matches("[0-9a-z]{8,20}")){
            T.show2(this,"密码不合法!");
            return ;
        }
        if(!p1.equals(p2)){
            T.show2(this,"密码不一致!");
            return ;
        }


        Pool.run(()->{


            HashMap<String, String> map = new HashMap<>();
            map.put("phone",p);
            map.put("psd",p1);

            final SysMsg r = URLService.post("resetpsd.php",map,SysMsg.class);
            UI(()->{
                T.show2(Passwd.this,r.getMsg());
                if(r.getCode().equals(Config.SUCCESS))
                    finish();
            });

        });

    }

    @Override
    public void init() {
        p=getIntent().getStringExtra("phone");
        if(p.length()==0)
            return ;
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_passwd);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


}
