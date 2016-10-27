package com.sundyn.bluesky.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.sundyn.bluesky.R;
import com.sundyn.bluesky.view.photoview.PhotoViewAttacher;
import com.sundyn.bluesky.view.photoview.PhotoViewAttacher.OnPhotoTapListener;

import java.io.File;

public class ImageDetailFragment extends Fragment {
    private String mImageUrl;
    private boolean formNet;

    private ImageView mImageView;
    private ProgressBar progressBar;
    private PhotoViewAttacher mAttacher;

    public static ImageDetailFragment newInstance(String imageUrl, boolean formNet) {
        final ImageDetailFragment f = new ImageDetailFragment();

        final Bundle args = new Bundle();
        args.putString("url", imageUrl);
        args.putBoolean("formNet", formNet);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString("url")
                : null;
        formNet = getArguments() != null ? getArguments().getBoolean("formNet")
                : false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.image_detail_fragment,
                container, false);
        mImageView = (ImageView) v.findViewById(R.id.image);
        mAttacher = new PhotoViewAttacher(mImageView);

        mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2) {
                getActivity().finish();
            }
        });

        progressBar = (ProgressBar) v.findViewById(R.id.loading);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (formNet) {
            Picasso.with(getActivity()).load(mImageUrl)
                    .into(mImageView, new Callback() {

                        @Override
                        public void onError() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                            mAttacher.update();
                        }

                    });
        } else {
            Picasso.with(getActivity()).load(new File(mImageUrl))
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.mipmap.pictures_no)
                    .error(R.mipmap.pictures_no)
                    .into(mImageView, new Callback() {

                        @Override
                        public void onError() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                            mAttacher.update();
                        }

                    });
        }
    }

}
