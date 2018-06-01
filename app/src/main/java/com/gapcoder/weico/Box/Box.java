package com.gapcoder.weico.Box;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.baoyz.actionsheet.ActionSheet;
import com.gapcoder.weico.General.Base;
import com.gapcoder.weico.Post;
import com.gapcoder.weico.R;
import com.gapcoder.weico.Utils.Pool;
import com.gapcoder.weico.Utils.T;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import butterknife.BindView;
import butterknife.OnLongClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class Box extends Base implements ActionSheet.ActionSheetListener {

    @BindView(R.id.text)
    TextView tv;


    @OnLongClick(R.id.text)
    boolean delete(){


        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles("删除", "重发")
                .setCancelableOnTouchOutside(true)
                .setListener(this).show();

        return true;
    }


    @Override
    public void init() {

            Pool.run(() -> {
                File f = new File(getCacheDir(), "box");
                if (!f.exists())
                    return;
                try {
                    ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
                    final Post.inner ins = (Post.inner) in.readObject();
                    UI(() -> {
                        tv.setText(ins.getText());
                    });
                    in.close();
                    tv.setText("");

                } catch (Exception e) {
                    Log.i("tag", e.toString());
                }
            });

    }

    @Override
    public void setContentView() {
        super.setContentView(R.layout.activity_box);
    }


    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
            actionSheet.dismiss();
    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
            switch (index){
                case 0:
                    File f = new File(getCacheDir(), "box");
                    if (!f.exists())
                        return ;

                    if(f.delete()){
                        T.show2(this,"删除成功");
                    }else
                        T.show2(this,"删除失败");
                    break;
                case 1:
                    Intent i=new Intent(this,Post.class);
                    startActivity(i);
                    finish();
            }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
