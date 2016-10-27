package com.sundyn.bluesky.model;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.sundyn.bluesky.R;

import java.util.ArrayList;

public class LineCharModel {
	private static LineCharModel instance = new LineCharModel();

	private LineCharModel() {
	}

	public static LineCharModel getLineCharModel() {
		return instance;
	}

	/**
	 * 格式化折线数据
	 * 
	 * @param //chartData
	 * @return
	 */
	private LineData initChartData(ArrayList<String> xValues,
			ArrayList<Entry> yValues, Context ctx) {
		LineDataSet lineDataSet = new LineDataSet(yValues, "PM10折线图");
		// 用y轴的集合来设置参数
		lineDataSet.setLineWidth(1.75f); // 线宽
		lineDataSet.setCircleSize(3f);// 显示的圆形大小
		lineDataSet.setColor(ctx.getResources().getColor(
				R.color.blue_datacenter));// 显示颜色
		lineDataSet.setCircleColor(ctx.getResources().getColor(
				R.color.blue_datacenter));// 圆形的颜色
		lineDataSet.setHighLightColor(Color.RED); // 高亮的线的颜色

		ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
		lineDataSets.add(lineDataSet); // add the datasets

		LineData lineData = new LineData(xValues, lineDataSets);

		return lineData;

	}

	/**
	 * 
	 * @param lineChart
	 * @param xValues
	 * @param yValues
	 */
	public void showChart(LineChart lineChart, ArrayList<String> xValues,
			ArrayList<Entry> yValues, Context context) {
		LineData lineData = this.initChartData(xValues, yValues, context);

		XAxis xAxis = lineChart.getXAxis();
		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // 设置X轴的位置
		xAxis.setLabelsToSkip(0);
		xAxis.setAxisLineColor(context.getResources().getColor(
				R.color.blue_datacenter));
		xAxis.setAxisLineWidth(0.5f);
		xAxis.setAvoidFirstLastClipping(true);
		xAxis.setDrawGridLines(false);

		lineChart.getAxisRight().setEnabled(false);// y轴右边

		YAxis leftY = lineChart.getAxisLeft();
		leftY.setAxisLineColor(context.getResources().getColor(
				R.color.blue_datacenter));
		leftY.setAxisLineWidth(0.5f);
		leftY.setDrawGridLines(false);

		lineChart.setDrawBorders(false); // 是否在折线图上添加边框
		lineChart.setDescription("");// 数据描述
		lineChart.setNoDataTextDescription("无数据");

		lineChart.setDrawGridBackground(false); // 是否显示表格颜色
		lineChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF); // 表格的的颜色，在这里是是给颜色设置一个透明度

		lineChart.setTouchEnabled(true); // 设置是否可以触摸

		lineChart.setDragEnabled(false);// 是否可以拖拽
		lineChart.setScaleEnabled(false);// 是否可以缩放

		lineChart.setPinchZoom(false);//
		lineChart.setBackgroundColor(Color.WHITE);// 设置背景

		lineChart.setData(lineData); // 设置数据

		Legend mLegend = lineChart.getLegend(); // 设置比例图标示，就是那个一组y的value的
		mLegend.setEnabled(false);

		lineChart.animateX(2500); // 立即执行的动画,x轴
	}

	/**
	 * 
	 * @param pieChart
	 * @param pieData
	 * @param context
	 * @param showLegend 是否显示比例图
	 * @param showPercent 是否显示百分比
	 * @param isFanned  是否为扇形
	 */
	public void showPieChart(PieChart pieChart, PieData pieData,
			Context context, boolean showLegend,boolean showPercent,boolean isFanned) {
		pieChart.setNoDataText("暂无数据！");
//		pieChart.setNoDataTextDescription("asdrasdfasdf");
		pieData.setValueTextSize(9f);
		pieData.setValueTextColor(context.getResources().getColor(
				R.color.font_color_all));// 饼状图上的字
		if(isFanned) {
			pieChart.setHoleRadius(0); //实心圆
			pieChart.setTransparentCircleRadius(0f); // 半透明圈
		}else {
			pieChart.setHoleColorTransparent(true);
			pieChart.setHoleRadius(60f); // 半径
			pieChart.setTransparentCircleRadius(60f); // 半透明圈
			pieChart.setTransparentCircleColor(context.getResources().getColor(
					R.color.transhalf));
		}
		pieChart.setDescription("");
		pieChart.setDrawCenterText(true); // 饼状图中间可以添加文字
		pieChart.setDrawHoleEnabled(true);
		pieChart.setRotationAngle(90); // 初始旋转角度
		pieChart.setRotationEnabled(true); // 可以手动旋转
		pieChart.setUsePercentValues(showPercent); // 显示成百分比
		// 设置数据
		pieChart.setDrawSliceText(false);// 只显示比例，不显示文字
		pieChart.setData(pieData);
		pieChart.animateXY(1000, 1000); // 设置动画

		Legend mLegend = pieChart.getLegend(); // 设置比例图
		if (showLegend) {
			mLegend.setWordWrapEnabled(true);
			mLegend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT); // 左下边显示
			mLegend.setXEntrySpace(5f);
			mLegend.setYEntrySpace(5f);
			mLegend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
			mLegend.setTextColor(context.getResources().getColor(
					R.color.font_color_all_sub));
			mLegend.setForm(Legend.LegendForm.CIRCLE);// 设置比例块形状，默认为方块
		} else {
			mLegend.setEnabled(false);
		}

	}

	/** 解决PieChart因为获取数据延迟导致底部的比例块显示只有一行的问题 */
	public void refreashPieChart(PieChart pieChart, PieData pieData) {
		pieChart.setData(pieData);
		pieChart.highlightValues(null);
		pieChart.invalidate();
	}
}
