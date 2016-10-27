package com.sundyn.bluesky.fragment;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import org.xutils.x;
import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.sundyn.bluesky.AllSiteMapActivity;
import com.sundyn.bluesky.CheckCountActivity;
import com.sundyn.bluesky.CountCheckActivity;
import com.sundyn.bluesky.DataCenterActivity;
import com.sundyn.bluesky.EnforcementNowActivity;
import com.sundyn.bluesky.MapDisplayActivity;
import com.sundyn.bluesky.MySiteActivity;
import com.sundyn.bluesky.ProblemQueryActivity;
import com.sundyn.bluesky.R;
import com.sundyn.bluesky.VideoCheckActivity;
import com.sundyn.bluesky.base.BaseFragment;
import com.sundyn.bluesky.baseadapterhelper.BaseAdapterHelper;
import com.sundyn.bluesky.baseadapterhelper.QuickAdapter;
import com.sundyn.bluesky.bean.AuthModle;
import com.sundyn.bluesky.bean.AuthModle.ModleItem;
import com.sundyn.bluesky.bean.WorkBenchItem;
import com.sundyn.bluesky.utils.CommonUtil;
import com.sundyn.bluesky.utils.Constant;
import com.sundyn.bluesky.utils.GsonTools;
import com.sundyn.bluesky.view.NormalTopBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

public class WorkBenchFra extends BaseFragment implements OnItemClickListener {
	private View view;

	private NormalTopBar mTopBar;
	@ViewInject(R.id.loading_view)
	protected View loadingView;
	@ViewInject(R.id.gv_workbench)
	private GridView gv_workbench;

	private ArrayList<WorkBenchItem> workBeanchItemList;
	private QuickAdapter<WorkBenchItem> workBeanchItemAdapter;
	private List<ModleItem> dataModle;// 包含权限模块的集合
	public static final int MODEL_VIDEO = 1;
	private static final int MODEL_ALLSITEMAP = 2;
	private static final int MODEL_DATACENTER = 3;
	private static final int MODEL_COUNTCHECK = 4;
	private static final int MODEL_ENFORCEMENTNOW = 5;
	private static final int MODEL_PROBLEMQUERY = 6;
	private static final int MODEL_MAPDISPLAY = 7;
	private static final int MODEL_MYSITE = 8;
	private static final int MODEL_CHECKCOUNT = 9;
	public static final int MODEL_SITETBJ = 10;

	@Override
	public View initView(LayoutInflater inflater) {
		view = inflater.inflate(R.layout.fra_workbench, null);
		x.view().inject(this, view); // 注入view和事件
		mTopBar = (NormalTopBar) view.findViewById(R.id.chat_top_bar);
		mTopBar.setBackVisibility(false);
		mTopBar.setTitle("工作台");
		CommonUtil.setLayoutAnimationController(gv_workbench, mContext);
		initEvent();
		return view;
	}

