package com.ecchi.sadpanda.tasks;

public class ImageSetLink {
	final String mPreviousPageUrl;
	final String mNextPageUrl;
	final String mCurrentImageUrl;
	final int mPosition;
	
	public ImageSetLink(String previousPage, String nextPage, String imageUrl, int position) {
		mCurrentImageUrl = imageUrl;
		mNextPageUrl = nextPage;
		mPreviousPageUrl = previousPage;
		mPosition = position;
	}
	
	public String getCurrentImageUrl() {
		return mCurrentImageUrl;
	}
	
	public String getNextPageUrl() {
		return mNextPageUrl;
	}
	
	public String getPreviousPageUrl() {
		return mPreviousPageUrl;
	}
	
	public int getPosition() {
		return mPosition;
	}
}
