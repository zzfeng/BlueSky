package com.sundyn.bluesky.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseActivity;
import com.sundyn.bluesky.baseadapterhelper.CameraImageAdapter;
import com.sundyn.bluesky.bean.CheckProblem;
import com.sundyn.bluesky.bean.CheckProblem.ProblemItem;
import com.sundyn.bluesky.bean.GovernmentBean.Government;
import com.sundyn.bluesky.hik.util.UtilFilePath;
import com.sundyn.bluesky.utils.CommonRq;
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

import okhttp3.Call;

/**
 * @author yangjl 描述：现场执法activity 2016-6-21下午5:09:45
 */
public class EnforcementNowActivity extends BaseActivity implements
        OnItemClickListener, OnClickListener {
    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.tag_container)
    private FlowLayout tagContainer;// 巡查问题
    @ViewInject(R.id.imageGridView)
    private GridView imageGridView;
    @ViewInject(R.id.id_iv_search)
    private TextView iv_search;
    @ViewInject(R.id.id_tv_area)
    private TextView tv_area;
    @ViewInject(R.id.bt_commit)
    private Button bt_commit;
    @ViewInject(R.id.id_et_prjname)
    private EditText et_prjname;
    @ViewInject(R.id.loading_view)
    protected View loadingView;

    private ArrayList<ProblemItem> problemList;// 巡查问题集合
    private ArrayList<String> problemCheckList = new ArrayList<String>();// 选择的检查问题
    private CameraImageAdapter imageAdapter;
    private ArrayList<String> imagePath = new ArrayList<String>();// 截图所存集合

    private CommonRq mConRq;// 共同请求体

    private int prjno;// 工地id
    private String prjname;// 工地名称

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_enforcementnow);
        x.view().inject(this);
        initTitleBar();
        initEvent();
        initData();
    }

    private void initEvent() {
        tv_area.setOnClickListener(this);
        imageGridView.setOnItemClickListener(this);
        iv_search.setOnClickListener(this);
        bt_commit.setOnClickListener(this);
    }

    private void initData() {
        getProblemCheck();
        imageAdapter = new CameraImageAdapter(mContext, imagePath);
        imageGridView.setAdapter(imageAdapter);
    }

    private void initTitleBar() {
        mTopBar.setBackVisibility(true);
        mTopBar.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                EnforcementNowActivity.this.finish();
            }
        });
        mTopBar.setTitle("现场执法");
        mTopBar.setActionTextVisibility(false);
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
            OkHttpUtils
                    .post()
                    .url(Constant.BASE_URL + Constant.URL_GETCAUSES)
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
        if (loadingView != null)
            loadingView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {
        if (position == imagePath.size()) {
            new ActionSheetDialog(EnforcementNowActivity.this)
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
    private static final int SELECT_PRJ = 0x000002;

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
                if (imagePath.size() < 9 && resultCode == -1) {
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
            case SELECT_PRJ:
                if (data != null) {
                    prjname = data.getStringExtra("prjname");
                    prjno = data.getIntExtra("prjno", 0);
                    et_prjname.setText(prjname);
                }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_iv_search:
                Intent intent = new Intent(mContext, AllProjectActivity.class);
                startActivityForResult(intent, SELECT_PRJ);
                break;
            case R.id.id_tv_area:
                if (mConRq == null) {
                    mConRq = new CommonRq(mContext, mApplication);
                }
                mConRq.showSelectGovernment(new CommonRq.RqCallBack<Government>() {
                    @Override
                    public void initSelectData(Government data) {
                        tv_area.setText(data.getPickerViewText());
                        // territorial = data.Territorial;
                    }
                });
                break;
            case R.id.bt_commit:
                commitReport();
            default:
                break;
        }
    }

    /**
     * 提交报告
     */
    private void commitReport() {
        if (TextUtils.isEmpty(et_prjname.getText().toString())) {
            showToast("请输入工地名称!");
            return;
        }

        ReportUtils.getReportUtils().clear();
        ReportUtils.getReportUtils().setProblemCheckList(problemCheckList);
        ReportUtils.getReportUtils().setImageList(imagePath);
        ReportUtils.getReportUtils().setBsiteName(prjname);
        ReportUtils.getReportUtils().setBsiteNo(prjno + "");

        ChoiceDialogFra cFra = new ChoiceDialogFra();
        cFra.show(getSupportFragmentManager(), "CHOICE");
    }

}
