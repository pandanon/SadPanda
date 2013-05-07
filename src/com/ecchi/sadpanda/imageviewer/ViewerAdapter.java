package com.ecchi.sadpanda.imageviewer;

import uk.co.senab.photoview.PhotoView;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView.ScaleType;

import com.ecchi.sadpanda.R;
import com.ecchi.sadpanda.tasks.LoadImageLinkTask;
import com.ecchi.sadpanda.tasks.LoadImageLinkTask.ImageSetViewer;
import com.ecchi.sadpanda.util.ImageSetLink;
import com.novoda.imageloader.core.LoaderSettings;
import com.novoda.imageloader.core.cache.LruBitmapCache;
import com.novoda.imageloader.core.loader.ConcurrentLoader;
import com.novoda.imageloader.core.model.ImageTag;
import com.novoda.imageloader.core.model.ImageTagFactory;

public class ViewerAdapter extends PagerAdapter implements ImageSetViewer {

	public interface ImageSetLinkDatabase {
		public ImageSetLink get(int position);

		public void add(int position, ImageSetLink value);
	}

	public interface ViewPagerChildFinder {
		public View findChild(int position);
	}

	ImageSetLinkDatabase mData;
	ViewPagerChildFinder mViewFinder;

	ConcurrentLoader mLoader;
	ImageTagFactory mTagFactory;

	int mSize = 0;
	boolean mIsLoading = false;
	int mItemsLoadedFromPage = 0;

	public ViewerAdapter(Context context, int totalSize,
			ImageSetLinkDatabase database) {
		mSize = totalSize;
		mData = database;

		LoaderSettings settings = new LoaderSettings.SettingsBuilder()
		.withCacheManager(new LruBitmapCache(context)).build(context);

		mLoader = new ConcurrentLoader(settings);
		mTagFactory = ImageTagFactory.newInstance(context, R.drawable.sadpanda);
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
		PhotoView view = new PhotoView(container.getContext());
		view.setId(position);

		if (item != null) {
			setView(view, position);
		} else {
			loadPage(position);
			view.setImageResource(R.drawable.sadpanda);
		}

		container.addView(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		return view;
	}

	void setView(PhotoView view, int position) {
		ImageSetLink item = mData.get(position);

		view.setScaleType(ScaleType.FIT_START);

		ImageTag tag = mTagFactory.build(item.getCurrentImageUrl(),
				view.getContext());
		tag.setDescription(String.valueOf(position));
		view.setTag(tag);
		mLoader.load(view);
	}

	void updateView(int position) {
		if (mViewFinder != null) {
			View child = mViewFinder.findChild(position);
			if (child != null) {
				setView((PhotoView) child, position);
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
}
