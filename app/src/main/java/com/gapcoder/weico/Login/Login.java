package com.gapcoder.weico.Login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gapcoder.weico.General.Base;
import com.gapcoder.weico.General.SysMsg;
import com.gapcoder.weico.General.URLService;
import com.gapcoder.weico.Index.index;
import com.gapcoder.weico.R;
import com.gapcoder.weico.Register;
import com.gapcoder.weico.Remember;
import com.gapcoder.weico.Utils.Pool;
import com.gapcoder.weico.Utils.T;
import com.gapcoder.weico.Utils.Token;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Login extends Base {

    Handler mh = new Handler();
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.psd)
    EditText psd;
    @BindView(R.id.login)
    Button login;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_login);
    }


    @Override
    public void init() {

        Log.i("tag","init");
        Token.initToken(this);
        if (!Token.token.equals("")) {
            Intent i = new Intent(Login.this, index.class);
            Login.this.startActivity(i);
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //
        Log.i("tag","onNewIntent");

    }

    @OnClick(R.id.login)
    void login() {

        final String key = name.getText().toString();
        final String p = psd.getText().toString();

        if(key.length()==0||p.length()==0){
            T.show2(Login.this,"手机和密码不能为空!");
            return ;
        }

        if(!key.matches("[0-9a-z]{11}")||!p.matches("[0-9a-z]{8,20}")){
            T.show2(Login.this,"手机和密码不合法!");
            return ;
        }

        Pool.run(() -> {
            HashMap<String, String> map = new HashMap<>();
            map.put("key", key);
            map.put("psd", p);
            final SysMsg r = URLService.post("login.php", map, LoginModel.class);
            mh.post(() -> {
                if (!r.getCode().equals("OK")) {
                    T.show(Login.this, r.getMsg());
                } else {

                    LoginModel.InnerBean token = ((LoginModel) r).getInner();
                    Token.initToken(Login.this, token.getToken());
                    Token.uid = token.getUid();
                    Intent i = new Intent(Login.this, index.class);
                    Login.this.startActivity(i);
                    finish();
                }

            });

        });
    }

    @OnClick(R.id.add)
    void add() {
        Intent i = new Intent(this, Register.class);
        startActivity(i);
    }
    @OnClick(R.id.remember)
    void remember() {
        Intent i = new Intent(this, Remember.class);
        startActivity(i);
    }

}
