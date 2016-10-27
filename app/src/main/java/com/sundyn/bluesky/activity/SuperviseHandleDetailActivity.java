package com.sundyn.bluesky.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseRoleActivity;
import com.sundyn.bluesky.baseadapterhelper.BaseAdapterHelper;
import com.sundyn.bluesky.baseadapterhelper.QuickAdapter;
import com.sundyn.bluesky.bean.SuperviseDetail;
import com.sundyn.bluesky.utils.CommonUtil;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.utils.GsonTools;
import com.sundyn.bluesky.view.FormDetailView;
import com.sundyn.bluesky.view.NormalTopBar;
import com.sundyn.bluesky.view.RebackDialogFra.RebackInputListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class SuperviseHandleDetailActivity extends BaseRoleActivity implements
        RebackInputListener {

    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.id_tv_noticeno)
    private FormDetailView tv_noticeno;
    @ViewInject(R.id.id_tv_publishdate)
    private FormDetailView tv_publishdate;
    @ViewInject(R.id.id_tv_rectificationdate)
    private FormDetailView tv_rectificationdate;
    @ViewInject(R.id.id_tv_prjname)
    private FormDetailView tv_prjname;
    @ViewInject(R.id.id_tv_territorial)
    private FormDetailView tv_territorial;
    @ViewInject(R.id.id_tv_problem)
    private TextView tv_problem;
    private QuickAdapter<SuperviseDetail.ImageInfo> mImageAdapter;

    private List<SuperviseDetail.ImageInfo> imageList;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_supervisehandle);
        x.view().inject(this);
        initTitleBar();
        type = NOTICE;
        initView();
        initData();
    }

    private void initData() {
        id = getIntent().getIntExtra(NOTICEID, 0);
        mImageAdapter = new QuickAdapter<SuperviseDetail.ImageInfo>(mContext,
                R.layout.grid_item) {

            @Override
            protected void convert(BaseAdapterHelper helper,
                                   final SuperviseDetail.ImageInfo item) {
                helper.setImageBuilder(
                        R.id.id_item_image,
                        Picasso.with(mContext)
                                .load(item.imgurl)
                                .placeholder(R.mipmap.pictures_no)
                                .error(R.mipmap.pictures_no)
                                .resize(CommonUtil.dip2px(mContext, 60),
                                        CommonUtil.dip2px(mContext, 60))
                                .centerCrop());
                helper.getView(R.id.id_item_image).setOnClickListener(
                        new OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(mContext,
                                        ImagePagerActivity.class);

                                List<String> mImgs = new ArrayList<String>();
                                for (SuperviseDetail.ImageInfo info : imageList) {
                                    mImgs.add(info.imgurl);
                                }
                                intent.putExtra(
                                        ImagePagerActivity.EXTRA_IMAGE_URLS,
                                        (String[]) mImgs
                                                .toArray(new String[mImgs
                                                        .size()]));
                                intent.putExtra(
                                        ImagePagerActivity.EXTRA_IMAGE_INDEX,
                                        imageList.indexOf(item));
                                intent.putExtra(ImagePagerActivity.FROMNET,
                                        true);
                                intent.putExtra(
                                        ImagePagerActivity.NO_PARENTPATH, false);
                                startActivity(intent);
                            }
                        });
            }
        };
        imageGridView.setAdapter(mImageAdapter);

        OkHttpUtils.get().url(Constant.BASE_URL + Constant.URL_DBCBONEINFO)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("id", id + "").build().execute(new StringCallback() {
            @Override
            public void onResponse(String response, int arg1) {
                if (!TextUtils.isEmpty(response)) {
                    processData(response);
                }
            }

            @Override
            public void onError(Call arg0, Exception arg1, int arg2) {
                showToast("加载失败，请检查服务器连接");
            }
        });
    }

    protected void processData(String response) {
        SuperviseDetail superviseDetail = GsonTools.changeGsonToBean(response,
                SuperviseDetail.class);
        if (superviseDetail.success) {
            imageList = superviseDetail.imginfo;
            if (imageList.size() > 0) {
                ll_img.setVisibility(View.VISIBLE);
                mImageAdapter.addAll(imageList);
            }
            if ("是".endsWith(superviseDetail.zgtzinfo.isBack)) {
                tv_back.setVisibility(View.GONE);
                ll_back.setVisibility(View.VISIBLE);
                mWebView.loadData(superviseDetail.zgtzinfo.ReContent,
                        "text/html; charset=UTF-8", null);

            }
            tv_noticeno.updateText(superviseDetail.zgtzinfo.NoticeNo);
            tv_prjname.updateText(superviseDetail.zgtzinfo.PrjName);
            tv_problem.setText(superviseDetail.zgtzinfo.Item);
            tv_publishdate.updateText(superviseDetail.zgtzinfo.PublishDate);
            tv_rectificationdate
                    .updateText(superviseDetail.zgtzinfo.RectificationDate);
            tv_territorial.updateText(superviseDetail.zgtzinfo.Territorial);

            statusNo = superviseDetail.zgtzinfo.ApproveStatus;
            isReturn = superviseDetail.zgtzinfo.IsReturn;
            returnResult = superviseDetail.zgtzinfo.ReturnResult;
            initStautusButton(statusNo);
        } else {
            showToast("获取整改通知详情失败!");
        }
    }

    private void initTitleBar() {
        mTopBar.setBackVisibility(true);
        mTopBar.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SuperviseHandleDetailActivity.this.finish();
            }
        });
        mTopBar.setTitle("督办详情");
        mTopBar.setActionTextVisibility(false);
    }

    @Override
    public void onComplete(String content, String tag) {
        if (REBACK.equals(tag)) {
            reply(content, NOTICE);
        } else if (REPLYRETURNRESULT.equals(tag)) {
            replyReturnResult(content, NOTICE);
        }
    }

}
