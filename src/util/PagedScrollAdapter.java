package util;

import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.AbsListView.OnScrollListener;

public abstract class PagedScrollAdapter extends BaseAdapter implements OnScrollListener {
	private boolean loading = false;
	private boolean failedLoading = false;
	private int previousTotal = 0;
	
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
		previousTotal = 0;
		loading = true;
	}
	
	/**
	 * implement this function to retreive any data you want add to the adapter. Adding it to the adapter should be done manually.
	 */
	public abstract void loadNewDataSet();
}
