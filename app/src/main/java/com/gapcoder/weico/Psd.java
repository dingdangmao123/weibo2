package com.gapcoder.weico;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;

import com.gapcoder.weico.General.Base;
import com.gapcoder.weico.General.SysMsg;
import com.gapcoder.weico.General.URLService;
import com.gapcoder.weico.Login.Login;
import com.gapcoder.weico.Utils.ActivityList;
import com.gapcoder.weico.Utils.Pool;
import com.gapcoder.weico.Utils.T;
import com.gapcoder.weico.Utils.Token;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

public class Psd extends Base {

    @BindView(R.id.old)
    EditText old;

    @BindView(R.id.psd1)
    EditText psd1;

    @BindView(R.id.psd2)
    EditText psd2;


    @OnClick(R.id.ok)
    void OnClick(){

        String psd=old.getText().toString();
        if(psd.length()==0){
            T.show2(this,"请输入旧密码!");
            return ;
        }

        String p1=psd1.getText().toString().toLowerCase();
        String p2=psd2.getText().toString().toLowerCase();

        if(p1.length()==0||!p1.equals(p2)||!p1.matches("[0-9a-z]{8,20}")){
            T.show2(this,"请按要求输入");
            return ;
        }


        Pool.run(() -> {

            HashMap<String, String> map = new HashMap<>();
            map.put("token", Token.token);
            map.put("old", psd);
            map.put("new",p1);
            final SysMsg r = URLService.post("passwd.php", map,SysMsg.class);

            UI(() -> {
                T.show(Psd.this, r.getMsg());
            });


            if (r.getCode().equals(Config.SUCCESS)) {

                Token.exit(Psd.this);
                Intent i=new Intent(Psd.this, Login.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
               // String name=ActivityList.getLast().getComponentName().getClassName();
               // if(name.equals("com.gapcoder.weico.Login.Login"))
                startActivity(i);
               // else{
               //     ActivityList.exit();
                //}
            }

        });
    }


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_psd);
    }

    @Override
    public void init(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
