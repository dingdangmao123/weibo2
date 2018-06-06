package com.gapcoder.weico.Index.FG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.gapcoder.weico.General.SysMsg;
import com.gapcoder.weico.General.URLService;
import com.gapcoder.weico.Index.Adapter.TitleAdapter;
import com.gapcoder.weico.Index.Model.TitleModel;
import com.gapcoder.weico.R;
import com.gapcoder.weico.Title.Title;
import com.gapcoder.weico.Utils.Pool;
import com.gapcoder.weico.Utils.T;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class TitleFG extends BaseFG {

    List<TitleModel.inner> data = new LinkedList<>();

    TitleAdapter adapter;


    @BindView(R.id.change)
    TextView change;

    @BindView(R.id.tag)
    TagContainerLayout tag;

    boolean flag = false;

    @BindView(R.id.search)
    SearchView search;


    @OnClick(R.id.change)
    void change(){
        Refresh();
    }


    public TitleFG() {

    }

    View init(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_title_fg, container, false);
    }

    @Override
    public void CreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState, View v) {

        search.setIconified(false);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String s) {
                Intent i=new Intent(getActivity(),Title.class);
                i.putExtra("title",s);
                getActivity().startActivity(i);
                return false;
            }
            @Override public boolean onQueryTextChange(String s) {
                return false;
            } });


        tag.setTheme(0);
        tag.setTagBackgroundColor(Color.TRANSPARENT);
        tag.setOnTagClickListener(new TagView.OnTagClickListener() {

            @Override
            public void onTagClick(int position, String text) {

               //T.show2(getActivity(),data.get(position).getId());


                int id=data.get(position).getId();
                Log.i("tag",id+"");
                Intent i=new Intent(getActivity(),Title.class);
                Bundle b=new Bundle();
                b.putInt("tid",id);
                b.putString("title",text);
                i.putExtras(b);
                startActivity(i);

            }

            @Override
            public void onTagLongClick(final int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });

        Refresh();
    }


    public void Refresh() {

        Pool.run(new Runnable() {
            @Override
            public void run() {

                final SysMsg m = URLService.get("title.php", TitleModel.class);
                if(!m.getCode().equals("OK"))
                {
                    UI(()->{
                        T.show2(getActivity(),m.getMsg());
                    });

                    return ;
                }
                data.clear();
                List<TitleModel.inner> t=((TitleModel)m).getInner();

                data.addAll(t);


               for(int  i=0;i<data.size();i++){
                   Log.i("tag",data.get(i).getId()+" "+data.get(i).getId());
               }


                String[] s=new String[t.size()];

                for(int i =0;i<s.length;i++)
                    s[i]=t.get(i).getTitle();

                UI(()->{
                    tag.setTags(s);
                });

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

    }

}
