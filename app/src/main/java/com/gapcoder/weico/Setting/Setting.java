package com.gapcoder.weico.Setting;

import android.content.SharedPreferences;
import android.view.Menu;

import com.gapcoder.weico.Config;
import com.gapcoder.weico.Event.event_mode;
import com.gapcoder.weico.General.Base;
import com.gapcoder.weico.R;
import com.suke.widget.SwitchButton;

import org.greenrobot.eventbus.EventBus;

import java.util.function.Function;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class Setting extends Base {


    SharedPreferences.Editor editor = null;

    @BindView(R.id.mode_switch)
    SwitchButton sb_mode;

    @BindView(R.id.box_switch)
    SwitchButton sb_box;

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
                    Config.mode=true;
                } else {
                    editor.putBoolean("mode", false);
                    Config.mode=false;
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
                    Config.box=true;
                } else {
                    editor.putBoolean("box", false);
                    Config.box=false;
                }
                editor.apply();
            }
        });

        if(Config.mode)
            sb_mode.setChecked(true);

        if(Config.box)
            sb_box.setChecked(true);

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
