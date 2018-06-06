package com.gapcoder.weico.Account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

import com.baoyz.actionsheet.ActionSheet;
import com.gapcoder.weico.Config;
import com.gapcoder.weico.General.Base;
import com.gapcoder.weico.General.SysMsg;
import com.gapcoder.weico.General.URLService;
import com.gapcoder.weico.General.UserModel;
import com.gapcoder.weico.Index.Adapter.WeicoAdapter;
import com.gapcoder.weico.Index.Model.WeicoModel;
import com.gapcoder.weico.Post;
import com.gapcoder.weico.R;
import com.gapcoder.weico.Utils.Curl;
import com.gapcoder.weico.Utils.Pool;
import com.gapcoder.weico.Utils.T;
import com.gapcoder.weico.Utils.Token;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yuyh.library.imgsel.ISNav;
import com.yuyh.library.imgsel.config.ISListConfig;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class Account extends Base{

    LinkedList<WeicoModel.InnerBean> data = new LinkedList<>();
    LinkedList<WeicoModel.InnerBean> tmp = new LinkedList<>();
    MyWeicoAdapter adapter;
    UserModel.InnerBean m;

    int cache = 20;
    int id = 0;
    final int BG=0;

    @BindView(R.id.bg)
    ImageView bg;

    @BindView(R.id.face)
    CircleImageView face;

    @BindView(R.id.timeline)
    RecyclerView tl;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout rf;

    BgListener bgListener=new BgListener();;

    ISListConfig config;

    private int uid = 0;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_account);
    }


    @Override
    public void init() {

        if ((uid = getIntent().getIntExtra("uid", 0)) == 0)
            return;

        config = new ISListConfig.Builder()
                .multiSelect(false)
                .btnBgColor(R.color.colorPrimary)
                .btnTextColor(Color.WHITE)
                .statusBarColor(R.color.colorPrimary)
                .title("封面设置")
                .titleColor(Color.WHITE)
                .titleBgColor(R.color.colorPrimary)
                .needCamera(true)
                .maxNum(1)
                .build();


        adapter = new MyWeicoAdapter(data, this,uid);
        tl.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        tl.setAdapter(adapter);
        tl.setNestedScrollingEnabled(false);
        rf.setOnRefreshListener((RefreshLayout refreshlayout)->{Refresh(1);});
        rf.setOnLoadmoreListener((RefreshLayout refreshlayout)->{Refresh(0);});
        getUser();
        rf.autoRefresh();

    }

    void getUser() {

        Pool.run(()->{
                String url = "account.php?uid=" + "" + uid;
                final SysMsg t = URLService.get(url, UserModel.class);
                if (!check(t, rf)) {
                    return;
                }
                m = ((UserModel) t).getInner();
                final Bitmap f = Curl.getImage(Config.face+m.getFace());
                final Bitmap b = Curl.getImage(Config.bg+m.getBg());
                UI(()->{
                    face.setImageBitmap(f);
                    bg.setImageBitmap(b);
                });
        });
    }

    public void Refresh(final int flag) {

        if (flag == 1) {
            if (data.size() != 0) {
                id = data.get(0).getId();
            }
        } else {
            id = data.get(data.size() - 1).getId();
        }

        Pool.run(()->{

                String url = "myweico.php?uid=" + uid + "&flag=" + String.valueOf(flag) + "&id=" + String.valueOf(id);
                Log.i("tag", url);
                SysMsg t = URLService.get(url, WeicoModel.class);
                if (!check(t, rf)) {
                    return;
                }

                tmp = ((WeicoModel) t).getInner();
                if (flag == 1) {
                    for (int i = 0; i < tmp.size(); i++)
                        data.addFirst(tmp.get(tmp.size() - i - 1));
                    int n = data.size() - cache;
                    for (int i = 0; i < n; i++) {
                        data.removeLast();
                    }
                } else if (tmp.size() > 0) {
                    data.addAll(tmp);
                    int n = data.size() - cache;
                    for (int i = 0; i < n; i++){

                        data.removeFirst();
                    }
                }

                UI(new Runnable() {
                    @Override
                    public void run() {
                        SmartRefresh(rf);
                        adapter.notifyDataSetChanged();
                    }
                });
        });

    }


    @OnLongClick(R.id.bg)
    boolean OnChange(){

       String tip="";
        if(Token.uid==uid)
            tip="更换封面";
        else
            tip="赞这个封面";

        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles(tip)
                .setCancelableOnTouchOutside(true)
                .setListener(bgListener).show();
        return true;
    }


     class BgListener implements ActionSheet.ActionSheetListener{


        @Override
        public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
            actionSheet.dismiss();
        }

        @Override
        public void onOtherButtonClick(ActionSheet actionSheet, int index) {
            if(Token.uid==uid){
                change();
            }else
                paise();
        }

    }


    void paise(){

        Pool.run(()->{
            HashMap<String, String> map = new HashMap<>();
            map.put("token", Token.token);
            map.put("hid", uid+"");

            final SysMsg r = URLService.post("praise_bg.php", map,  SysMsg.class);
            UI(() -> {
                T.show(Account.this, r.getMsg());
            });
        });
    }

    void change(){
            ISNav.getInstance().toListActivity(this, config,BG);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data==null)
            return ;
        String res=data.getStringExtra("text");
        switch(requestCode){
            case BG:
                if(resultCode==RESULT_OK&&data!=null) {
                    List<String> pathList = data.getStringArrayListExtra("result");
                    post(pathList);
                }
                break;
        }

    }
    void post(final List<String> url){

        Pool.run(()->{
                HashMap<String, String> map = new HashMap<>();
                map.put("token", Token.token);
                map.put("key","bg");
                final SysMsg r = URLService.upload("face.php", map, url, SysMsg.class);
                if (!check(r, null)) {
                    return;
                }
                UI(()->{
                        T.show(Account.this, r.getMsg());
                       // Refresh();
                    });
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
