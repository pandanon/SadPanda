package com.ecchi.sadpanda.imageviewer;

import uk.co.senab.photoview.PhotoView;
import android.content.Context;
import android.graphics.drawable.Drawable;
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

	public ViewerAdapter(Context context, String startUrl, int position,
			int totalSize) {
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
		ImageSetLink item = mData.get(position);
		if (item != null)
			ImageLoader.cancelPotentialWork(item.getCurrentImageUrl(),
					((PhotoView) ((View) object).findViewById(R.id.photoView)));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageSetLink item = mData.get(position);
		View view = null;

		view = View
				.inflate(container.getContext(), R.layout.loading_view, null);
		view.setTag(position);

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

		PhotoView photo = (PhotoView) view.findViewById(R.id.photoView);
		photo.setScaleType(ScaleType.FIT_START);
		mLoader.loadBitmap(item.getCurrentImageUrl(), item.getCurrentPageUrl(),
				photo, view.findViewById(R.id.empty));
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
			mData.append(link.getPosition(), link);
			updateView(link.getPosition());
			notifyDataSetChanged();
		}
	}

	@Override
	public int getItemPosition(Object object) {
		int pos = (Integer) ((View) object).getTag();

		if (mData.get(pos) == null) {
			if (mData.get(pos - 1) != null || mData.get(pos + 1) != null)
				return POSITION_NONE;
			else
				return POSITION_UNCHANGED;
		} else {
			return POSITION_UNCHANGED;
		}
	}
}
