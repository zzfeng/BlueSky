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
     * �洢�ļ����е�ͼƬ����
     */
    private int mPicsSize;
    /**
     * ͼƬ���������ļ���
     */
    private File mImgDir;
    /**
     * ���е�ͼƬ
     */
    private List<String> mImgs;

    private GridView mGirdView;
    private QuickAdapter<String> mAdapter;
    /**
     * ��ʱ�ĸ����࣬���ڷ�ֹͬһ���ļ��еĶ��ɨ��
     */
    private HashSet<String> mDirPaths = new HashSet<String>();

    /**
     * ɨ���õ����е�ͼƬ�ļ���
     */
    private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

    /**
     * �û�ѡ���ͼƬ���洢ΪͼƬ������·��
     */
    // public static List<String> mSelectedImage = new LinkedList<String>();
    public ArrayList<String> mSelectedImage = new ArrayList<String>();
    /**
     * �ļ���·��
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
            // ΪView������
            data2View();
            // ��ʼ��չʾ�ļ��е�popupWindw
            initListDirPopupWindw();
        }
    };
    private int count;

    /**
     * ΪView������
     */
    private void data2View() {
        if (mImgDir == null) {
            showToast("һ��ͼƬ��ûɨ�赽��");
            return;
        }

        mImgs = Arrays.asList(mImgDir.list());
        /**
         * ���Կ����ļ��е�·����ͼƬ��·���ֿ����棬����ļ������ڴ�����ģ�
         */

        mAdapter = new QuickAdapter<String>(mContext, R.layout.gv_myimage_item) {

            @Override
            protected void convert(final BaseAdapterHelper helper,
                                   final String item) {
                // ����no_pic
                helper.setImageResource(R.id.id_item_image,
                        R.mipmap.pictures_no);
                // ����no_selected
                helper.setImageResource(R.id.id_item_select,
                        R.mipmap.picture_unselected);
                // ����ͼƬ
                final ImageView mImageView = helper.getView(R.id.id_item_image);
                final ImageView mSelect = helper.getView(R.id.id_item_select);
                mDirPath = mImgDir.getAbsolutePath();
                ImageLoader.getInstance(3, Type.LIFO).loadImage(
                        mDirPath + "/" + item, mImageView);
                mImageView.setColorFilter(null);
                // ����ImageView�ĵ���¼�
                mImageView.setOnClickListener(new OnClickListener() {
                    // ѡ����ͼƬ�䰵����֮��֮
                    @Override
                    public void onClick(View v) {
                        if (mSelectedImage.size() + count >= 5) {
                            showToast("����ѡ������Ƭ");
                            return;
                        }
                        // �Ѿ�ѡ�����ͼƬ
                        if (mSelectedImage.contains(mDirPath + "/" + item)) {
                            mSelectedImage.remove(mDirPath + "/" + item);
                            mSelect.setImageResource(R.mipmap.picture_unselected);
                            mImageView.setColorFilter(null);
                        } else
                        // δѡ���ͼƬ
                        {
                            mSelectedImage.add(mDirPath + "/" + item);
                            mSelect.setImageResource(R.mipmap.pictures_selected);
                            mImageView.setColorFilter(Color
                                    .parseColor("#77000000"));
                        }

                    }
                });

                /**
                 * �Ѿ�ѡ�����ͼƬ����ʾ��ѡ�����Ч��
                 */
                if (mSelectedImage.contains(mDirPath + "/" + item)) {
                    mSelect.setImageResource(R.mipmap.pictures_selected);
                    mImageView.setColorFilter(Color.parseColor("#77000000"));
                }
            }
        };
        mGirdView.setAdapter(mAdapter);
        mAdapter.addAll(mImgs);
        mImageCount.setText(totalCount + "��");
    }

    ;

    /**
     * ��ʼ��չʾ�ļ��е�popupWindw
     */
    private void initListDirPopupWindw() {
        mListImageDirPopupWindow = new ListImageDirPopupWindow(
                LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
                mImageFloders, LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.list_dir, null));

        mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                // ���ñ�����ɫ�䰵
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        // ����ѡ���ļ��еĻص�
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
     * ����ContentProviderɨ���ֻ��е�ͼƬ���˷��������������߳��� ���ͼƬ��ɨ�裬���ջ��jpg�����Ǹ��ļ���
     */
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "�����ⲿ�洢", Toast.LENGTH_SHORT).show();
            return;
        }
        // ��ʾ������
        showDialog();

        new Thread(new Runnable() {
            @Override
            public void run() {

                String firstImage = null;

                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = SelectImageActivity.this
                        .getContentResolver();

                // ֻ��ѯjpeg��png��ͼƬ
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);

                Log.e("TAG", mCursor.getCount() + "");
                while (mCursor.moveToNext()) {
                    // ��ȡͼƬ��·��
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));

                    Log.e("TAG", path);
                    // �õ���һ��ͼƬ��·��
                    if (firstImage == null)
                        firstImage = path;
                    // ��ȡ��ͼƬ�ĸ�·����
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null)
                        continue;
                    String dirPath = parentFile.getAbsolutePath();
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

    /**
     * ��ʼ��View
     */
    private void initView() {
        mGirdView = (GridView) findViewById(R.id.id_gridView);
        mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
        mImageCount = (TextView) findViewById(R.id.id_total_count);

        mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);

    }

    private void initEvent() {
        /**
         * Ϊ�ײ��Ĳ������õ���¼�������popupWindow
         */
        mBottomLy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListImageDirPopupWindow
                        .setAnimationStyle(R.style.anim_popup_dir);
                mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

                // ���ñ�����ɫ�䰵
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

        mImageCount.setText(floder.getCount() + "��");
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
        mTopBar.setTitle("ͼƬѡ��");
        mTopBar.setActionTextVisibility(true);
        mTopBar.setActionText("ȷ��");
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
