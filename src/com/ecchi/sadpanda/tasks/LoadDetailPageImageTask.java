package com.ecchi.sadpanda.tasks;

import com.ecchi.sadpanda.util.ImageSetItem;

public class LoadDetailPageImageTask extends LoadDetailPageTask {

	public interface ImageSetViewer extends ImageContainer {
		public void addImageSetItem(ImageSetItem item);
	}
	
	public LoadDetailPageImageTask(ImageSetViewer pageAdapter) {
		super(pageAdapter);
	}
	
	@Override
	protected void onProgressUpdate(ImageSetItem... values) {	
		super.onProgressUpdate(values);
		new LoadImageLinkTask((ImageSetViewer)mAdapter).execute(values);
	}

}
