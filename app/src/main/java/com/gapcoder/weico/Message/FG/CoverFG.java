package com.gapcoder.weico.Message.FG;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gapcoder.weico.General.SysMsg;
import com.gapcoder.weico.General.URLService;
import com.gapcoder.weico.Message.Adapter.CoverAdapter;
import com.gapcoder.weico.Message.Model.CommModel;
import com.gapcoder.weico.Message.Model.CoverModel;
import com.gapcoder.weico.R;
import com.gapcoder.weico.UserList.UserListAdapter;
import com.gapcoder.weico.UserList.UserListModel;
import com.gapcoder.weico.Utils.Pool;
import com.gapcoder.weico.Utils.Token;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.LinkedList;

import butterknife.BindView;

public class CoverFG extends BaseFG {

    LinkedList<CoverModel.InnerBean> data = new LinkedList<>();
    LinkedList<CoverModel.InnerBean> tmp = new LinkedList<>();
    CoverAdapter adapter;
    @BindView(R.id.timeline)
    RecyclerView tl;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout rf;


    int cache = 10;
    int id = 0;

    boolean flag=false;

    @Override
    View init(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cover_fg, container, false);
    }

    @Override
    public void CreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState, View v) {
        adapter = new CoverAdapter(data, getActivity());
        tl.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        tl.setAdapter(adapter);

        rf.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                Refresh(1);
            }
        });
        rf.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                Refresh(0);
            }
        });



    }
    @Override
    public void onResume() {
        super.onResume();
        if(!flag)
        {
            flag=true;
            rf.autoRefresh();
        }
    }

    void Refresh(int flag) {



        if (flag == 1) {
            if (data.size() != 0) {
                id = data.get(0).getId();
            }
        } else {
            id = data.get(data.size() - 1).getId();
        }



        Pool.run(()->{
                String url = "cover.php?token=" + Token.token + "&flag=" + flag + "&id=" + id;
                Log.i("tag",url);
                final SysMsg m = URLService.get(url, CoverModel.class);

                if (!check(m, rf)) {
                    return;
                }
                tmp = ((CoverModel) m).getInner();

                if (flag == 1) {
                    for (int i = 0; i < tmp.size(); i++)
                        data.addFirst(tmp.get(tmp.size() - i - 1));
                    int n = data.size() - cache;
                    for (int i = 0; i < n; i++) {
                        data.removeLast();
                    }
                } else if(tmp.size()>0){
                    data.addAll(tmp);
                    int n = data.size() - cache;
                    for (int i = 0; i < n; i++) {
                        data.removeFirst();
                    }
                }

                UI(()->{
                        if(rf.isRefreshing())
                            rf.finishRefresh(true);
                        if(rf.isLoading())
                            rf.finishLoadmore(true);

                        adapter.notifyDataSetChanged();
                });

        });
    }

}

