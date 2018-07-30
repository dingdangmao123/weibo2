package com.gapcoder.weico;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gapcoder.weico.General.Base;
import com.gapcoder.weico.General.SysMsg;
import com.gapcoder.weico.General.URLService;
import com.gapcoder.weico.Utils.Compress;
import com.gapcoder.weico.Utils.Pool;
import com.gapcoder.weico.Utils.T;
import com.gapcoder.weico.Utils.Token;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class Post extends Base {

    @BindView(R.id.text)
    EditText text;

    List<String> url = new ArrayList<>();
    String weibo="";
    Adapter adapter;

    int w=0;

    final int IMAGE = 0;

    boolean ok=false;

    @BindView(R.id.container)
    RecyclerView container;

    @BindView(R.id.count)
    TextView count;

    @OnClick(R.id.select)
    void selectCheck() {
        Permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, () -> {
            MultiImageSelector.create().count(9 - url.size())
                    .start(this, IMAGE);
        });
    }

    @OnTextChanged(R.id.text)
    void input(CharSequence s, int start, int before, int c) {
        int n = 200 - s.length();
        count.setText("" + n);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_post);
    }

    @Override
    public void init() {
        adapter = new Adapter(url, this);
        container.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        container.setAdapter(adapter);
        //box();
    }

    void box() {
        Pool.run(() -> {

            Log.i("tag","box");
            File f = new File(getCacheDir(), "box");
            if (!f.exists())
                return;

            Log.i("tag","box2");
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
                inner ins = (inner) in.readObject();
                url.clear();
                url.addAll(ins.getUrl());
                f.delete();
                UI(() -> {
                    text.setText(ins.text);
                    adapter.notifyDataSetChanged();
                });

            } catch (Exception e) {
                Log.i("tag", e.toString());
            }
        });

    }

    void post() {

        weibo = text.getText().toString();
        if(weibo.length()==0&&url.size()==0){
            return ;
        }

        if (url.size() > 9) {
            T.show(Post.this, "你已经超过九张图片!");
            return;
        }

        if (weibo.length() > 200) {
            T.show(Post.this, "超过长度限制");
            return;
        }


        Pool.run(()->{

                HashMap<String, String> map = new HashMap<>();
                map.put("token", Token.getToken());
                map.put("text", weibo);

                final SysMsg r = URLService.upload("upload.php", map, url, SysMsg.class);
                if(r!=null) {
                    UI(() -> {
                        T.show(Post.this, r.getMsg());
                    });
                }

                if (r!=null&&!r.getCode().equals(Config.SUCCESS)) {

                    inner ins = new inner(weibo, url);
                    try {
                        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(getCacheDir(), "box")));
                        out.writeObject(ins);
                        out.close();
                    } catch (Exception e) {

                    }
                }
        });

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ok, menu);
        return true;
    }

    @Override
    public void onItemSelected(int id) {
        if (id == R.id.TextOk) {
            ok=true;
            post();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus&&w==0)
        {
          w=container.getWidth();
          adapter.setW(w);
          box();
          Log.i("tag","onWindowFocusChanged");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE) {
            if (resultCode == RESULT_OK) {
                List<String> t = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (t.size() == 0)
                    return;
                if (t.size() + url.size() > 9) {
                    T.show(Post.this, "图片不能超过九张");
                    return;
                }
                url.addAll(t);
                adapter.setW(w);
                adapter.notifyDataSetChanged();

            }
        }
    }

    @Override
    public void beforeFinish() {

        if(ok||!Config.box)
            return ;

        weibo=text.getText().toString();
        if(weibo.length()==0&&url.size()==0)
            return ;
        Pool.run(()->{
            inner ins = new inner(weibo, url);
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(getCacheDir(), "box")));
                out.writeObject(ins);
                out.close();
            } catch (Exception e) {
                Log.i("tag",e.toString());
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        weibo=text.getText().toString();
        if(weibo.length()==0&&url.size()==0)
            return ;

        outState.putString("weibo",weibo);
    }

    static class Adapter extends RecyclerView.Adapter<Adapter.SnapViewHolder> {

        private int w=0;

        private Context context;

        private List<String> url;

        Typeface typeface;

        public Adapter(List<String> url, Context context) {

            this.url = url;
            this.context = context;
        }

        public void setW(int w){
            this.w=w;
        }

        @Override
        public SnapViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            final int gap=4;
            ViewGroup.MarginLayoutParams lp=new  ViewGroup.MarginLayoutParams((w-gap*8)/4,(w-gap*8)/4);
            lp.setMargins(0,gap,gap,gap);
            ImageView iv=new ImageView(context);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

            iv.setLayoutParams(lp);

            final SnapViewHolder h = new SnapViewHolder(iv);

            h.iv.setOnLongClickListener((View v) -> {
                int pos = h.getAdapterPosition();
                url.remove(pos);
                notifyDataSetChanged();
                return false;
            });

            h.iv.setOnClickListener((View v)->{

                int p = h.getAdapterPosition();
                StringBuffer sb=new StringBuffer();
                for(int i=0;i<url.size();i++)
                    sb.append(url.get(i)+",");

                sb.deleteCharAt(sb.length()-1);

                Intent i=new Intent(context,Photo.class);
                Bundle b=new Bundle();

                b.putString("url",sb.toString());
                b.putInt("index",p);
                b.putString("from","post");
                i.putExtras(b);
                context.startActivity(i);

            });
            return h;
        }

        @Override
        public void onBindViewHolder(final SnapViewHolder h, final int position) {
            Pool.run(() -> {
                ViewGroup.LayoutParams lp=h.iv.getLayoutParams();
                final Bitmap bit = Compress.decodeFile(url.get(position), lp.width, lp.height);
                ((Activity) context).runOnUiThread(() -> {
                    h.iv.setImageBitmap(bit);
                });
            });
        }

        @Override
        public int getItemCount() {
            return url.size();
        }

        static class SnapViewHolder extends RecyclerView.ViewHolder {

            ImageView iv;

            public SnapViewHolder(View itemView) {
                super(itemView);
                iv = (ImageView)itemView;

            }
        }


    }

    public static class inner implements Serializable {

        private String text;
        private List<String> url;

        public inner(String text, List<String> url) {
            this.text = text;
            this.url = url;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public List<String> getUrl() {
            return url;
        }

        public void setUrl(List<String> url) {
            this.url = url;
        }
    }

}
