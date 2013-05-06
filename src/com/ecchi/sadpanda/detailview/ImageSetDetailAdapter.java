package com.ecchi.sadpanda.detailview;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.ecchi.sadpanda.R;
import com.ecchi.sadpanda.tasks.LoadDetailPageTask;
import com.ecchi.sadpanda.tasks.LoadDetailPageTask.ImageContainer;
import com.ecchi.sadpanda.util.CroppedImageView;
import com.ecchi.sadpanda.util.ImageLoader;
import com.ecchi.sadpanda.util.ImageSetDetailDescription;
import com.ecchi.sadpanda.util.ImageSetItem;
import com.ecchi.sadpanda.util.PagedScrollAdapter;

public class ImageSetDetailAdapter extends PagedScrollAdapter<ImageSetItem> implements ImageContainer {

	int mCurrentPage = 0;
	int mTotalPages = 1;
	public static final int PADDING = 5;

	ImageSetDetailDescription mDescription;

	ImageLoader mImageLoader;

	String baseUrl;

	public ImageSetDetailAdapter(String url, Context context) {
		this.baseUrl = url;
		mImageLoader = new ImageLoader(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = View.inflate(parent.getContext(), R.layout.thumb_view_item, null);

		convertView.setLayoutParams( new GridView.LayoutParams(100, 164));
		ImageSetItem item = getItem(position);	
		
		CroppedImageView thumb = (CroppedImageView)convertView.findViewById(R.id.thumb);
		// if the height has been predefined on the page, use it.
		int height = item.getHeight();
		
		if(height == -1)
			height = 144;
		
		thumb.setLayoutParams(new LinearLayout.LayoutParams(100, height));

		mImageLoader.loadBitmap(item.getThumbUrl(), null, thumb);
		
		return convertView;
	}

	@Override
	public void loadNewDataSet() {
		if (mCurrentPage < mTotalPages) {
				new LoadDetailPageTask(this).execute(baseUrl + mCurrentPage);				
		}
	}

	public void setTotalPages(int totalPages) {
		mTotalPages = totalPages;
	}

	@Override
	public void addPage(List<ImageSetItem> dataSet) {
		super.addPage(dataSet);
		mCurrentPage++;
	}
	
	public ImageLoader getImageLoader()
	{
		return mImageLoader;
	}
}
