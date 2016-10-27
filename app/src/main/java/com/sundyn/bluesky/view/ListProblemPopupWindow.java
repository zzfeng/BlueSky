package com.sundyn.bluesky.view;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.baseadapterhelper.BaseAdapterHelper;
import com.sundyn.bluesky.baseadapterhelper.QuickAdapter;
import com.sundyn.bluesky.bean.AreaOfOld;
import com.sundyn.bluesky.bean.AreaOfOld.Region;

import java.util.List;

public class ListProblemPopupWindow extends
		BasePopupWindowForListView<AreaOfOld.Region> {
	private ListView lv_problem;
	private QuickAdapter<Region> mAdapter;

	public ListProblemPopupWindow(int width, int height,
			List<AreaOfOld.Region> datas, View convertView) {
		super(convertView, width, height, true, datas);
	}

	@Override
	public void initViews() {
		lv_problem = (ListView) findViewById(R.id.id_list_dir);
		mAdapter = new QuickAdapter<AreaOfOld.Region>(context,
				R.layout.pop_compaint_item) {

			@Override
			protected void convert(BaseAdapterHelper helper, Region item) {
				helper.setText(R.id.id_tv_problem, item.argName);
			}
		};
		lv_problem.setAdapter(mAdapter);
		mAdapter.addAll(mDatas);

	}

	public interface OnProblemSelected {
		void selected(AreaOfOld.Region floder);
	}

	private OnProblemSelected mProblemSelected;

	public void setOnProblemSelected(OnProblemSelected mProblemSelected) {
		this.mProblemSelected = mProblemSelected;
	}

	@Override
	public void initEvents() {
		lv_problem.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (mProblemSelected != null) {
					mProblemSelected.selected(mDatas.get(position));
				}
			}
		});
	}

	@Override
	public void init() {

	}

	@Override
	protected void beforeInitWeNeedSomeParams(Object... params) {
	}

}
