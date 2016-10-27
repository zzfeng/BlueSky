package com.sundyn.bluesky.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.squareup.picasso.Picasso;
import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseRoleActivity;
import com.sundyn.bluesky.baseadapterhelper.BaseAdapterHelper;
import com.sundyn.bluesky.baseadapterhelper.QuickAdapter;
import com.sundyn.bluesky.bean.ReformDetail;
import com.sundyn.bluesky.bean.ReformDetail.ImageInfo;
import com.sundyn.bluesky.bean.ReformDetail.ItemInfo;
import com.sundyn.bluesky.utils.CommonUtil;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.utils.GsonTools;
import com.sundyn.bluesky.view.FormDetailView;
import com.sundyn.bluesky.view.LinearListView;
import com.sundyn.bluesky.view.NormalTopBar;
import com.sundyn.bluesky.view.RebackDialogFra.RebackInputListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class ReformNoticeDetailActivity extends BaseRoleActivity implements
        RebackInputListener {

    @ViewInject(R.id.chat_top_bar)
    private NormalTopBar mTopBar;
    @ViewInject(R.id.problemView)
    private LinearListView problemView;
    @ViewInject(R.id.id_tv_approveStatus)
    private FormDetailView tv_approveStatus;// 发布格式
    @ViewInject(R.id.id_tv_noticeNo)
    private FormDetailView tv_noticeNo;//
    @ViewInject(R.id.id_tv_governmentname)
    private FormDetailView tv_governmentname;
    @ViewInject(R.id.id_tv_rectificationdate)
    private FormDetailView tv_rectificationdate;
    @ViewInject(R.id.id_tv_checkteam)
    private FormDetailView tv_checkteam;
    @ViewInject(R.id.id_tv_prjname)
    private FormDetailView tv_prjname;
    @ViewInject(R.id.id_tv_publishdate)
    private FormDetailView tv_publishdate;

    private QuickAdapter<ItemInfo> mProblemAdapter;
    private QuickAdapter<ImageInfo> mImageAdapter;

    private List<ImageInfo> imageList;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_reformnoticedetail);
        x.view().inject(this);
        initTitleBar();
        type = ZGTZ;

        initView();

        initData();
    }

    private void initData() {
        id = getIntent().getIntExtra(NOTICEID, 0);
        mProblemAdapter = new QuickAdapter<ItemInfo>(mContext,
                R.layout.lv_reformproblem_item) {

            @Override
            protected void convert(BaseAdapterHelper helper, ItemInfo item) {
                helper.setText(R.id.tv_reform, item.Item);
            }
        };
        problemView.setAdapter(mProblemAdapter);

        mImageAdapter = new QuickAdapter<ImageInfo>(mContext,
                R.layout.grid_item) {

            @Override
            protected void convert(BaseAdapterHelper helper,
                                   final ImageInfo item) {
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
                                for (ImageInfo info : imageList) {
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

        OkHttpUtils.get().url(Constant.BASE_URL + Constant.URL_ZGTZONEINFO)
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
        ReformDetail reformDetail = GsonTools.changeGsonToBean(response,
                ReformDetail.class);
        if (reformDetail.success) {
            List<ItemInfo> problemList = reformDetail.iteminfo;
            mProblemAdapter.addAll(problemList);

            imageList = reformDetail.imginfo;
            if (imageList.size() > 0) {
                ll_img.setVisibility(View.VISIBLE);
                mImageAdapter.addAll(imageList);
            }
            if ("是".endsWith(reformDetail.zgtzinfo.isBack)) {
                tv_back.setVisibility(View.GONE);
                ll_back.setVisibility(View.VISIBLE);
                mWebView.loadData(reformDetail.zgtzinfo.ReContent,
                        "text/html; charset=UTF-8", null);

            }
            tv_approveStatus.updateText(reformDetail.zgtzinfo.itype);
            tv_checkteam.updateText(reformDetail.zgtzinfo.CheckTeam);
            tv_governmentname.updateText(reformDetail.zgtzinfo.GovernmentName);
            tv_noticeNo.updateText(reformDetail.zgtzinfo.NoticeNo);
            tv_prjname.updateText(reformDetail.zgtzinfo.PrjName);
            tv_rectificationdate
                    .updateText(reformDetail.zgtzinfo.RectificationDate);
            tv_publishdate.updateText(reformDetail.zgtzinfo.PublishDate);

            statusNo = reformDetail.zgtzinfo.ApproveStatus;
            isReturn = Boolean.valueOf(reformDetail.zgtzinfo.IsReturn);
            returnResult = reformDetail.zgtzinfo.ReturnResult;
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
                ReformNoticeDetailActivity.this.finish();
            }
        });
        mTopBar.setTitle("整改详情");
        mTopBar.setActionTextVisibility(false);
    }

    @Override
    public void onComplete(String content, String tag) {
        if (REBACK.equals(tag)) {
            reply(content, ZGTZ);
        } else if (REPLYRETURNRESULT.equals(tag)) {
            replyReturnResult(content, ZGTZ);
        }
    }

}
