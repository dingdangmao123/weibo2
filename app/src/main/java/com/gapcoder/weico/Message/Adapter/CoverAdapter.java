package com.gapcoder.weico.Message.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gapcoder.weico.Config;
import com.gapcoder.weico.Message.Model.CoverModel;
import com.gapcoder.weico.R;
import com.gapcoder.weico.User.User;
import com.gapcoder.weico.UserList.UserListModel;
import com.gapcoder.weico.Utils.Image;
import com.gapcoder.weico.Utils.Time;

import java.util.List;

/**
 * Created by gapcoder on 2018/6/4.
 */

public class CoverAdapter extends RecyclerView.Adapter<CoverAdapter.SnapViewHolder> {

    private Context mContext;

    private List<CoverModel.InnerBean> data;

    public CoverAdapter(List<CoverModel.InnerBean> data, Context context) {
        this.data = data;
        this.mContext = context;
    }

    @Override
    public CoverAdapter.SnapViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.coveritem, parent, false);
        final CoverAdapter.SnapViewHolder h= new CoverAdapter.SnapViewHolder(view);
        h.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p=h.getAdapterPosition();
                Intent i=new Intent(mContext,User.class);
                i.putExtra("uid",data.get(p).getId());
                mContext.startActivity(i);
            }
        });
        return h;
    }

    @Override
    public void onBindViewHolder(CoverAdapter.SnapViewHolder h, int position) {

        CoverModel.InnerBean m = data.get(position);
        h.name.setText(m.getName());
        h.cover.setText(Time.format(m.getTime())+" 赞了你的封面");
        h.face.setTag(Config.face+m.getFace());
        Image.down((Activity)mContext,h.face, Config.face+m.getFace());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class SnapViewHolder extends RecyclerView.ViewHolder {

        View v;
        ImageView face;
        TextView name;
        TextView cover;

        public SnapViewHolder(View itemView) {
            super(itemView);
            v=itemView;
            face=(ImageView)itemView.findViewById(R.id.face);
            name = (TextView) itemView.findViewById(R.id.name);
            cover = (TextView) itemView.findViewById(R.id.cover);
        }
    }
}

