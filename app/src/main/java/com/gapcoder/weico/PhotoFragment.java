package com.gapcoder.weico;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.gapcoder.weico.Utils.Compress;
import com.gapcoder.weico.Utils.DiskLRU;
import com.gapcoder.weico.Utils.Image;
import com.gapcoder.weico.Utils.Pool;
import com.gapcoder.weico.Utils.T;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

import java.io.File;
import java.io.FileNotFoundException;

import static android.widget.ImageView.*;

public class PhotoFragment extends Fragment {


    private PhotoView mPhotoView;
    private ProgressBar bar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        bar = (ProgressBar) view.findViewById(R.id.progress);
        mPhotoView = (PhotoView) view.findViewById(R.id.photoview);

        mPhotoView.setScaleType(ScaleType.CENTER);
        mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                return true;
            }
        });
        mPhotoView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {

            }
        });

        String url = (String) getArguments().get("url");
        String from = (String) getArguments().get("from");

        if (!from.equals("post"))
            mPhotoView.setOnLongClickListener((View v) -> {
                try {

                    MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
                            DiskLRU.getCacheDir(getActivity()) + File.separator + url, url, null);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(new File(DiskLRU.getCacheDir(getActivity()) + File.separator + url));
                    intent.setData(uri);
                    getActivity().sendBroadcast(intent);
                    T.show2(getActivity(), "保存成功");

                } catch (FileNotFoundException e) {
                    T.show2(getActivity(), "出错啦!");
                }

                return true;
            });

        // Log.i("tag","down "+url);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int h = dm.heightPixels;
        int w = dm.widthPixels;

        if (!from.equals("post"))
            Image.getFromDisk(getActivity(), this::update, mPhotoView, url, w, h);
        else
            getFromDisk(url, w, h);

        return view;
    }

    void update() {
        bar.setVisibility(View.GONE);
        mPhotoView.setVisibility(View.VISIBLE);
    }

    void getFromDisk(String url, int w, int h) {
        Pool.run(() -> {
            Bitmap b = Compress.decodeFile(url, w, h);
            getActivity().runOnUiThread(() -> {

                mPhotoView.setImageBitmap(b);
                update();
            });
        });
    }

}