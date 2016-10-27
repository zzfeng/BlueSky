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
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private HashSet<String> mDirPaths = new HashSet<String>();
    int totalCount = 0;
    /**
     * 扫描拿到所有的图片文件夹
     */
    private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();
    /**
     * 存储文件夹中的图片数量
     */
    private int mPicsSize;
    /**
     * 图片数量最多的文件夹
     */
    /**
     * 所有的图片
     */
    private List<String> mImgs;
    private File mImgDir; // 最多图片的文件夹路径
    private String mImgDirPath;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            disDIalog();
            // 为View绑定数据
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
     * 将图片填充到当前view
     */
    protected void data2View() {
        if (mImgDir == null) {
            showToast("扫描不到截图，检查是否已截图");
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
                // 设置图片
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
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            showToast("暂无外部存储!");
            return;
        }
        // 显示进度条
        showDialog();

        new Thread(new Runnable() {
            @Override
            public void run() {

                String firstImage = null;

                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = MyImageActivity.this
                        .getContentResolver();

                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);

                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                    // 拿到第一张图片的路径
                    if (firstImage == null)
                        firstImage = path;
                    // 获取该图片的父路径名
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null)
                        continue;
                    String dirPath = parentFile.getAbsolutePath();

                    // storage/emulated/0/BlueSky
                    if (!UtilFilePath.getPictureDirPath().getAbsolutePath()
                            .equals(dirPath)) {// 如果不是视频截图路径则返回
                        continue;
                    }
                    ImageFloder imageFloder = null;
                    // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        mDirPaths.add(dirPath);
                        // 初始化imageFloder
                        imageFloder = new ImageFloder();
                        imageFloder.setDir(dirPath);
                        imageFloder.setFirstImagePath(path);
                    }

                    int picSize = parentFile.list(new FilenameFilter() {// 该文件夹下的总数
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

                // 扫描完成，辅助的HashSet也就可以释放内存了
                mDirPaths = null;

                // 通知Handler扫描图片完成
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
        mTopBar.setTitle("我的照片");
        mTopBar.setActionTextVisibility(false);
    }
}
