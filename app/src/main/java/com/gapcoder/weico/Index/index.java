package com.gapcoder.weico.Index;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.gapcoder.weico.Index.FG.AccountFG;
import com.gapcoder.weico.Index.FG.TitleFG;
import com.gapcoder.weico.Index.FG.WeicoFG;
import com.gapcoder.weico.MessageService.MessageService;
import com.gapcoder.weico.R;
import com.gapcoder.weico.Utils.ActivityList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import q.rorbin.badgeview.QBadgeView;

public class index extends AppCompatActivity {

    FragmentManager fm = getSupportFragmentManager();
    HashMap<Integer, Fragment> map = new HashMap<>();
    HashSet<Integer> flag = new HashSet<>();

    @BindView(R.id.tab)
    BottomNavigationView tab;

    private IntentFilter filter;
    private MessageReceiver receiver;

    QBadgeView bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        final Typeface typeface = Typeface.createFromAsset(getAssets(), "fz.TTF");

        LayoutInflaterCompat.setFactory(LayoutInflater.from(this), new LayoutInflaterFactory()
        {
            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs)
            {
                AppCompatDelegate delegate = getDelegate();
                View view = delegate.createView(parent, name, context, attrs);

                if ( view!= null && (view instanceof TextView))
                {
                    ((TextView) view).setTypeface(typeface);
                }
                return view;
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        ButterKnife.bind(this);
        ActivityList.add(this);

        tab.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.weico)
                    bar.hide(false);
                FragmentTransaction tran = fm.beginTransaction();
                hideFragments(tran);
                int id = item.getItemId();
                if (!flag.contains(id)) {
                    tran.add(R.id.container, map.get(id));
                    flag.add(id);
                }
                tran.show(map.get(id));
                tran.commit();
                return true;
            }
        });

        bar = new QBadgeView(this);

        bar.bindTarget(tab).setBadgeGravity(Gravity.CENTER|Gravity.START);
        bar.setGravityOffset(40,0,true);

        FragmentTransaction tran = fm.beginTransaction();
        hideFragments(tran);
        Fragment fg = new WeicoFG();
        map.put(R.id.weico, fg);
        map.put(R.id.title, new TitleFG());
        map.put(R.id.account, new AccountFG());
        tran.add(R.id.container, fg);
        flag.add(R.id.weico);
        tran.commit();

        receiver=new MessageReceiver();
        filter=new IntentFilter();
        filter.addAction("com.gapcoder.weico.MESSAGE");
        registerReceiver(receiver,filter);
       // Intent service=new Intent(this, MessageService.class);
        //startService(service);
    }

    private void hideFragments(FragmentTransaction transaction) {

        if (map.size() == 0)
            return;
        Iterator<Fragment> it = map.values().iterator();
        while (it.hasNext()) {
            transaction.hide(it.next());
        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent service=new Intent(this, MessageService.class);
        stopService(service);
        unregisterReceiver(receiver);
        ActivityList.remove(this);
    }

    class MessageReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int num=intent.getIntExtra("num",0);
            Log.i("tag",""+num);
            ((WeicoFG)map.get(R.id.weico)).message(num);
            bar.setBadgeNumber(-num);
        }
    }
}
