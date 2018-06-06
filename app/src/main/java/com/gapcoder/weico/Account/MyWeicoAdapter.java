package com.gapcoder.weico.Account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.actionsheet.ActionSheet;
import com.gapcoder.weico.Comment.Comment;
import com.gapcoder.weico.Config;
import com.gapcoder.weico.General.SysMsg;
import com.gapcoder.weico.General.URLService;
import com.gapcoder.weico.Index.Adapter.GridAdapter;
import com.gapcoder.weico.Index.Model.WeicoModel;
import com.gapcoder.weico.R;
import com.gapcoder.weico.User.User;
import com.gapcoder.weico.Utils.Image;
import com.gapcoder.weico.Utils.LinkText;
import com.gapcoder.weico.Utils.Pool;
import com.gapcoder.weico.Utils.T;
import com.gapcoder.weico.Utils.Time;
import com.gapcoder.weico.Utils.Token;
import com.gapcoder.weico.Views.NineView.NineView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gapcoder on 2018/6/2.
 */


public class MyWeicoAdapter extends RecyclerView.Adapter<MyWeicoAdapter.SnapViewHolder> implements ActionSheet.ActionSheetListener{

    private Context mContext;

    private List<WeicoModel.InnerBean> data;

    private GridAdapter mAdapter;

    private int uid;
    private int deleteid;
    private int deletep;

    Typeface typeface;
    LinkText parse;

    public MyWeicoAdapter(List<WeicoModel.InnerBean> data, Context context,int uid) {
        this.data = data;
        this.mContext = context;
        this.uid=uid;
        parse = new LinkText(context);
        mAdapter= new GridAdapter(context);
        typeface =Typeface.createFromAsset(context.getAssets(), "fz.TTF");
    }

    @Override
    public MyWeicoAdapter.SnapViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.myweicoitem, parent, false);
        MyWeicoAdapter.SnapViewHolder h = new MyWeicoAdapter.SnapViewHolder(view,typeface);

        h.t4.setOnLongClickListener((View v)->{
            OnLongClick(h);
            return true;
        });

        h.Grid.setOnLongClickListener((View v)->{
            OnLongClick(h);
            return true;
        });



        h.t4.setOnClickListener((View v)->{
                Intent i = new Intent(mContext, Comment.class);
                int p = h.getAdapterPosition();
                i.putExtra("wid", data.get(p).getId());
                Log.i("tag", String.valueOf(data.get(p).getId()));
                mContext.startActivity(i);
            }
        );

        return h;

    }

    void OnLongClick(SnapViewHolder h){
        deletep = h.getAdapterPosition();
        deleteid=data.get(deletep).getId();
        if(Token.uid!=uid)
            return ;
        ActionSheet.createBuilder(mContext, ((AppCompatActivity)mContext).getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles("删除")
                .setCancelableOnTouchOutside(true)
                .setListener(MyWeicoAdapter.this).show();
    }

    @Override
    public void onBindViewHolder(MyWeicoAdapter.SnapViewHolder h, int position) {

        WeicoModel.InnerBean m = data.get(position);

        String[] ss= Time.format2(m.getTime()).split(" ");
        if(ss.length>1)
            h.t1.setText(ss[1]);

        h.t2.setText(ss[0]);
        h.t4.setMovementMethod(LinkMovementMethod.getInstance());
        h.t4.setText(parse.parse(m.getText()));

        if(Config.mode) {

            h.Grid.setVisibility(View.GONE);

        }else {

            if (m.getPhoto().length() > 0)
                h.Grid.setUrl((Activity) mContext, new ArrayList(Arrays.asList(m.getPhoto().split(","))));

            if (m.getPhoto().length() == 0) {
                h.Grid.setVisibility(View.GONE);
            } else {
                h.Grid.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
        actionSheet.dismiss();
    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        if(Token.uid!=uid)
            return ;

        if(deleteid<=0)
            return ;


        Pool.run(()->{

            HashMap<String, String> map = new HashMap<>();
            map.put("token", Token.token);
            map.put("id", deleteid+"");

            final SysMsg r = URLService.post("delete.php", map,SysMsg.class);
            if(r.getCode().equals("OK")){
                data.remove(deletep);
            }
            ((Activity)mContext).runOnUiThread(() -> {
                notifyDataSetChanged();
                T.show(mContext, r.getMsg());
            });


        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class SnapViewHolder extends RecyclerView.ViewHolder {


        ViewGroup p;
        NineView Grid;
        TextView t1;
        TextView t2;
        TextView t4;
        TextView t5;
        TextView t6;

        public SnapViewHolder(View itemView,Typeface tf) {
            super(itemView);

            p=(ViewGroup)itemView.findViewById(R.id.p);
            Grid = (NineView) itemView.findViewById(R.id.NineGrid);
            t1 = (TextView) itemView.findViewById(R.id.unit);
            t2 = (TextView) itemView.findViewById(R.id.time);
            t4 = (TextView) itemView.findViewById(R.id.text);
            t4.setTypeface(tf);

        }
    }
}



