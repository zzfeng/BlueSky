package com.sundyn.bluesky.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseActivity;
import com.sundyn.bluesky.baseadapterhelper.BaseAdapterHelper;
import com.sundyn.bluesky.baseadapterhelper.QuickAdapter;
import com.sundyn.bluesky.bean.ImageFloder;
import com.sundyn.bluesky.utils.ImageLoader;
import com.sundyn.bluesky.utils.ImageLoader.Type;
import com.sundyn.bluesky.view.ListImageDirPopupWindow;
import com.sundyn.bluesky.view.ListImageDirPopupWindow.OnImageDirSelected;
import com.sundyn.bluesky.view.NormalTopBar;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class SelectImageActivity extends BaseActivity implements
        OnImageDirSelected {
    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    public static final String SELECTCOUNT = "selectcount";

    /**
     * 存储文件夹中的图片数量
     */
    private int mPicsSize;
    /**
     * 图片数量最多的文件夹
     */
    private File mImgDir;
    /**
     * 所有的图片
     */
    private List<String> mImgs;

    private GridView mGirdView;
    private QuickAdapter<String> mAdapter;
    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private HashSet<String> mDirPaths = new HashSet<String>();

    /**
     * 扫描拿到所有的图片文件夹
     */
    private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

    /**
     * 用户选择的图片，存储为图片的完整路径
     */
    // public static List<String> mSelectedImage = new LinkedList<String>();
    public ArrayList<String> mSelectedImage = new ArrayList<String>();
    /**
     * 文件夹路径
     */
    private String mDirPath;

    private RelativeLayout mBottomLy;

    private TextView mChooseDir;
    private TextView mImageCount;
    int totalCount = 0;

    private int mScreenHeight;

    private ListImageDirPopupWindow mListImageDirPopupWindow;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            disDIalog();
            // 为View绑定数据
            data2View();
            // 初始化展示文件夹的popupWindw
            initListDirPopupWindw();
        }
    };
    private int count;

    /**
     * 为View绑定数据
     */
    private void data2View() {
        if (mImgDir == null) {
            showToast("一张图片都没扫描到！");
            return;
        }

        mImgs = Arrays.asList(mImgDir.list());
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */

        mAdapter = new QuickAdapter<String>(mContext, R.layout.gv_myimage_item) {

            @Override
            protected void convert(final BaseAdapterHelper helper,
                                   final String item) {
                // 设置no_pic
                helper.setImageResource(R.id.id_item_image,
                        R.mipmap.pictures_no);
                // 设置no_selected
                helper.setImageResource(R.id.id_item_select,
                        R.mipmap.picture_unselected);
                // 设置图片
                final ImageView mImageView = helper.getView(R.id.id_item_image);
                final ImageView mSelect = helper.getView(R.id.id_item_select);
                mDirPath = mImgDir.getAbsolutePath();
                ImageLoader.getInstance(3, Type.LIFO).loadImage(
                        mDirPath + "/" + item, mImageView);
                mImageView.setColorFilter(null);
                // 设置ImageView的点击事件
                mImageView.setOnClickListener(new OnClickListener() {
                    // 选择，则将图片变暗，反之则反之
                    @Override
                    public void onClick(View v) {
                        if (mSelectedImage.size() + count >= 5) {
                            showToast("至多选五张照片");
                            return;
                        }
                        // 已经选择过该图片
                        if (mSelectedImage.contains(mDirPath + "/" + item)) {
                            mSelectedImage.remove(mDirPath + "/" + item);
                            mSelect.setImageResource(R.mipmap.picture_unselected);
                            mImageView.setColorFilter(null);
                        } else
                        // 未选择该图片
                        {
                            mSelectedImage.add(mDirPath + "/" + item);
                            mSelect.setImageResource(R.mipmap.pictures_selected);
                            mImageView.setColorFilter(Color
                                    .parseColor("#77000000"));
                        }

                    }
                });

                /**
                 * 已经选择过的图片，显示出选择过的效果
                 */
                if (mSelectedImage.contains(mDirPath + "/" + item)) {
                    mSelect.setImageResource(R.mipmap.pictures_selected);
                    mImageView.setColorFilter(Color.parseColor("#77000000"));
                }
            }
        };
        mGirdView.setAdapter(mAdapter);
        mAdapter.addAll(mImgs);
        mImageCount.setText(totalCount + "张");
    }

    ;

    /**
     * 初始化展示文件夹的popupWindw
     */
    private void initListDirPopupWindw() {
        mListImageDirPopupWindow = new ListImageDirPopupWindow(
                LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
                mImageFloders, LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.list_dir, null));

        mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        // 设置选择文件夹的回调
        mListImageDirPopupWindow.setOnImageDirSelected(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectimage);
        x.view().inject(this);
        initTitleBar();
        count = getIntent().getIntExtra(SELECTCOUNT, 0);

        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;

        initView();
        getImages();
        initEvent();

    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        // 显示进度条
        showDialog();

        new Thread(new Runnable() {
            @Override
            public void run() {

                String firstImage = null;

                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = SelectImageActivity.this
                        .getContentResolver();

                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);

                Log.e("TAG", mCursor.getCount() + "");
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));

                    Log.e("TAG", path);
                    // 拿到第一张图片的路径
                    if (firstImage == null)
                        firstImage = path;
                    // 获取该图片的父路径名
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null)
                        continue;
                    String dirPath = parentFile.getAbsolutePath();
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

    /**
     * 初始化View
     */
    private void initView() {
        mGirdView = (GridView) findViewById(R.id.id_gridView);
        mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
        mImageCount = (TextView) findViewById(R.id.id_total_count);

        mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);

    }

    private void initEvent() {
        /**
         * 为底部的布局设置点击事件，弹出popupWindow
         */
        mBottomLy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListImageDirPopupWindow
                        .setAnimationStyle(R.style.anim_popup_dir);
                mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = .3f;
                getWindow().setAttributes(lp);
            }
        });
    }

    @Override
    public void selected(ImageFloder floder) {

        mImgDir = new File(floder.getDir());
        mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".jpg") || filename.endsWith(".png")
                        || filename.endsWith(".jpeg"))
                    return true;
                return false;
            }
        }));
        mAdapter.clear();
        mAdapter.addAll(mImgs);

        mImageCount.setText(floder.getCount() + "张");
        mChooseDir.setText(floder.getName());
        mListImageDirPopupWindow.dismiss();

    }

    private void initTitleBar() {
        mTopBar.setBackVisibility(true);
        mTopBar.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SelectImageActivity.this.finish();
            }
        });
        mTopBar.setTitle("图片选择");
        mTopBar.setActionTextVisibility(true);
        mTopBar.setActionText("确定");
        mTopBar.setOnActionListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra("picdata", mSelectedImage);
                setResult(0x11, intent);
                SelectImageActivity.this.finish();
            }
        });
    }

}
