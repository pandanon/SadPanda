package com.ecchi.sadpanda.util;

import java.util.ArrayList;
import java.util.List;

import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.AbsListView.OnScrollListener;

public abstract class PagedScrollAdapter<T> extends BaseAdapter implements OnScrollListener {
	private boolean loading = false;
	private boolean failedLoading = false;
	private int previousTotal = 0;
	
	private List<T> data;
	
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public PagedScrollAdapter() {
		data = new ArrayList<T>();
	}
	
	@Override
	public T getItem(int position) {
		return data.get(position);
	}
	
	@Override
	public int getCount() {		
		return data.size();
	}
	
	@Override
	public final void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
		if (loading) {
			if (totalItemCount > previousTotal) {
				loading = false;
				previousTotal = totalItemCount;
			}
		}
		if (!loading && !failedLoading
				&& ((firstVisibleItem + visibleItemCount) > totalItemCount - 1)) {
			loading = true;
			loadNewDataSet();
		}
		
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}
	
	public void failedLoading()
	{
		failedLoading = true;
	}
	
	public void clear()
	{
		data.clear();
		
		previousTotal = 0;
		loading = true;		
	}
	
	/**
	 * implement this function to retreive any data you want add to the adapter. Adding the data to the adapter should be done manually.
	 */
	public abstract void loadNewDataSet();
	
	/**
	 * general method to return information to after loadNewDataSet() has been called.
	 */
	public void addPage(List<T> dataSet)
	{
		data.addAll(dataSet);
		notifyDataSetChanged();
	}
}
