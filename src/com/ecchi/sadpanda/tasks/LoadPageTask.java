package com.ecchi.sadpanda.tasks;

import java.util.List;

import android.os.AsyncTask;

import com.ecchi.sadpanda.util.ImageSetItem;
import com.ecchi.sadpanda.util.OnAddPageListener;
import com.ecchi.sadpanda.util.Utils;

public abstract class LoadPageTask<T> extends AsyncTask<String, ImageSetItem, List<T>> {

	protected OnAddPageListener<T> mAdapter;
	
	public LoadPageTask(OnAddPageListener<T> pageAdapter)
	{
		mAdapter = pageAdapter;
	}
	
	@Override
	protected List<T> doInBackground(String... params) {
		String resString = Utils.LoadSadPandaLink(params[0]);
		
		return parseHtmlContent(resString);
	}
	
	protected abstract List<T> parseHtmlContent(String content);
	
	@Override
	protected void onPostExecute(List<T> result) {
		super.onPostExecute(result);
		
		if (!(result != null && result.size() > 0))
			return;
		
		mAdapter.addPage(result);
	}
	
}
