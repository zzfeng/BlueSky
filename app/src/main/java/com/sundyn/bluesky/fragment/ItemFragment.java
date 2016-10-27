package com.sundyn.bluesky.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.activity.CameraListActivity;
import com.sundyn.bluesky.base.BaseFragment;
import com.sundyn.bluesky.baseadapterhelper.BaseAdapterHelper;
import com.sundyn.bluesky.baseadapterhelper.QuickAdapter;
import com.sundyn.bluesky.bean.SiteBean;
import com.sundyn.bluesky.bean.SiteBean.Site;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.utils.GsonTools;
import com.sundyn.bluesky.utils.ReportUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import okhttp3.Call;

public class ItemFragment extends BaseFragment implements OnItemClickListener {

    private View view;
    private int areaCode;

    @ViewInject(R.id.lv_item_news)
    private ListView lv_item_news;
    @ViewInject(R.id.loading_view)
    protected View loadingView;
    private ArrayList<Site> siteList;
    private QuickAdapter<Site> siteAdapter;

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_item, null);
        x.view().inject(this, view); // 注入view和事件

        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        siteList = new ArrayList<Site>();
        Bundle mBundle = getArguments();
        areaCode = mBundle.getInt("areaCode");

        siteAdapter = new QuickAdapter<Site>(mContext,
                R.layout.lv_videocheck_item) {

            @Override
            protected void convert(BaseAdapterHelper helper, Site item) {
                helper.setText(R.id.tv_video_item, item.bsiteName);

            }
        };
        lv_item_news.setAdapter(siteAdapter);

        OkHttpUtils.post().url(Constant.BASE_URL + Constant.URL_GETSITEBYAREAID)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken())
                .addParams("areaId", areaCode + "")
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onResponse(String response, int arg1) {
                        LogUtil.i(response);
                        if (!TextUtils.isEmpty(response)) {
                            SiteBean siteBean = GsonTools.changeGsonToBean(response, SiteBean.class);
                            if (siteBean.success) {
                                siteList = siteBean.data;
                            } else {
                                showToast(siteBean.message);
                            }
                            siteAdapter.addAll(siteList);
                            dismissLoadingView();
                        }
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        showToast("获取数据失败");
                        dismissLoadingView();
                    }
                });
        lv_item_news.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Intent intent = new Intent(mContext, CameraListActivity.class);
        Site site = siteList.get(position);

		/* 用于提交报告时的参数*/
        ReportUtils.getReportUtils().clear();
        ReportUtils.getReportUtils().setBsiteName(site.bsiteName);
        ReportUtils.getReportUtils().setBsiteNo(site.bsiteNo);

        String bsiteNo = site.bsiteNo;
        intent.putExtra("bsiteNo", bsiteNo);
        startActivity(intent);
    }

    public void dismissLoadingView() {
        if (loadingView != null)
            loadingView.setVisibility(View.INVISIBLE);
    }
}
