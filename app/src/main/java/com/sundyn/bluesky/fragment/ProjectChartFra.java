package com.sundyn.bluesky.fragment;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.sundyn.bluesky.R;
import com.sundyn.bluesky.base.BaseFragment;
import com.sundyn.bluesky.bean.ProjectChart;
import com.sundyn.bluesky.bean.ProjectChart.BarChartItem;
import com.sundyn.bluesky.utils.GsonTools;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

public class ProjectChartFra extends BaseFragment {

	private View view;
	@ViewInject(R.id.id_barchart)
	private BarChart mBarChart;

	public ArrayList<BarEntry> entries = new ArrayList<BarEntry>(); // (x,y1)
	public ArrayList<BarEntry> entries2 = new ArrayList<BarEntry>();// (x,y2)
	public ArrayList<BarEntry> entries3 = new ArrayList<BarEntry>();// (x,y3)
	public BarDataSet dataset, dataset2, dataset3;

	private XAxis xAxis; // X坐标轴
	private YAxis yAxis; // Y
	private ArrayList<String> xVals;// x坐标数据

	@Override
	public View initView(LayoutInflater inflater) {
		view = inflater.inflate(R.layout.fra_projectchar, null);
		x.view().inject(this, view); // 注入view和事件
		return view;
	}

	@Override
	public void initData(Bundle savedInstanceState) {
		xAxis = mBarChart.getXAxis();
		xAxis.setPosition(XAxisPosition.BOTTOM);// X轴在下边
		yAxis = mBarChart.getAxisLeft();
		xAxis.setDrawGridLines(false);// 不要竖线网格
		yAxis.setStartAtZero(true);
		mBarChart.getAxisRight().setEnabled(false); // 隐藏右边的坐标轴
		xAxis.setAvoidFirstLastClipping(false);

		xVals = new ArrayList<String>();

		mBarChart.zoom(3.0f, 1.0f, 0f, 0f);// 放大倍数,要不然数据密集

		getCountData();
	}

	/* 初始化数据 */
	public void initEntriesData(List<BarChartItem> chartData) {
		for (int i = 0; i < chartData.size(); i++) {
			entries.add(new BarEntry(chartData.get(i).PrjCount, i));
			entries2.add(new BarEntry(chartData.get(i).sArea, i));
			entries3.add(new BarEntry(chartData.get(i).sMoney, i));
			xVals.add(chartData.get(i).argName);
		}
		showBarChart();
	}

	public void showBarChart() {
		dataset = new BarDataSet(entries, "项目数量");
		dataset.setColor(Color.rgb(255, 48, 48));
		dataset2 = new BarDataSet(entries2, "面积");
		dataset2.setColor(Color.rgb(0, 191, 255));
		dataset3 = new BarDataSet(entries3, "造价");
		dataset3.setColor(Color.rgb(255, 215, 0));

		ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>(); // 坐标线的集合。
		dataSets.add(dataset);
		dataSets.add(dataset2);
		dataSets.add(dataset3);

		BarData data3 = new BarData(xVals, dataSets);
		mBarChart.setData(data3);
		mBarChart.animateY(2000);// 动画效果 y轴方向，2秒
		mBarChart.setDescription("");
	}

	private void getCountData() {
		showDialog();

		OkHttpUtils.get()
				.url(Constant.BASE_URL + Constant.URL_SELECTPRJAREAAMTSUMMARY)
				.addParams("userName", mApplication.getUser().getUserNo())
				.addParams("token", mApplication.getUser().getToken()).build()
				.execute(new StringCallback() {
					@Override
					public void onResponse(String response, int arg1) {
						ProjectChart mProjectChart = GsonTools
								.changeGsonToBean(response, ProjectChart.class);
						if (mProjectChart.success) {
							List<BarChartItem> chartData = mProjectChart.data;
							initEntriesData(chartData);
						} else {
							showToast(mProjectChart.message);
						}
						disDIalog();

					}

					@Override
					public void onError(Call arg0, Exception arg1, int arg2) {
						showToast("获取数据失败");
						disDIalog();
					}
				});
	}

}