	@Override
	public void initData(Bundle savedInstanceState) {
		workBeanchItemList = new ArrayList<WorkBenchItem>();
		dataModle = new ArrayList<ModleItem>();
		workBeanchItemAdapter = new QuickAdapter<WorkBenchItem>(mContext,
				R.layout.gv_workbenchfra_item) {
			@Override
			protected void convert(BaseAdapterHelper helper, WorkBenchItem item) {
				helper.setText(R.id.item_name, item.getName());
				helper.setImageResource(R.id.item_image, item.getImageID());
			}
		};
		gv_workbench.setAdapter(workBeanchItemAdapter);

		List<ModleItem> controlModleData = mApplication.getControlModleData();
		if (controlModleData != null) {
			initModle(controlModleData);
		} else {
			OkHttpUtils.post().url(Constant.BASE_URL + Constant.URL_GETMODEL)
					.addParams("userName", mApplication.getUser().getUserNo())
					.addParams("token", mApplication.getUser().getToken())
					.addParams("roles", mApplication.getUser().getRolestring())
					.build().execute(new StringCallback() {

						@Override
						public void onResponse(String response, int arg1) {
							LogUtil.i(response);
							if (!TextUtils.isEmpty(response)) {
								AuthModle authModle = GsonTools
										.changeGsonToBean(response,
												AuthModle.class);
								if (authModle.success) {
									dataModle = authModle.data;
									/* 测试数据start */
									/* 测试数据end */
									initModle(dataModle);
									mApplication.setControlModleData(dataModle);
								} else {
									showToast(authModle.message);
									dismissLoadingView();
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

	/* 初始化权限模块 */
	protected void initModle(List<ModleItem> dataModle) {
		for (ModleItem modleItem : dataModle) {
			createModel(modleItem);
		}
		workBeanchItemAdapter.addAll(workBeanchItemList);
		dismissLoadingView();
	}

	private void createModel(ModleItem modleItem) {
		switch (modleItem.id) {
		case MODEL_VIDEO:
			WorkBenchItem vadioCheck = new WorkBenchItem(
					R.drawable.workbench_vadiocheck, "视频中心", MODEL_VIDEO);
			workBeanchItemList.add(vadioCheck);
			break;
		case MODEL_ALLSITEMAP:
			/*
			 * WorkBenchItem allSiteMap = new WorkBenchItem(
			 * R.drawable.workbench_map, "全部工地", MODEL_ALLSITEMAP);
			 * workBeanchItemList.add(allSiteMap);
			 */
			break;
		case MODEL_DATACENTER:
			WorkBenchItem dataCenter = new WorkBenchItem(
					R.drawable.workbench_datacenter, "数据中心", MODEL_DATACENTER);
			workBeanchItemList.add(dataCenter);
			break;
		case MODEL_COUNTCHECK:
			WorkBenchItem countCheck = new WorkBenchItem(
					R.drawable.workbench_countcheck, "统计查询", MODEL_COUNTCHECK);
			workBeanchItemList.add(countCheck);
			break;
		case MODEL_ENFORCEMENTNOW:

			WorkBenchItem enforcementNow = new WorkBenchItem(
					R.drawable.workbench_enforcement, "现场执法",
					MODEL_ENFORCEMENTNOW);
			workBeanchItemList.add(enforcementNow);
			break;
		case MODEL_PROBLEMQUERY:
			WorkBenchItem problemQuery = new WorkBenchItem(
					R.drawable.workbench_problemquery, "故障查询",
					MODEL_PROBLEMQUERY);
			workBeanchItemList.add(problemQuery);
			break;
		case MODEL_MAPDISPLAY:
			WorkBenchItem mapDisplay = new WorkBenchItem(
					R.drawable.workbench_map, "地图展示", MODEL_MAPDISPLAY);
			workBeanchItemList.add(mapDisplay);
			break;
		case MODEL_MYSITE:
			WorkBenchItem mySite = new WorkBenchItem(
					R.drawable.workbench_datacenter, "我的工地", MODEL_MYSITE);
			workBeanchItemList.add(mySite);
			break;

		case MODEL_CHECKCOUNT:
			WorkBenchItem checkCount = new WorkBenchItem(
					R.drawable.workbench_checkcount, "巡查汇总", MODEL_CHECKCOUNT);
			workBeanchItemList.add(checkCount);
			break;
		case MODEL_SITETBJ:
			WorkBenchItem siteTbj = new WorkBenchItem(
					R.drawable.workbench_checkcount, "工地维护", MODEL_SITETBJ);
			workBeanchItemList.add(siteTbj);
			break;

		default:
			break;
		}
	}

	private void initEvent() {
		gv_workbench.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		switch (workBeanchItemList.get(position).getModleID()) {
		case MODEL_VIDEO:
		case MODEL_SITETBJ:
			Intent intent = new Intent(mContext, VideoCheckActivity.class);
			intent.putExtra(VideoCheckActivity.MODELID,
					workBeanchItemList.get(position).getModleID());
			startActivity(intent);
			break;
		case MODEL_ALLSITEMAP:
			Intent allSiteMap = new Intent(mContext, AllSiteMapActivity.class);
			startActivity(allSiteMap);
			break;
		case MODEL_DATACENTER:
			Intent dataCentertIntent = new Intent(mContext,
					DataCenterActivity.class);
			startActivity(dataCentertIntent);
			break;
		case MODEL_COUNTCHECK:
			Intent countCheckIntent = new Intent(mContext,
					CountCheckActivity.class);
			startActivity(countCheckIntent);
			break;
		case MODEL_ENFORCEMENTNOW:
			Intent enforcementNowIntent = new Intent(mContext,
					EnforcementNowActivity.class);
			startActivity(enforcementNowIntent);
			break;
		case MODEL_PROBLEMQUERY:
			Intent problemQueryIntent = new Intent(mContext,
					ProblemQueryActivity.class);
			startActivity(problemQueryIntent);
			break;
		case MODEL_MAPDISPLAY:
			Intent mapDisplayIntent = new Intent(mContext,
					MapDisplayActivity.class);
			startActivity(mapDisplayIntent);
			break;
		case MODEL_MYSITE:
			Intent mySiteIntent = new Intent(mContext, MySiteActivity.class);
			startActivity(mySiteIntent);
			break;
		case MODEL_CHECKCOUNT:
			Intent checkCountIntent = new Intent(mContext,
					CheckCountActivity.class);
			startActivity(checkCountIntent);
			break;
		default:
			break;
		}
	}

	public void dismissLoadingView() {
		if (loadingView != null)
			loadingView.setVisibility(View.INVISIBLE);
	}
}
