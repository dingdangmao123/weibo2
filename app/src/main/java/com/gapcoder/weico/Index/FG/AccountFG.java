package com.gapcoder.weico.Index.FG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gapcoder.weico.Account.Account;
import com.gapcoder.weico.Box.Box;
import com.gapcoder.weico.Change.Change;
import com.gapcoder.weico.Config;
import com.gapcoder.weico.General.SysMsg;
import com.gapcoder.weico.General.URLService;
import com.gapcoder.weico.General.UserModel;
import com.gapcoder.weico.Login.Login;
import com.gapcoder.weico.Post;
import com.gapcoder.weico.R;
import com.gapcoder.weico.Setting.Setting;
import com.gapcoder.weico.UserList.UserList;
import com.gapcoder.weico.Utils.ActivityList;
import com.gapcoder.weico.Utils.Curl;
import com.gapcoder.weico.Utils.Image;
import com.gapcoder.weico.Utils.Pool;
import com.gapcoder.weico.Utils.T;
import com.gapcoder.weico.Utils.Token;
import com.yuyh.library.imgsel.ISNav;
import com.yuyh.library.imgsel.common.ImageLoader;
import com.yuyh.library.imgsel.config.ISListConfig;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;


public class AccountFG extends BaseFG {

    UserModel.InnerBean user=new UserModel.InnerBean();

    String oldurl="";

    final int PLACE=0;
    final int SIGN=1;
    final int FACE=2;
    final int NAME=3;

    @BindView(R.id.face)
    ImageView face;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.care)
    TextView care;
    @BindView(R.id.careitem)
    LinearLayout careitem;
    @BindView(R.id.fans)
    TextView fans;
    @BindView(R.id.fansitem)
    LinearLayout fansitem;
    @BindView(R.id.place)
    TextView place;
    @BindView(R.id.placeitem)
    LinearLayout placeitem;
    @BindView(R.id.sign)
    TextView sign;
    @BindView(R.id.signitem)
    LinearLayout signitem;
    @BindView(R.id.weicoitem)
    LinearLayout weicoitem;

    ISListConfig config;
    boolean flag=false;



    @OnClick(R.id.face)
    void select(){
        ISNav.getInstance().toListActivity(this, config,FACE);
    }

    @OnClick(R.id.placeitem)
    void place(){

            Intent i=new Intent(getActivity(),Change.class);
            i.putExtra("key","place");
            i.putExtra("title","地区");
            i.putExtra("text",place.getText().toString());

            startActivityForResult(i, PLACE);

    }


    @OnClick(R.id.nameitem)
    void name(){

        Intent i=new Intent(getActivity(),Change.class);
        i.putExtra("key","name");
        i.putExtra("title","昵称");
        i.putExtra("text",name.getText().toString());

        startActivityForResult(i, NAME);

    }


    @OnClick(R.id.exititem)
    void exit(){
        Log.i("tag","exit");
        Token.exit(getActivity());
        Intent i=new Intent(getActivity(),Login.class);
        startActivity(i);
        ActivityList.exit();
    }

    @OnClick(R.id.setting)
    void setting(){
        Log.i("tag","setting");
        Intent i=new Intent(getActivity(),Setting.class);
        startActivity(i);
    }

    @OnClick(R.id.box)
    void box(){

        Log.i("tag","box");
        Intent i=new Intent(getActivity(),Box.class);
        startActivity(i);
    }

    @OnClick(R.id.signitem)
    void sign(){

        Intent i=new Intent(getActivity(),Change.class);
        i.putExtra("key","sign");
        i.putExtra("title","签名");
        i.putExtra("text",sign.getText().toString());
        startActivityForResult(i,SIGN);
    }

    @OnClick(R.id.weicoitem)
    void jump() {
        Intent i = new Intent(getActivity(), Account.class);
        i.putExtra("uid", user.getId());
        startActivity(i);
    }

    @OnClick(R.id.fansitem)
    void fans() {
        Intent i = new Intent(getActivity(), UserList.class);
        i.putExtra("uid", user.getId());
        i.putExtra("type","fans");
        startActivity(i);
    }

    @OnClick(R.id.careitem)
    void  care() {
        Intent i = new Intent(getActivity(), UserList.class);
        i.putExtra("uid", user.getId());
        i.putExtra("type","care");
        startActivity(i);
    }

    void post(final List<String> url){

        Pool.run(()->{
                HashMap<String, String> map = new HashMap<>();
                map.put("token", Token.getToken());
                map.put("key","face");
                final SysMsg r = URLService.upload("face.php", map, url, SysMsg.class);
                if (!check(r, null)) {
                    return;
                }
                Image.delete(getActivity(),Config.face+oldurl);
                UI(()->{
                        T.show(getActivity(), r.getMsg());
                        Refresh();
                });
        });
    }

    public AccountFG() {

        super();
        Log.i("tag","AccountFG");
        ISNav.getInstance().init(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);

            }
        });

        config = new ISListConfig.Builder()
                .multiSelect(false)
                .btnBgColor(R.color.colorPrimary)
                .btnTextColor(R.color.colorPrimary)
                .statusBarColor(R.color.colorPrimary)
                .title("头像设置")
                .titleColor(Color.WHITE)
                .titleBgColor(R.color.colorPrimary)
                .cropSize(1, 1, 200, 200)
                .needCrop(true)
                .needCamera(true)
                .maxNum(1)
                .build();
    }

    View init(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("tag","Account init");
        return inflater.inflate(R.layout.fragment_account_fg, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("tag","onResume account");
        if(!flag)
        {
            flag=true;
            Refresh();
        }
    }

    void Refresh() {
        Pool.run(()->{
                String url="account.php?token="+ Token.getToken();
                final SysMsg m = URLService.get(url, UserModel.class);
                Log.i("tag",url);
                if(!(m instanceof UserModel))
                    return ;
                user= ((UserModel) m).getInner();
                oldurl=user.getFace();
                UI(()->{
                        name.setText(user.getName());
                        fans.setText(String.valueOf(user.getFans()));
                        care.setText(String.valueOf(user.getCare()));
                        sign.setText(user.getSign());
                        place.setText(user.getPlace());
                });
                final Bitmap bit = Curl.getImage(Config.face+user.getFace());
                UI(()->{
                        face.setImageBitmap(bit);
                });
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data==null)
            return ;
        String res=data.getStringExtra("text");
        switch(requestCode){
            case PLACE:
                if(resultCode==RESULT_OK){
                    place.setText(res);
                    user.setPlace(res);
                }
                break;
            case NAME:
                if(resultCode==RESULT_OK){
                    name.setText(res);
                    user.setName(res);
                }
                break;
            case SIGN:
                if(resultCode==RESULT_OK){
                    sign.setText(res);
                    user.setSign(res);
                }
                break;
            case FACE:
                if(resultCode==RESULT_OK&&data!=null) {
                    List<String> pathList = data.getStringArrayListExtra("result");
                    post(pathList);
                }
                break;
        }

    }
}
