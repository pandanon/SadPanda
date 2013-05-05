package com.ecchi.sadpanda.imageviewer;

import uk.co.senab.photoview.PhotoView;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView.ScaleType;

import com.ecchi.sadpanda.R;
import com.ecchi.sadpanda.tasks.ImageSetLink;
import com.ecchi.sadpanda.tasks.LoadImageLinkTask;
import com.ecchi.sadpanda.tasks.LoadImageLinkTask.ImageSetViewer;
import com.ecchi.sadpanda.util.ImageLoader;

public class ViewerAdapter extends PagerAdapter implements ImageSetViewer {
	
	public interface ViewPagerChildFinder {
		public View findChild(int position);
	}
	
	ViewPagerChildFinder mViewFinder;
	
	ImageLoader mLoader;
	SparseArray<ImageSetLink> mData;

	int mSize = 0;
	boolean mIsLoading = false;
	int mItemsLoadedFromPage = 0;

	public ViewerAdapter(Context context, String startUrl, int position, int totalSize) {
		mLoader = new ImageLoader(context);
		mData = new SparseArray<ImageSetLink>();		
		mSize = totalSize;
		
		new LoadImageLinkTask(this, position).execute(startUrl);
	}

	@Override
	public int getCount() {
		return mSize;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);		
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageSetLink item = mData.get(position);
		View view = null;

		view = View
				.inflate(container.getContext(), R.layout.loading_view, null);
		view.setTag(position);

		if (item == null) {
			loadImagePage(position);
		} else {
			setView(view, position);
		}

		container.addView(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		return view;
	}

	void loadImagePage(int position) {
		ImageSetLink item = mData.get(position-1);
		if(item != null && item.getNextPageUrl() != null) {
			new LoadImageLinkTask(this, position).execute(item.getNextPageUrl());
			return;
		}
		item = mData.get(position+1);
		if(item != null && item.getPreviousPageUrl() != null) {
			new LoadImageLinkTask(this, position).execute(item.getPreviousPageUrl());
		}
		
	}

	void setView(View view, int position) {
		view.findViewById(R.id.empty).setVisibility(View.GONE);
		
		PhotoView photo = (PhotoView)view.findViewById(R.id.photoView);
		photo.setVisibility(View.VISIBLE);
		photo.setScaleType(ScaleType.FIT_START);
		mLoader.loadBitmap(mData.get(position).getCurrentImageUrl(), photo);
	}
	
	void updateView(int position) {
		if(mViewFinder != null) {
			View child = mViewFinder.findChild(position);
			if(child != null) {
				setView(child, position);
			}
				
		}
	}
	
	public void setViewPagerChildFinder(ViewPagerChildFinder mViewFinder) {
		this.mViewFinder = mViewFinder;
	}

	@Override
	public void addImageSetLink(ImageSetLink link) {
		if(link != null) {
			mData.append(link.getPosition(), link);
			updateView(link.getPosition());
		}		
	}
}
