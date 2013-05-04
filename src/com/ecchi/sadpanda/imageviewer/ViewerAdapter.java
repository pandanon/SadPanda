/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.ecchi.sadpanda.imageviewer;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

import com.ecchi.sadpanda.tasks.LoadImageLinksTask;
import com.ecchi.sadpanda.util.ImageLoader;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView.ScaleType;

public class ViewerAdapter extends PagerAdapter {

	static final int PREFETCH_COUNT = 10;
	static final int DISTANCE_TO_END = 3;

	ImageLoader mImageLoader;
	ArrayList<String> mData;
	String mLastUrl;
	int mCurrentPosition = 0;
	boolean mIsPrefetching = false;

	final String[] mAvailableUrls;

	public ViewerAdapter(Context context, String[] availableUrls) {
		mImageLoader = new ImageLoader(context);
		mData = new ArrayList<String>();
		mAvailableUrls = availableUrls;
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
	public Object instantiateItem(ViewGroup container, int position) {
		checkForPrefetchNeeded(position);

		PhotoView photoView = new PhotoView(container.getContext());
		photoView.setScaleType(ScaleType.FIT_START);

		mImageLoader.loadBitmap(mData.get(position), photoView);

		// Now just add PhotoView to ViewPager and return it
		container.addView(photoView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		return photoView;
	}

	void checkForPrefetchNeeded(int position) {
		if (position > mCurrentPosition)
			mCurrentPosition = position;

		if (mCurrentPosition + DISTANCE_TO_END > getCount() && !mIsPrefetching) {
			// TODO prevent loading after set has been fetched
			new LoadImageLinksTask(this, PREFETCH_COUNT)
					.execute(mAvailableUrls);
			mIsPrefetching = true;
		}
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	public void addImageLinks(List<String> links, String lastUrl) {
		mLastUrl = lastUrl;
		mData.addAll(links);
		//prevents unnecessary loading after set has been fetched
		if(lastUrl != null)
			mIsPrefetching = false;
		notifyDataSetChanged();
	}
	
	public ArrayList<String> getData() {
		return mData;
	}
	
	public void setData(ArrayList<String> data)
	{
		mData = data;
		notifyDataSetChanged();
	}
}
