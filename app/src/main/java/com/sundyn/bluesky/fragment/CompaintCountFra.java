package com.sundyn.bluesky.fragment;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.activity.CheckCountActivity;
import com.sundyn.bluesky.base.BaseFragment;
import com.sundyn.bluesky.baseadapterhelper.BaseAdapterHelper;
import com.sundyn.bluesky.baseadapterhelper.QuickAdapter;
import com.sundyn.bluesky.bean.AreaOfOld;
import com.sundyn.bluesky.bean.AreaOfOld.Region;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.utils.GsonTools;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Random;

import okhttp3.Call;

/**
 * @author yangjl
 * @date 2016-8-9下午5:13:30
 * @版本：1.0
 * @描述：
 */
public class CompaintCountFra extends BaseFragment implements
        OnItemClickListener {

    private View view;
    @ViewInject(R.id.tag_container)
    private GridView mGridView;
    @ViewInject(R.id.id_bt_countcity)
    private Button bt_countcity;

    private ArrayList<Region> allAreaList;
    private QuickAdapter<Region> mAdapter;
    private Random random = new Random();
    private final int[] colors = {R.color.bg_area_1, R.color.bg_area_2,
            R.color.bg_area_3, R.color.bg_area_4, R.color.bg_area_5,
            R.color.bg_area_6, R.color.bg_area_7, R.color.bg_area_8,
            R.color.bg_area_9, R.color.bg_area_10, R.color.bg_area_11};
    private String cityCode;

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fra_compaintcount, null);
        x.view().inject(this, view); // 注入view和事件
        mGridView.setOnItemClickListener(this);
        bt_countcity.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentActivity activity = CompaintCountFra.this.getActivity();
                if (activity != null) {
                    ((CheckCountActivity) activity).go2CompaintSum(cityCode);
                }
            }
        });
        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mAdapter = new QuickAdapter<Region>(mContext,
                R.layout.gv_compaint_item) {

            @Override
            protected void convert(BaseAdapterHelper helper, Region item) {
                TextView tagView = helper.getView(R.id.id_area);
                helper.setText(R.id.id_area, item.argName);

                int index_color = random.nextInt(colors.length);
                GradientDrawable myGrad = (GradientDrawable) tagView
                        .getBackground();
                myGrad.setColor(getResources().getColor(colors[index_color]));
            }
        };
        mGridView.setAdapter(mAdapter);
        if (allAreaList != null) {
            mAdapter.clear();
            mAdapter.addAll(allAreaList);
        } else {
            getAllArea();
        }
    }

    private void getAllArea() {
        showDialog();
        OkHttpUtils.post()
                .url(Constant.BASE_URL + Constant.URL_SELECTTERRITORIALNOZZ)
                .addParams("userName", mApplication.getUser().getUserNo())
                .addParams("token", mApplication.getUser().getToken()).build()
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(String response, int arg1) {
                        if (!TextUtils.isEmpty(response)) {
                            AreaOfOld allArea = GsonTools.changeGsonToBean(
                                    response, AreaOfOld.class);
                            allAreaList = allArea.data;
                            mAdapter.clear();
                            mAdapter.addAll(allAreaList);
                            disDIalog();
                        }
                    }

                    @Override
                    public void onError(Call arg0, Exception arg1, int arg2) {
                        showToast("获取数据失败");
                        disDIalog();
                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {
        FragmentActivity activity = CompaintCountFra.this.getActivity();
        if (activity != null) {
            ((CheckCountActivity) activity).go2CompaintSum(allAreaList
                    .get(position).argCode);
        }
    }

}
