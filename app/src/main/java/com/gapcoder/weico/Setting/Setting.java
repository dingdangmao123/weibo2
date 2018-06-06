package com.gapcoder.weico.Setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;

import com.gapcoder.weico.Config;
import com.gapcoder.weico.General.Base;
import com.gapcoder.weico.Psd;
import com.gapcoder.weico.R;
import com.gapcoder.weico.Store;
import com.suke.widget.SwitchButton;

import butterknife.BindView;
import butterknife.OnClick;

public class Setting extends Base {


    SharedPreferences.Editor editor = null;

    @BindView(R.id.mode_switch)
    SwitchButton sb_mode;

    @BindView(R.id.box_switch)
    SwitchButton sb_box;

    @BindView(R.id.message_switch)
    SwitchButton sb_message;

    @BindView(R.id.weibo_switch)
    SwitchButton sb_weibo;


    @OnClick(R.id.store)
    void OnStore(){
        Intent i=new Intent(this,Store.class);
        startActivity(i);
    }

    @OnClick(R.id.psd)
    void OnPsd(){
        Intent i=new Intent(this,Psd.class);
        startActivity(i);
    }

    @Override
    public void init() {

        editor = getSharedPreferences("setting", MODE_PRIVATE).edit();

        sb_mode.setEnableEffect(true);//disable the switch animation
        sb_mode.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                //TODO do your job

                if (sb_mode.isChecked()) {
                    editor.putBoolean("mode", true);
                    Config.mode = true;
                } else {
                    editor.putBoolean("mode", false);
                    Config.mode = false;
                }
                editor.apply();
            }
        });

        sb_box.setEnableEffect(true);//disable the switch animation
        sb_box.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                //TODO do your job

                if (sb_box.isChecked()) {
                    editor.putBoolean("box", true);
                    Config.box = true;
                } else {
                    editor.putBoolean("box", false);
                    Config.box = false;
                }
                editor.apply();
            }
        });


        sb_message.setEnableEffect(true);//disable the switch animation
        sb_message.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                //TODO do your job

                if (sb_weibo.isChecked()) {
                    editor.putBoolean("message", true);
                    Config.message = true;
                } else {
                    editor.putBoolean("message", false);
                    Config.message = false;
                }
                editor.apply();
            }
        });


        sb_weibo.setEnableEffect(true);//disable the switch animation
        sb_weibo.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                //TODO do your job

                if (sb_weibo.isChecked()) {
                    editor.putBoolean("weibo", true);
                    Config.weibo = true;
                } else {
                    editor.putBoolean("weibo", false);
                    Config.weibo = false;
                }
                editor.apply();
            }
        });


        if (Config.mode)
            sb_mode.setChecked(true);

        if (Config.box)
            sb_box.setChecked(true);

        if (Config.message)
            sb_message.setChecked(true);

        if (Config.weibo)
            sb_weibo.setChecked(true);

    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_setting);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
