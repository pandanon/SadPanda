package com.ecchi.sadpanda.tasks;

import java.util.ArrayList;
import java.util.List;

import com.ecchi.sadpanda.imageviewer.ViewerAdapter;

import android.os.AsyncTask;

public class LoadImageLinksTask extends AsyncTask<String, Void, List<String>> {

	final int mPrefetchCount;
	final ViewerAdapter mAdapter;
	String lastUrl = null;

	public LoadImageLinksTask(ViewerAdapter adapter, int prefetchCount) {
		mPrefetchCount = prefetchCount;
		mAdapter = adapter;
	}

	@Override
	protected List<String> doInBackground(String... params) {
		if(params == null || params.length == 0)
			return null;
		
		String content = null;
		String url = params[0];
		int availableUrls = params.length;

		List<String> imageLinks = new ArrayList<String>();

		for (int i = 1; i < mPrefetchCount + 1; i++) {
			content = getPage(url);

			// TODO get image link from content

			// check if the needed url has been passed as parameter
			if (availableUrls < i) {
				url = getNextPage(content);
				// if there is no url, we've probably reached the end of the set
				if (url == null)
					break;
			} else
				url = params[i];
		}

		//pass the final url to the adapter for future calls of this task
		lastUrl = url;
		
		return imageLinks;
	}

	@Override
	protected void onPostExecute(List<String> result) {
		super.onPostExecute(result);

		if (!isCancelled() || result == null || result.size() == 0) {

		}
	}

	String getNextPage(String content) {
		return null;
	}

	String getPage(String url) {
		return null;
	}

}
