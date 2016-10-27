package com.sundyn.bluesky.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;

import com.squareup.picasso.Picasso;
import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseActivity;
import com.sundyn.bluesky.baseadapterhelper.BaseAdapterHelper;
import com.sundyn.bluesky.baseadapterhelper.QuickAdapter;
import com.sundyn.bluesky.bean.ImageFloder;
import com.sundyn.bluesky.hik.util.UtilFilePath;
import com.sundyn.bluesky.utils.CommonUtil;
import com.sundyn.bluesky.view.NormalTopBar;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class MyImageActivity extends BaseActivity {
    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.id_gridView)
    private GridView mGirdView;
    private QuickAdapter<String> mAdapter;

    /**
     * ��ʱ�ĸ����࣬���ڷ�ֹͬһ���ļ��еĶ��ɨ��
     */
    private HashSet<String> mDirPaths = new HashSet<String>();
    int totalCount = 0;
    /**
     * ɨ���õ����е�ͼƬ�ļ���
     */
    private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();
    /**
     * �洢�ļ����е�ͼƬ����
     */
    private int mPicsSize;
    /**
     * ͼƬ���������ļ���
     */
    /**
     * ���е�ͼƬ
     */
    private List<String> mImgs;
    private File mImgDir; // ���ͼƬ���ļ���·��
    private String mImgDirPath;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            disDIalog();
            // ΪView������
            data2View();
        }
    };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_myimagefile);
        x.view().inject(this);
        initTitleBar();
        initData();
    }

    /**
     * ��ͼƬ��䵽��ǰview
     */
    protected void data2View() {
        if (mImgDir == null) {
            showToast("ɨ�費����ͼ������Ƿ��ѽ�ͼ");
            return;
        }

        mImgs = Arrays.asList(mImgDir.list());
        mImgDirPath = mImgDir.getAbsolutePath();
        mAdapter.addAll(mImgs);
    }

    private void initData() {
        getImages();
        mAdapter = new QuickAdapter<String>(mContext, R.layout.gv_myimage_item) {

            @Override
            protected void convert(BaseAdapterHelper helper, final String item) {
                helper.getView(R.id.id_item_select).setVisibility(View.GONE);
                // ����ͼƬ
                helper.setImageBuilder(
                        R.id.id_item_image,
                        Picasso.with(mContext)
                                .load(new File(mImgDirPath + "/" + item))
                                .resize(CommonUtil.dip2px(mContext, 60),
                                        CommonUtil.dip2px(mContext, 60)));
                helper.getView(R.id.id_item_image).setOnClickListener(
                        new OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(mContext,
                                        ImagePagerActivity.class);
                                intent.putExtra(
                                        ImagePagerActivity.EXTRA_IMAGE_URLS,
                                        mImgs.toArray());
                                intent.putExtra(
                                        ImagePagerActivity.EXTRA_IMAGE_INDEX,
                                        mImgs.indexOf(item));
                                intent.putExtra(ImagePagerActivity.FROMNET,
                                        false);
                                intent.putExtra(
                                        ImagePagerActivity.NO_PARENTPATH, true);
                                startActivity(intent);
                            }
                        });

            }
        };
        mGirdView.setAdapter(mAdapter);
    }

    /**
     * ����ContentProviderɨ���ֻ��е�ͼƬ���˷��������������߳��� ���ͼƬ��ɨ�裬���ջ��jpg�����Ǹ��ļ���
     */
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            showToast("�����ⲿ�洢!");
            return;
        }
        // ��ʾ������
        showDialog();

        new Thread(new Runnable() {
            @Override
            public void run() {

                String firstImage = null;

                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = MyImageActivity.this
                        .getContentResolver();

                // ֻ��ѯjpeg��png��ͼƬ
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);

                while (mCursor.moveToNext()) {
                    // ��ȡͼƬ��·��
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                    // �õ���һ��ͼƬ��·��
                    if (firstImage == null)
                        firstImage = path;
                    // ��ȡ��ͼƬ�ĸ�·����
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null)
                        continue;
                    String dirPath = parentFile.getAbsolutePath();

                    // storage/emulated/0/BlueSky
                    if (!UtilFilePath.getPictureDirPath().getAbsolutePath()
                            .equals(dirPath)) {// ���������Ƶ��ͼ·���򷵻�
                        continue;
                    }
                    ImageFloder imageFloder = null;
                    // ����һ��HashSet��ֹ���ɨ��ͬһ���ļ��У���������жϣ�ͼƬ�����������൱�ֲ���~~��
                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        mDirPaths.add(dirPath);
                        // ��ʼ��imageFloder
                        imageFloder = new ImageFloder();
                        imageFloder.setDir(dirPath);
                        imageFloder.setFirstImagePath(path);
                    }

                    int picSize = parentFile.list(new FilenameFilter() {// ���ļ����µ�����
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg")
                                    || filename.endsWith(".png")
                                    || filename.endsWith(".jpeg"))
                                return true;
                            return false;
                        }
                    }).length;
                    totalCount += picSize;

                    imageFloder.setCount(picSize);
                    mImageFloders.add(imageFloder);

                    if (picSize > mPicsSize) {
                        mPicsSize = picSize;
                        mImgDir = parentFile;
                    }
                }
                mCursor.close();

                // ɨ����ɣ�������HashSetҲ�Ϳ����ͷ��ڴ���
                mDirPaths = null;

                // ֪ͨHandlerɨ��ͼƬ���
                mHandler.sendEmptyMessage(0x110);

            }
        }).start();

    }

    private void initTitleBar() {
        mTopBar.setBackVisibility(true);
        mTopBar.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                MyImageActivity.this.finish();
            }
        });
        mTopBar.setTitle("�ҵ���Ƭ");
        mTopBar.setActionTextVisibility(false);
    }
}
