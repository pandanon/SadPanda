package com.ecchi.sadpanda.util;

import java.util.ArrayList;
import java.util.List;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;

import com.ecchi.sadpanda.tasks.LoadPageTask.OnAddPageListener;

public abstract class PagedScrollAdapter<T> extends BaseAdapter implements
		OnScrollListener, OnAddPageListener<T> {

	boolean mLoading = false;
	int mPreviousTotal = 0;
	int mCurrentPage = 0;

	private List<T> mData;

	@Override
	public long getItemId(int position) {
		return position;
	}

	public PagedScrollAdapter() {
		mData = new ArrayList<T>();
	}

	@Override
	public T getItem(int position) {
		return mData.get(position);
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public final void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		if (mLoading) {
			if (totalItemCount > mPreviousTotal) {
				mLoading = false;
				mPreviousTotal = totalItemCount;
			}
		}
		if (!mLoading
				&& ((firstVisibleItem + visibleItemCount) > totalItemCount - 1)) {
			mLoading = true;
			loadNewDataSet();
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	/***
	 * 
	 * @param notify
	 *            true if the adapter should notify its listeners after clearing
	 *            its contents
	 */
	public void clear(boolean notify) {
		if (mData.size() > 0) {
			mData.clear();

			mCurrentPage = 0;
			mPreviousTotal = 0;
			mLoading = false;
			if (notify)
				notifyDataSetChanged();
		}
	}

	/**
	 * implement this function to retreive any data you want add to the adapter.
	 * Adding the data to the adapter should be done manually.
	 */
	public abstract void loadNewDataSet();

	/**
	 * general method to return information to after loadNewDataSet() has been
	 * called.
	 */
	public void addPage(List<T> dataSet) {
		mData.addAll(dataSet);
		notifyDataSetChanged();
		mCurrentPage++;
	}

	public void goToPage(int page) {
		if (page >= 0) {
			clear(false);
			mCurrentPage = page;
			notifyDataSetChanged();
		}
	}

	public void setLoading(boolean loading) {
		mLoading = loading;
	}

	public int getCurrentPage() {
		return mCurrentPage;
	}
}
