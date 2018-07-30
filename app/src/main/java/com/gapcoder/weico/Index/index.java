package com.gapcoder.weico.Index;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.gapcoder.weico.Config;
import com.gapcoder.weico.General.SysMsg;
import com.gapcoder.weico.General.URLService;
import com.gapcoder.weico.Index.FG.AccountFG;
import com.gapcoder.weico.Index.FG.BaseFG;
import com.gapcoder.weico.Index.FG.TitleFG;
import com.gapcoder.weico.Index.FG.WeicoFG;
import com.gapcoder.weico.MessageService.MessageService;
import com.gapcoder.weico.R;
import com.gapcoder.weico.Utils.ActivityList;
import com.gapcoder.weico.Utils.Pool;
import com.gapcoder.weico.Utils.Token;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import q.rorbin.badgeview.QBadgeView;

public class index extends AppCompatActivity {

    FragmentManager fm = getSupportFragmentManager();
    HashMap<Integer, Fragment> map = new HashMap<>();
    HashSet<BaseFG> flag = new HashSet<>();

    @BindView(R.id.tab)
    BottomNavigationView tab;

    private int pos = 1;


    WeicoFG wFG;
    TitleFG tFG;
    AccountFG aFG;

    boolean flag1=false;
    boolean flag2=false;
    boolean flag3=false;



    private IntentFilter filter;
    private MessageReceiver receiver;

    QBadgeView bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        final Typeface typeface = Typeface.createFromAsset(getAssets(), "fz.TTF");

