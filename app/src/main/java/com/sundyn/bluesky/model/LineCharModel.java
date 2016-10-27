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
	 * ��ʽ����������
	 * 
	 * @param //chartData
	 * @return
	 */
	private LineData initChartData(ArrayList<String> xValues,
			ArrayList<Entry> yValues, Context ctx) {
		LineDataSet lineDataSet = new LineDataSet(yValues, "PM10����ͼ");
		// ��y��ļ��������ò���
		lineDataSet.setLineWidth(1.75f); // �߿�
		lineDataSet.setCircleSize(3f);// ��ʾ��Բ�δ�С
		lineDataSet.setColor(ctx.getResources().getColor(
				R.color.blue_datacenter));// ��ʾ��ɫ
		lineDataSet.setCircleColor(ctx.getResources().getColor(
				R.color.blue_datacenter));// Բ�ε���ɫ
		lineDataSet.setHighLightColor(Color.RED); // �������ߵ���ɫ

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
		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // ����X���λ��
		xAxis.setLabelsToSkip(0);
		xAxis.setAxisLineColor(context.getResources().getColor(
				R.color.blue_datacenter));
		xAxis.setAxisLineWidth(0.5f);
		xAxis.setAvoidFirstLastClipping(true);
		xAxis.setDrawGridLines(false);

		lineChart.getAxisRight().setEnabled(false);// y���ұ�

		YAxis leftY = lineChart.getAxisLeft();
		leftY.setAxisLineColor(context.getResources().getColor(
				R.color.blue_datacenter));
		leftY.setAxisLineWidth(0.5f);
		leftY.setDrawGridLines(false);

		lineChart.setDrawBorders(false); // �Ƿ�������ͼ����ӱ߿�
		lineChart.setDescription("");// ��������
		lineChart.setNoDataTextDescription("������");

		lineChart.setDrawGridBackground(false); // �Ƿ���ʾ�����ɫ
		lineChart.setGridBackgroundColor(Color.WHITE & 0x70FFFFFF); // ���ĵ���ɫ�����������Ǹ���ɫ����һ��͸����

		lineChart.setTouchEnabled(true); // �����Ƿ���Դ���

		lineChart.setDragEnabled(false);// �Ƿ������ק
		lineChart.setScaleEnabled(false);// �Ƿ��������

		lineChart.setPinchZoom(false);//
		lineChart.setBackgroundColor(Color.WHITE);// ���ñ���

		lineChart.setData(lineData); // ��������

		Legend mLegend = lineChart.getLegend(); // ���ñ���ͼ��ʾ�������Ǹ�һ��y��value��
		mLegend.setEnabled(false);

		lineChart.animateX(2500); // ����ִ�еĶ���,x��
	}

	/**
	 * 
	 * @param pieChart
	 * @param pieData
	 * @param context
	 * @param showLegend �Ƿ���ʾ����ͼ
	 * @param showPercent �Ƿ���ʾ�ٷֱ�
	 * @param isFanned  �Ƿ�Ϊ����
	 */
	public void showPieChart(PieChart pieChart, PieData pieData,
			Context context, boolean showLegend,boolean showPercent,boolean isFanned) {
		pieChart.setNoDataText("�������ݣ�");
//		pieChart.setNoDataTextDescription("asdrasdfasdf");
		pieData.setValueTextSize(9f);
		pieData.setValueTextColor(context.getResources().getColor(
				R.color.font_color_all));// ��״ͼ�ϵ���
		if(isFanned) {
			pieChart.setHoleRadius(0); //ʵ��Բ
			pieChart.setTransparentCircleRadius(0f); // ��͸��Ȧ
		}else {
			pieChart.setHoleColorTransparent(true);
			pieChart.setHoleRadius(60f); // �뾶
			pieChart.setTransparentCircleRadius(60f); // ��͸��Ȧ
			pieChart.setTransparentCircleColor(context.getResources().getColor(
					R.color.transhalf));
		}
		pieChart.setDescription("");
		pieChart.setDrawCenterText(true); // ��״ͼ�м�����������
		pieChart.setDrawHoleEnabled(true);
		pieChart.setRotationAngle(90); // ��ʼ��ת�Ƕ�
		pieChart.setRotationEnabled(true); // �����ֶ���ת
		pieChart.setUsePercentValues(showPercent); // ��ʾ�ɰٷֱ�
		// ��������
		pieChart.setDrawSliceText(false);// ֻ��ʾ����������ʾ����
		pieChart.setData(pieData);
		pieChart.animateXY(1000, 1000); // ���ö���

		Legend mLegend = pieChart.getLegend(); // ���ñ���ͼ
		if (showLegend) {
			mLegend.setWordWrapEnabled(true);
			mLegend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT); // ���±���ʾ
			mLegend.setXEntrySpace(5f);
			mLegend.setYEntrySpace(5f);
			mLegend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
			mLegend.setTextColor(context.getResources().getColor(
					R.color.font_color_all_sub));
			mLegend.setForm(Legend.LegendForm.CIRCLE);// ���ñ�������״��Ĭ��Ϊ����
		} else {
			mLegend.setEnabled(false);
		}

	}

	/** ���PieChart��Ϊ��ȡ�����ӳٵ��µײ��ı�������ʾֻ��һ�е����� */
	public void refreashPieChart(PieChart pieChart, PieData pieData) {
		pieChart.setData(pieData);
		pieChart.highlightValues(null);
		pieChart.invalidate();
	}
}
