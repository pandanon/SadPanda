package com.ecchi.sadpanda.imageviewer;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView.ScaleType;

import com.ecchi.sadpanda.R;
import com.ecchi.sadpanda.tasks.LoadDetailPageImageTask;
import com.ecchi.sadpanda.tasks.LoadDetailPageImageTask.ImageSetViewer;
import com.ecchi.sadpanda.util.ImageLoader;
import com.ecchi.sadpanda.util.ImageSetItem;

public class ViewerAdapter extends PagerAdapter implements ImageSetViewer {
	ImageLoader mLoader;
	SparseArray<ImageSetItem> mData;

	String mBaseUrl;
	int mSize = 0;
	boolean mIsLoading = false;
	int mItemsLoadedFromPage = 0;

	public ViewerAdapter(Context context, String baseUrl, int totalSize) {
		mLoader = new ImageLoader(context);
		mData = new SparseArray<ImageSetItem>();
		mBaseUrl = baseUrl;
		mSize = totalSize;
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
		ImageSetItem item = mData.get(position);
		View view = null;

		view = View
				.inflate(container.getContext(), R.layout.loading_view, null);
		view.setTag(position);

		if (item == null) {
			loadPage(position);
		} else {
			PhotoView photo = (PhotoView)view.findViewById(R.id.photoView);
			photo.setScaleType(ScaleType.FIT_START);
			mLoader.loadBitmap(mData.get(position).getImageLinkUrl(), photo);
			photo.setEmptyView(view.findViewById(R.id.empty));
		}

		container.addView(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		return view;
	}

	void loadPage(int position) {
		if (!mIsLoading) {
			int page = position / 20;
			new LoadDetailPageImageTask(this).execute(mBaseUrl + page);
			mIsLoading = true;
			mItemsLoadedFromPage = 0;
		}
	}

	@Override
	public void addPage(List<ImageSetItem> dataSet) {
	}

	@Override
	public ImageLoader getImageLoader() {
		return mLoader;
	}

	@Override
	public void addImageSetItem(ImageSetItem item) {
		if (item != null && item.getImageLinkUrl() != null) {
			mData.append(item.getPosition() - 1, item);
			// updateExistingViews(item.getPosition());
			notifyDataSetChanged();
		}
		// make sure all links have been loaded.
		// TODO Might pose problem with faulty connection..
		mItemsLoadedFromPage++;
		if (mItemsLoadedFromPage >= 20)
			mIsLoading = false;

	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
}
