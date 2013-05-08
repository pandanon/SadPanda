package com.ecchi.sadpanda.detailview;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.ecchi.sadpanda.HomePageBrowser;
import com.ecchi.sadpanda.R;
import com.ecchi.sadpanda.tasks.LoadDetailPageTask;
import com.ecchi.sadpanda.util.CroppedImageView;
import com.ecchi.sadpanda.util.ImageSetDetailDescription;
import com.ecchi.sadpanda.util.ImageSetItem;
import com.ecchi.sadpanda.util.PagedScrollAdapter;
import com.novoda.imageloader.core.loader.Loader;
import com.novoda.imageloader.core.model.ImageTagFactory;

public class ImageSetDetailAdapter extends PagedScrollAdapter<ImageSetItem> {

	int mCurrentPage = 0;
	int mTotalPages = 1;
	public static final int PADDING = 5;

	ImageSetDetailDescription mDescription;

	Loader mLoader;
	ImageTagFactory mTagFactory;

	String baseUrl;

	public ImageSetDetailAdapter(String url, Context context) {
		this.baseUrl = url;
		mLoader = HomePageBrowser.getImageLoader();
		mTagFactory = ImageTagFactory.newInstance(context, -1);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = View.inflate(parent.getContext(), R.layout.thumb_view_item, null);

		convertView.setLayoutParams( new GridView.LayoutParams(100, 164));
		ImageSetItem item = getItem(position);	
		
		CroppedImageView thumb = (CroppedImageView)convertView.findViewById(R.id.thumb);
		initCroppedThumb(thumb, item);
		
		thumb.setTag(mTagFactory.build(item.getThumbUrl(), parent.getContext()));
		
		mLoader.load(thumb);
		
		return convertView;
	}
	
	void initCroppedThumb(CroppedImageView thumbnail, ImageSetItem item) {
		int height = item.getHeight(), width = item.getWidth();
		
		if(height == -1 && width == -1) {
			height = 144;
			width = 100;
		}
		
		thumbnail.setBounds(item.getOffset(), 0, width, height);
		thumbnail.setLayoutParams(new LinearLayout.LayoutParams(width, height));
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
}
