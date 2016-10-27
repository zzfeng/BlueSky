package com.sundyn.bluesky.baseadapterhelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.sundyn.bluesky.R;
import com.sundyn.bluesky.utils.CommonUtil;
import com.sundyn.bluesky.utils.ImageUtils;

import java.io.IOException;
import java.util.List;

public class CameraImageAdapter extends BaseAdapter {
	private List<String> imageDatas;
	private Context mContext;
	private LayoutInflater mLayInflater;

	public CameraImageAdapter(Context context, List<String> imageDatas) {
		this.mContext = context;
		this.imageDatas = imageDatas;
		mLayInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return imageDatas.size() + 1;
	}

	@Override
	public Object getItem(int arg0) {
		return imageDatas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mLayInflater.inflate(R.layout.grid_item, parent,
					false);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView
					.findViewById(R.id.id_item_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position == imageDatas.size()) {
			Picasso.with(mContext)
					.load(R.mipmap.icon_addpic_unfocused)
					.resize(CommonUtil.dip2px(mContext, 60),
							CommonUtil.dip2px(mContext, 60)).centerCrop()
					.into(holder.image);
			if (position == 5) {
				holder.image.setVisibility(View.GONE);
			}
		} else {
			/*Picasso.with(mContext)
					.load(new File(imageDatas.get(position)))
					.placeholder(R.drawable.pictures_no)
					.error(R.drawable.pictures_no)
					.transform(new CropSquareTransformation())
					.into(holder.image);*/
			try {
				holder.image.setImageBitmap(ImageUtils.revitionImageSize(imageDatas.get(position)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return convertView;
	}

	static class ViewHolder {
		public ImageView image;
	}

}
