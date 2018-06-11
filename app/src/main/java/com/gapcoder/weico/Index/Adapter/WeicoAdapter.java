package com.gapcoder.weico.Index.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gapcoder.weico.Account.Account;
import com.gapcoder.weico.Comment.Comment;
import com.gapcoder.weico.Config;
import com.gapcoder.weico.Index.Model.WeicoModel;
import com.gapcoder.weico.Photo;
import com.gapcoder.weico.R;
import com.gapcoder.weico.User.User;
import com.gapcoder.weico.Utils.Image;
import com.gapcoder.weico.Utils.LinkText;
import com.gapcoder.weico.Utils.Time;
import com.gapcoder.weico.Utils.Token;
import com.gapcoder.weico.Views.NineView.ArrowTextView;
import com.gapcoder.weico.Views.NineView.NineView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by suxiaohui on 2018/3/2.
 */


public class WeicoAdapter extends RecyclerView.Adapter<WeicoAdapter.SnapViewHolder> {

    private Context mContext;

    private List<WeicoModel.InnerBean> data;

    private GridAdapter mAdapter;

    Typeface typeface;
    LinkText parse;

    public WeicoAdapter(List<WeicoModel.InnerBean> data, Context context) {
        this.data = data;
        this.mContext = context;
        parse = new LinkText(context);
        mAdapter= new GridAdapter(context);
        typeface =Typeface.createFromAsset(context.getAssets(), "fz.TTF");
    }

    @Override
    public SnapViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.weicoitem, parent, false);
        final SnapViewHolder h = new SnapViewHolder(view,typeface);
        h.t4.setMovementMethod(LinkMovementMethod.getInstance());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, Comment.class);
                int p = h.getAdapterPosition();
                i.putExtra("wid", data.get(p).getId());
               // Log.i("tag", String.valueOf(data.get(p).getId()));
                mContext.startActivity(i);

            }
        });

        h.t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=null;
                int p = h.getAdapterPosition();
                int id=data.get(p).getUid();
                if(Token.uid!=id)
                   i = new Intent(mContext, User.class);
                else
                   i=new Intent(mContext, Account.class);

                i.putExtra("uid", id);
                mContext.startActivity(i);
            }
        });

        h.Grid.setTouch(new NineView.NineClick() {
            @Override
            public void OnClick(View v) {
                int p = h.getAdapterPosition();
                int cur=h.Grid.indexOfChild(v);
                Intent i=new Intent(mContext,Photo.class);
                Bundle b=new Bundle();
                b.putString("url",data.get(p).getPhoto());
                b.putInt("index",cur);
                i.putExtras(b);
                mContext.startActivity(i);
            }
            @Override
            public boolean OnLongClick(View v) {
                return false;
            }
        });

        return h;
    }

    @Override
    public void onBindViewHolder(SnapViewHolder h, int position) {

        WeicoModel.InnerBean m = data.get(position);
        h.t1.setText(m.getName());
        h.t2.setText(Time.format(m.getTime()));


        if(m.getText().length()>0) {
            h.t4.setText(parse.parse(m.getText()));
            h.t4.setVisibility(View.VISIBLE);
        }else
            h.t4.setVisibility(View.GONE);

        if(m.getComment()+m.getLove()>0){

            String s="";
            if(m.getLove()>0)
                s=m.getLove()+" 赞";
            if(m.getComment()>0)
                if(s.length()>0)
                    s=s+"   "+m.getComment()+" 评论";
                else
                    s=m.getComment()+" 评论";

            h.arrow.setText(s);
            h.arrow.setVisibility(View.VISIBLE);
        }else{
            h.arrow.setVisibility(View.GONE);
        }

        String face=Config.face+m.getFace();
        if (h.face.getTag()!=null&&!face.equals((String) h.face.getTag()))
            h.face.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.face));

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

        h.face.setTag(face);
        Image.down((Activity) mContext, h.face,face);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class SnapViewHolder extends RecyclerView.ViewHolder {


        NineView Grid;
        ImageView face;
        TextView t1;
        TextView t2;
        TextView t4;

        ArrowTextView arrow;

        public SnapViewHolder(View itemView,Typeface tf) {
            super(itemView);

            Grid = (NineView) itemView.findViewById(R.id.NineGrid);
            face = (ImageView) itemView.findViewById(R.id.face);
            arrow=(ArrowTextView) itemView.findViewById(R.id.arrow);
            t1 = (TextView) itemView.findViewById(R.id.name);
            t2 = (TextView) itemView.findViewById(R.id.time);
            t4 = (TextView) itemView.findViewById(R.id.text);

            t1.setTypeface(tf);
            t2.setTypeface(tf);
            t4.setTypeface(tf);
        }
    }
}


