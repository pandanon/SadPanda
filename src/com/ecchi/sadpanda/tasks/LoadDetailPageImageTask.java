package com.ecchi.sadpanda.tasks;

import com.ecchi.sadpanda.detailview.ImageSetDetailAdapter;
import com.ecchi.sadpanda.util.ImageSetItem;

public class LoadDetailPageImageTask extends LoadDetailPageTask {

	public LoadDetailPageImageTask(ImageSetDetailAdapter pageAdapter) {
		super(pageAdapter);
	}
	
	@Override
	protected void onProgressUpdate(ImageSetItem... values) {	
		super.onProgressUpdate(values);
		new LoadImageLinkTask().execute(values);
	}

}
