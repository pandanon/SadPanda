package com.ecchi.sadpanda.imageviewer;

import java.lang.ref.WeakReference;

import uk.co.senab.photoview.PhotoView;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.ecchi.sadpanda.HomePageBrowser;
import com.ecchi.sadpanda.R;
import com.ecchi.sadpanda.tasks.LoadImageLinkTask;
import com.ecchi.sadpanda.tasks.LoadImageLinkTask.ImageSetViewer;
import com.ecchi.sadpanda.util.ImageSetLink;
import com.novoda.imageloader.core.LoaderSettings;
import com.novoda.imageloader.core.OnImageLoadedListener;
import com.novoda.imageloader.core.cache.LruBitmapCache;
import com.novoda.imageloader.core.loader.ConcurrentLoader;
import com.novoda.imageloader.core.loader.Loader;
import com.novoda.imageloader.core.model.ImageTag;
import com.novoda.imageloader.core.model.ImageTagFactory;

public class ViewerAdapter extends PagerAdapter implements ImageSetViewer, OnImageLoadedListener {

	public interface ImageSetLinkDatabase {
		public ImageSetLink get(int position);

		public void add(int position, ImageSetLink value);
	}

	public interface ViewPagerChildFinder {
		public View findChild(int position);
	}

	ImageSetLinkDatabase mData;
	ViewPagerChildFinder mViewFinder;

	Loader mLoader;
	ImageTagFactory mTagFactory;

	int mSize = 0;
	boolean mIsLoading = false;
	int mItemsLoadedFromPage = 0;

	public ViewerAdapter(Context context, int totalSize,
			ImageSetLinkDatabase database) {
		mSize = totalSize;
		mData = database;
		
		mLoader = HomePageBrowser.getImageLoader();		
		mTagFactory = ImageTagFactory.newInstance(context, -1);
		mTagFactory.setErrorImageId(R.drawable.sadpanda);
		mLoader.setLoadListener(new WeakReference<OnImageLoadedListener>(this));
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
		View view = View.inflate(container.getContext(), R.layout.loading_view, null);
		view.setId(position);
		
		if (item != null) {
			setView(view, position);
		} else {
			loadPage(position);
		}

		container.addView(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		return view;
	}

	void setView(View view, int position) {
		ImageSetLink item = mData.get(position);

		PhotoView photo = (PhotoView)view.findViewById(R.id.photoView);
		photo.setScaleType(ScaleType.FIT_START);
		photo.setId(position);
		
		ImageTag tag = mTagFactory.build(item.getCurrentImageUrl(),
				view.getContext());
		tag.setDescription(String.valueOf(position));
		photo.setTag(tag);
		mLoader.load(photo);
	}

	void updateView(int position) {
		if (mViewFinder != null) {
			View child = mViewFinder.findChild(position);
			if (child != null) {
				setView(child, position);
			}

		}
	}

	void loadPage(int position) {
		ImageSetLink item = mData.get(position - 1);
		if (item != null) {
			new LoadImageLinkTask(this, position)
					.execute(item.getNextPageUrl());
			return;
		}

		item = mData.get(position + 1);
		if (item != null)
			new LoadImageLinkTask(this, position).execute(item
					.getPreviousPageUrl());
	}

	public void setViewPagerChildFinder(ViewPagerChildFinder mViewFinder) {
		this.mViewFinder = mViewFinder;
	}

	@Override
	public void addImageSetLink(ImageSetLink link) {
		if (link != null) {
			mData.add(link.getPosition(), link);
			updateView(link.getPosition());
			notifyDataSetChanged();
		}
	}

	int mResetPosition = -1;

	@Override
	public int getItemPosition(Object object) {
		int pos = (Integer) ((View) object).getId();

		if (pos == mResetPosition) {
			mResetPosition = -1;
			return POSITION_NONE;
		}

		if (mData.get(pos) == null) {
			if (mData.get(pos - 1) != null || mData.get(pos + 1) != null)
				return POSITION_NONE;
			else
				return POSITION_UNCHANGED;
		} else {
			return POSITION_UNCHANGED;
		}
	}

	public synchronized void resetView(int position) {
		mResetPosition = position;
		notifyDataSetChanged();
	}

	@Override
	public void onImageLoaded(ImageView arg0) {
		if(mViewFinder != null) {
			View view = mViewFinder.findChild(arg0.getId());
			if(view != null) {
				view.findViewById(R.id.empty).setVisibility(View.GONE);
			}
		}
		arg0.setVisibility(View.VISIBLE);
	}
}
