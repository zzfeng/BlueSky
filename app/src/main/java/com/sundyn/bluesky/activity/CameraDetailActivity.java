package com.sundyn.bluesky.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseActivity;
import com.sundyn.bluesky.baseadapterhelper.CameraImageAdapter;
import com.sundyn.bluesky.bean.CheckProblem;
import com.sundyn.bluesky.bean.CheckProblem.ProblemItem;
import com.sundyn.bluesky.bean.User;
import com.sundyn.bluesky.hik.live.LiveModel;
import com.sundyn.bluesky.hik.util.UtilFilePath;
import com.sundyn.bluesky.utils.CommonUtil;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.utils.GsonTools;
import com.sundyn.bluesky.utils.ReportUtils;
import com.sundyn.bluesky.view.ActionSheetDialog;
import com.sundyn.bluesky.view.ActionSheetDialog.OnSheetItemClickListener;
import com.sundyn.bluesky.view.ActionSheetDialog.SheetItemColor;
import com.sundyn.bluesky.view.ChoiceDialogFra;
import com.sundyn.bluesky.view.FlowLayout;
import com.sundyn.bluesky.view.NormalTopBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class CameraDetailActivity extends BaseActivity implements
        OnClickListener, OnItemClickListener {

    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.surfaceView)
    private SurfaceView mSurfaceView;
    @ViewInject(R.id.surfaceViewLayout)
    private RelativeLayout surfaceViewLayout;
    @ViewInject(R.id.writView)
    private TextView writView;
    @ViewInject(R.id.liveCaptureBtn)
    private ImageView liveCaptureBtn;
    @ViewInject(R.id.full)
    private ImageView full;
    @ViewInject(R.id.imageGridView)
    private GridView imageGridView;
    @ViewInject(R.id.extraMessage)
    private EditText extraMessage;// 附加信息
    @ViewInject(R.id.bt_commit)
    private Button bt_commit;
    @ViewInject(R.id.tag_container)
    private FlowLayout tagContainer;// 巡查问题
    @ViewInject(R.id.ll_fullControl)
    private LinearLayout ll_fullControl;// 全屏控制隐藏
    @ViewInject(R.id.id_sv_cameardetail)
    private ScrollView sv_cameardetail;//

    private boolean ifFull = false;// 是否是全屏状态

    private CameraImageAdapter imageAdapter;
    private ArrayList<String> imagePath = new ArrayList<String>();// 截图所存集合
    private ArrayList<ProblemItem> problemList;// 巡查问题集合
    private String mCameraID = null;// 摄像头ID
    private String camera_name;

    private ArrayList<String> problemCheckList = new ArrayList<String>();// 选择的检查问题
    private final static String ROLE_SITE_MANAGER = "ROLE_SITE_MANAGER";// 工地登录，只能查看视频

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_cameradetail);
        x.view().inject(this);
        initTitleBar();
        initView();
        initData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        writView.setVisibility(View.VISIBLE);
        LiveModel.getLiveModel().play(mCameraID, this, mSurfaceView, writView);
    }

    private void initView() {
        if (isSiteManager())
            sv_cameardetail.setVisibility(View.GONE);

        imageAdapter = new CameraImageAdapter(mContext, imagePath);
        imageGridView.setAdapter(imageAdapter);
        imageGridView.setOnItemClickListener(this);

        liveCaptureBtn.setOnClickListener(this);
        full.setOnClickListener(this);
        bt_commit.setOnClickListener(this);
    }

    private void initData() {
        getProblemCheck();

        Intent intent = getIntent();
        mCameraID = intent.getStringExtra(Constant.Hik.CAMERA_ID);
        camera_name = intent.getStringExtra("camera_name");
        mTopBar.setTitle(camera_name);

        LiveModel.getLiveModel().play(mCameraID, this, mSurfaceView, writView);
    }

    /**
     * 判断是否为工地管理者
     */
    private boolean isSiteManager() {
        User user = mApplication.getUser();
        if (user != null) {
            List<String> rolesList = user.getRoles();
            for (String role : rolesList) {
                if (ROLE_SITE_MANAGER.equalsIgnoreCase(role))
                    return true;
            }
        }
        return false;
    }

    /**
     * 获取巡查问题
     *
     * @return
     */
    private void getProblemCheck() {
        problemList = mApplication.getProblemList();
        if (problemList != null) {
            initProblemView(problemList);
        } else {
            OkHttpUtils.post().url(Constant.BASE_URL + Constant.URL_GETCAUSES)
                    .addParams("userName", mApplication.getUser().getUserNo())
                    .addParams("token", mApplication.getUser().getToken())
                    .build().execute(new StringCallback() {

                @Override
                public void onResponse(String response, int arg1) {
                    LogUtil.i(response);
                    if (!TextUtils.isEmpty(response)) {
                        CheckProblem checkProblem = GsonTools
                                .changeGsonToBean(response,
                                        CheckProblem.class);
                        if (checkProblem.success) {
                            problemList = checkProblem.data;
                            mApplication.setProblemList(problemList);
                            initProblemView(problemList);
                        }

                    }
                }

                @Override
                public void onError(Call arg0, Exception arg1, int arg2) {
                    showToast("获取数据失败");
                }
            });
        }
    }

    private void initProblemView(ArrayList<ProblemItem> problemList) {
        LayoutInflater mLayoutInflater = getLayoutInflater();
        for (final ProblemItem tag : problemList) {
            final TextView tagView;
            tagView = (TextView) mLayoutInflater.inflate(R.layout.tag,
                    tagContainer, false);
            tagView.setText(tag.Item);
            tagView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (problemCheckList.contains(tag.Item)) {
                        problemCheckList.remove(tag.Item);
                        tagView.setTextColor(mContext.getResources().getColor(
                                R.color.tag_font_unselect));
                        tagView.setBackgroundDrawable(mContext.getResources()
                                .getDrawable(R.drawable.shape_tag));
                    } else {
                        problemCheckList.add(tag.Item);
                        tagView.setTextColor(mContext.getResources().getColor(
                                R.color.white));
                        tagView.setBackgroundColor(mContext.getResources()
                                .getColor(R.color.tag_select));
                    }
                }
            });
            tagContainer.addView(tagView);
        }
    }

    private void initTitleBar() {
        mTopBar.setBackVisibility(true);
        mTopBar.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                CameraDetailActivity.this.finish();
            }
        });
        mTopBar.setTitle("大门");
        mTopBar.setActionTextVisibility(false);
    }

    /**
     * 抓拍 void
     *
     * @since V1.0
     */
    private void captureBtnOnClick() {

        if (imagePath.size() >= 5) {
            return;
        }

        String path = LiveModel.getLiveModel().capture();

        if (path != null) {
            imagePath.add(path);
            imageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.liveCaptureBtn:
                captureBtnOnClick();
                break;
            case R.id.bt_commit:
                commitReport();
                break;
            case R.id.full:
                if (ifFull) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    ifFull = !ifFull;
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    ifFull = !ifFull;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            surfaceViewLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            this.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mTopBar.setVisibility(View.GONE);
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            surfaceViewLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, CommonUtil.dip2px(
                    mContext, 199)));
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mTopBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 提交报告
     */
    private void commitReport() {
        ChoiceDialogFra cFra = new ChoiceDialogFra();
        cFra.show(getSupportFragmentManager(), "CHOICE");

        ReportUtils.getReportUtils().setProblemCheckList(problemCheckList);
        ReportUtils.getReportUtils().setImageList(imagePath);

    }

    @Override
    public void onBackPressed() {
        if (ifFull) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            ifFull = !ifFull;
        } else {
            this.finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {
        if (position == imagePath.size()) {
            new ActionSheetDialog(CameraDetailActivity.this)
                    .builder()
                    .setCancelable(false)
                    .setCanceledOnTouchOutside(false)
                    .addSheetItem("拍照", SheetItemColor.Blue,
                            new OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    photo();
                                }
                            })
                    .addSheetItem("从相册中选取", SheetItemColor.Blue,
                            new OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    Intent intent = new Intent(mContext,
                                            SelectImageActivity.class);
                                    intent.putExtra(
                                            SelectImageActivity.SELECTCOUNT,
                                            imagePath.size());
                                    startActivityForResult(intent,
                                            SELECT_PICTURE);
                                }
                            }).show();
        } else {
            Intent intent = new Intent(mContext, ImagePagerActivity.class);
            intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS,
                    (String[]) imagePath.toArray(new String[imagePath.size()]));
            intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
            intent.putExtra(ImagePagerActivity.FROMNET, false);
            intent.putExtra(ImagePagerActivity.NO_PARENTPATH, false);
            startActivity(intent);
        }
    }

    private static final int TAKE_PICTURE = 0x000000;
    private static final int SELECT_PICTURE = 0x000001;
    private String path = "";

    public void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(
                UtilFilePath.getPictureDirPath().getAbsolutePath(),
                String.valueOf(System.currentTimeMillis()) + ".jpg");
        path = file.getPath();
        Uri imageUri = Uri.fromFile(file);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (imagePath.size() < 6 && resultCode == -1) {
                    imagePath.add(path);
                    imageAdapter.notifyDataSetChanged();
                }
                break;
            case SELECT_PICTURE:
                if (data != null) {
                    ArrayList<String> mSelectedImage = data
                            .getStringArrayListExtra("picdata");
                    imagePath.addAll(mSelectedImage);
                    imageAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

}
