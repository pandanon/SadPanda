package com.ecchi.sadpanda.imageviewer;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.ecchi.sadpanda.tasks.LoadDetailPageTask.ImageContainer;
import com.ecchi.sadpanda.util.ImageLoader;
import com.ecchi.sadpanda.util.ImageSetItem;

public class ViewerAdapter extends PagerAdapter implements ImageContainer {

	ImageLoader mLoader;
	
	SparseArray<ImageSetItem> mData;
	String mBaseUrl;
	
	public ViewerAdapter(Context context, String baseUrl)
	{
		mLoader = new ImageLoader(context);
		mData = new SparseArray<ImageSetItem>();
		mBaseUrl = baseUrl;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
		
		container.removeView((View)object);
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageSetItem item = mData.get(position);
		if(item == null)
			loadPage(position);
				
		return null;
	}
	
	void loadPage(int position) {
		
	}

	@Override
	public void addPage(List<ImageSetItem> dataSet) {
		for(ImageSetItem item: dataSet)
		{
			mData.append(item.getPosition(), item);
		}
	}

	@Override
	public ImageLoader getImageLoader() {
		return mLoader;
	}
}
