package com.gapcoder.weico;

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

public class Register extends Base {


    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.psd1)
    EditText psd1;
    @BindView(R.id.psd2)
    EditText psd2;
    @BindView(R.id.check)
    EditText check;
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
                    T.show2(Register.this,"手机号不合法！");
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
            UI(()->{T.show2(Register.this,r.getMsg());});


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
        String n=name.getText().toString();
        String p1=psd1.getText().toString();
        String p2=psd2.getText().toString();
        String c=check.getText().toString();

        if(!p.matches("[0-9]{11}")){
            T.show2(this,"手机号不合法!");
            return ;
        }
        if(n.length()<6||n.length()>20){
            T.show2(this,"昵称长度不合法!");
            return ;
        }

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
            map.put("phone", p);
            map.put("name",n);
            map.put("psd",p1);
            map.put("code",c);
            final SysMsg r = URLService.post("regeister.php",map,SysMsg.class);
            UI(()->{
                    T.show2(Register.this,r.getMsg());
                    if(r.getCode().equals(Config.SUCCESS))
                        finish();
            });



        });



    }

    @Override
    public void init() {

    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_register);

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