        LayoutInflaterCompat.setFactory(LayoutInflater.from(this), new LayoutInflaterFactory() {
            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
                AppCompatDelegate delegate = getDelegate();
                View view = delegate.createView(parent, name, context, attrs);

                if (view != null && (view instanceof TextView)) {
                    ((TextView) view).setTypeface(typeface);
                }
                return view;
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        ButterKnife.bind(this);
        ActivityList.add(this);


        

        tab.setOnNavigationItemSelectedListener((MenuItem item) -> {

            if (item.getItemId() == R.id.weico)
                bar.hide(false);

     /*       FragmentTransaction tran = fm.beginTransaction();
            hideFragments(tran);
            int id = item.getItemId();
            if (!flag.contains(id)) {
                tran.add(R.id.container, map.get(id));
                flag.add(id);
            }
            tran.show(map.get(id));
            tran.commit();*/
            int id=item.getItemId();

            if(id==R.id.weico) {

                pos=1;
                showFragments(wFG);
                if(!flag1)
                    flag1=true;

            }else if(id==R.id.title){
                pos=2;
                showFragments(tFG);
                if(!flag2)
                    flag2=true;
            }else{
                pos=3;
                showFragments(aFG);
                if(!flag3)
                    flag3=true;
            }
            return true;

        });

        bar = new QBadgeView(this);
        bar.bindTarget(tab).setBadgeGravity(Gravity.CENTER | Gravity.START);
        bar.setGravityOffset(40, 0, true);

        initSetting();





        if(savedInstanceState==null) {
            FragmentTransaction tran = fm.beginTransaction();
  /*      hideFragments(tran);
        Fragment fg = new WeicoFG();
        map.put(R.id.weico, fg);
        map.put(R.id.title, new TitleFG());
        map.put(R.id.account, new AccountFG());
        tran.add(R.id.container, fg, WeicoFG.class.getName());
        flag.add(R.id.weico);*/
            wFG = new WeicoFG();
            tFG = new TitleFG();
            aFG = new AccountFG();
            tran.add(R.id.container, wFG, wFG.getName());
            flag.add(wFG);
            flag1=true;
            tran.commit();

        }else{

            pos=savedInstanceState.getInt("pos");

            flag1=savedInstanceState.getBoolean("flag1");
            flag2=savedInstanceState.getBoolean("flag2");
            flag3=savedInstanceState.getBoolean("flag3");



            wFG=(WeicoFG)fm.findFragmentByTag(WeicoFG.class.getName());
            if(wFG==null)
                wFG=new WeicoFG();
            tFG=(TitleFG)fm.findFragmentByTag(TitleFG.class.getName());
            if(tFG==null)
                tFG=new TitleFG();

            aFG=(AccountFG)fm.findFragmentByTag(AccountFG.class.getName());
            if(aFG==null)
                aFG=new AccountFG();

            wFG.setName(WeicoFG.class.getName());
            tFG.setName(TitleFG.class.getName());
            aFG.setName(AccountFG.class.getName());

            if(flag1)
                flag.add(wFG);
            if(flag2)
                flag.add(tFG);
            if(flag3)
                flag.add(aFG);

           // Log.i("tag",wFG.getName()+" "+tFG.getName()+" "+aFG.getName());

            if(flag1)
                Log.i("tag","flag1 ");

            if(flag2)
                Log.i("tag","flag2 ");

            if(flag3)
                Log.i("tag","flag3 ");

            if(pos==1){
                showFragments(wFG);
            }else if(pos==2){
                showFragments(tFG);
            }else{
                showFragments(aFG);
            }


        }

        receiver = new MessageReceiver();
        filter = new IntentFilter();
        filter.addAction("com.gapcoder.weico.MESSAGE");
        registerReceiver(receiver, filter);
        // Intent service=new Intent(this, MessageService.class);
        //startService(service);
        uploadCrash();

    }


    protected void initSetting() {

        SharedPreferences p = getSharedPreferences("setting", MODE_PRIVATE);
        Config.mode = p.getBoolean("mode", false);
        Config.box = p.getBoolean("box", false);
        Config.message = p.getBoolean("message", true);
        Config.weibo = p.getBoolean("weibo", true);

    }

    private void uploadCrash() {

        final File file = new File(getCacheDir(), "crash.log");

        if (!file.exists()) {
            return;
        }

        Long filelength = file.length();
        String str = null;

        byte[] filecontent = new byte[filelength.intValue()];
        try {

            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            str = new String(filecontent);

        } catch (Exception e) {

            System.out.println(e.toString());

        } finally {

        }

        String device = Build.VERSION.RELEASE + " " + Build.BRAND + "-" + Build.MODEL;
        final HashMap<String, String> map = new HashMap<>();
        map.put("token", Token.token);
        map.put("device", device);
        map.put("text", str);

        Pool.run(() -> {
            final SysMsg r = URLService.post("crash.php", map, SysMsg.class);
            Log.i("tag", r.getMsg());
            if (r.getCode().equals("OK")) {
                file.delete();
            }
        });
    }

    private void hideFragments(FragmentTransaction transaction) {

        if (map.size() == 0)
            return;
        Iterator<Fragment> it = map.values().iterator();
        while (it.hasNext()) {
            transaction.hide(it.next());
        }

    }

    private void showFragments(BaseFG fg){

        try {


            FragmentTransaction ft = fm.beginTransaction();

            Iterator<BaseFG> it = flag.iterator();
            while (it.hasNext()) {
                BaseFG tmp = it.next();
                if (tmp != fg)
                    ft.hide(tmp);
            }

            if (flag.contains(fg))
                ft.show(fg);
            else {
                flag.add(fg);
                ft.add(R.id.container, fg, fg.getName());
            }
            ft.commit();
        }catch(Exception e){
            Log.i("tag",e.toString());
        }
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pos", pos);
        outState.putBoolean("flag1",flag1);
        outState.putBoolean("flag2",flag2);
        outState.putBoolean("flag3",flag3);
        Log.i("tag","pos saved");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent service = new Intent(this, MessageService.class);
        stopService(service);
        unregisterReceiver(receiver);

        ActivityList.remove(this);
    }

    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int num = intent.getIntExtra("num", 0);
            Log.i("tag", "" + num);
            ((WeicoFG) map.get(R.id.weico)).message(num);
            bar.setBadgeNumber(-num);
        }
    }
}
