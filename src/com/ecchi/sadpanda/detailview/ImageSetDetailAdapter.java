package com.ecchi.sadpanda.detailview;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageView;

import com.ecchi.sadpanda.tasks.LoadDetailDescriptionPageTask;
import com.ecchi.sadpanda.tasks.LoadDetailPageTask;
import com.ecchi.sadpanda.util.ImageLoader;
import com.ecchi.sadpanda.util.ImageSetDetailDescription;
import com.ecchi.sadpanda.util.ImageSetThumb;
import com.ecchi.sadpanda.util.PagedScrollAdapter;

public class ImageSetDetailAdapter extends PagedScrollAdapter<ImageSetThumb> {

	int currentPage = 0;
	int totalPages = 1;

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
			convertView = new ImageView(parent.getContext());

		// make sure the size of the view is the same as the thumb.
		convertView.setLayoutParams(new LayoutParams(100, 144));

		ImageSetThumb item = getItem(position);

		mImageLoader.loadBitmap(item.getThumbUrl(), (ImageView) convertView);

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

}
