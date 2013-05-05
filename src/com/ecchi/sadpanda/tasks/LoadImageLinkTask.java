package com.ecchi.sadpanda.tasks;

import android.os.AsyncTask;

import com.ecchi.sadpanda.tasks.LoadDetailPageImageTask.ImageSetViewer;
import com.ecchi.sadpanda.util.ImageSetItem;
import com.ecchi.sadpanda.util.Utils;

public class LoadImageLinkTask extends AsyncTask<ImageSetItem, Void, ImageSetItem> {
	
	ImageSetViewer mViewer;
	
	public LoadImageLinkTask(ImageSetViewer viewer)
	{
		mViewer = viewer;
	}
	
	@Override
	protected ImageSetItem doInBackground(ImageSetItem... params) {
		ImageSetItem item = params[0];
		String imageUrl = null;
		// check if task hasn't been called on accident and the link was loaded
		// already
		if (item != null && item.getImageLinkUrl() == null) {
			String result = Utils.LoadSadPandaLink(item.getImagePageUrl());
			
			String token = "<div id=\"i3\">";
			int startIdx = result.indexOf(token);
			token = "src=\"";
			startIdx = result.indexOf(token, startIdx) + token.length();			
			int endIdx = result.indexOf("\"", startIdx);
			
			imageUrl = result.substring(startIdx, endIdx);			
		}

		synchronized (item) {
			item.setImageLinkUrl(imageUrl);
		}
		
		return item;
	}
	
	@Override
	protected void onPostExecute(ImageSetItem result) {
		super.onPostExecute(result);
		
		mViewer.addImageSetItem(result);
	}
}
