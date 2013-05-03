package com.ecchi.sadpanda.detailview;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.ecchi.sadpanda.R;
import com.ecchi.sadpanda.tasks.LoadDetailDescriptionPageTask;
import com.ecchi.sadpanda.tasks.LoadDetailPageTask;
import com.ecchi.sadpanda.util.CroppedImageView;
import com.ecchi.sadpanda.util.ImageLoader;
import com.ecchi.sadpanda.util.ImageSetDetailDescription;
import com.ecchi.sadpanda.util.ImageSetThumb;
import com.ecchi.sadpanda.util.PagedScrollAdapter;

public class ImageSetDetailAdapter extends PagedScrollAdapter<ImageSetThumb> {

	int currentPage = 0;
	int totalPages = 1;
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
		ImageSetThumb item = getItem(position);	
		
		CroppedImageView thumb = (CroppedImageView)convertView.findViewById(R.id.thumb);
		// if the height has been predefined on the page, use it.
		int height = item.getHeight();
		
		if(height == -1)
			height = 144;
		
		thumb.setLayoutParams(new LinearLayout.LayoutParams(100, height));

		mImageLoader.loadBitmap(item.getThumbUrl(), thumb);
		
		return convertView;
	}

	@Override
	public void loadNewDataSet() {
		if (currentPage < totalPages) {
			if (currentPage > 0)
				new LoadDetailPageTask(this).execute(baseUrl + currentPage);
			else
				new LoadDetailDescriptionPageTask(this).execute(baseUrl
						+ currentPage);
		}
	}

	public void setImageSetDetailDescription(
			ImageSetDetailDescription description) {
		mDescription = description;
		totalPages = mDescription.getTotalPages();
	}

	@Override
	public void addPage(List<ImageSetThumb> dataSet) {
		super.addPage(dataSet);
		currentPage++;
	}
	
	public ImageLoader getImageLoader()
	{
		return mImageLoader;
	}
}
