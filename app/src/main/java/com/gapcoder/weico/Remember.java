package com.gapcoder.weico;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gapcoder.weico.General.Base;
import com.gapcoder.weico.General.SysMsg;
import com.gapcoder.weico.General.URLService;
import com.gapcoder.weico.Utils.Pool;
import com.gapcoder.weico.Utils.T;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

public class Remember extends Base {

    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.code)
    EditText code;
    @BindView(R.id.get)
    TextView get;
    @BindView(R.id.ok)
    Button ok;


    Handler mh=new MyH();
    final int SMS=0;
    int count=0;
    long time=0;

    @OnClick(R.id.get)
    void get(){

        Log.i("tag","get");

        Pool.run(()->{

            String p=phone.getText().toString();

            if(!p.matches("[0-9]{11}")){
                UI(()->{
                    T.show2(Remember.this,"手机号不合法！");
                });
                return ;
            }

            UI(()->{
                get.setEnabled(false);
                get.setText("60秒重发");
                count=60;
                delay();
            });

            HashMap<String, String> map = new HashMap<>();
            map.put("phone", p);
            final SysMsg r = URLService.post("sendms.php",map,SysMsg.class);
            UI(()->{T.show2(Remember.this,r.getMsg());});

        });

    }

    void delay(){
        Message m=Message.obtain();
        m.what=SMS;
        mh.sendMessageDelayed(m,1000);
    }

    @OnClick(R.id.ok)
    void add(){

        String p=phone.getText().toString();
        String c=code.getText().toString();

        if(!p.matches("[0-9]{11}")){
            T.show2(this,"手机号不合法!");
            return ;
        }

        Pool.run(()->{

            HashMap<String, String> map = new HashMap<>();
            map.put("phone", p);
            map.put("code",c);
            final SysMsg r = URLService.post("remember.php",map,SysMsg.class);
            UI(()->{
                if(r.getCode().equals(Config.SUCCESS)) {
                    Intent i=new Intent(Remember.this,Passwd.class);
                    i.putExtra("phone",p);
                    startActivity(i);
                    finish();
                }else{
                    T.show2(Remember.this,r.getMsg());
                }
            });

        });

    }

    @Override
    public void init() {

    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_remember);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    @Override
    public void beforeFinish() {

        mh.removeMessages(SMS);

    }

    class MyH extends Handler{
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==SMS) {

                Log.i("tag",count+"");
                count--;
                if (count == 0) {
                    get.setText("验证码");
                    get.setEnabled(true);
                    return;
                }

                get.setText(count + "秒重发");
                delay();
            }
        }
    }
}
