package com.sundyn.bluesky.view;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.sundyn.bluesky.R;
import com.sundyn.bluesky.baseadapterhelper.BaseAdapterHelper;
import com.sundyn.bluesky.baseadapterhelper.QuickAdapter;
import com.sundyn.bluesky.bean.ImageFloder;
import com.sundyn.bluesky.utils.ImageLoader;
import com.sundyn.bluesky.utils.ImageLoader.Type;

import java.util.List;

public class ListImageDirPopupWindow extends
		BasePopupWindowForListView<ImageFloder> {
	private ListView mListDir;
	private QuickAdapter<ImageFloder> mAdapter;

	public ListImageDirPopupWindow(int width, int height,
			List<ImageFloder> datas, View convertView) {
		super(convertView, width, height, true, datas);
	}

	@Override
	public void initViews() {
		mListDir = (ListView) findViewById(R.id.id_list_dir);
		mAdapter = new QuickAdapter<ImageFloder>(context,
				R.layout.list_dir_item) {

			@Override
			protected void convert(BaseAdapterHelper helper, ImageFloder item) {
				helper.setText(R.id.id_dir_item_name, item.getName());
				ImageLoader.getInstance(3, Type.LIFO).loadImage(
						item.getFirstImagePath(),
						(ImageView) helper.getView(R.id.id_dir_item_image));
				helper.setText(R.id.id_dir_item_count, item.getCount() + "уе");
			}
		};
		mListDir.setAdapter(mAdapter);
		mAdapter.addAll(mDatas);

	}

	public interface OnImageDirSelected {
		void selected(ImageFloder floder);
	}

	private OnImageDirSelected mImageDirSelected;

	public void setOnImageDirSelected(OnImageDirSelected mImageDirSelected) {
		this.mImageDirSelected = mImageDirSelected;
	}

	@Override
	public void initEvents() {
		mListDir.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (mImageDirSelected != null) {
					mImageDirSelected.selected(mDatas.get(position));
				}
			}
		});
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void beforeInitWeNeedSomeParams(Object... params) {
		// TODO Auto-generated method stub
	}

}
