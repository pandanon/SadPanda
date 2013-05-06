package com.ecchi.sadpanda.tasks;

public class ImageSetLink {
	final String mPreviousPageUrl;
	final String mNextPageUrl;
	final String mCurrentImageUrl;
	final String mCurrentPageUrl;
	final int mPosition;
	
	public ImageSetLink(String previousPage, String nextPage, String imageUrl, String pageUrl, int position) {
		mCurrentImageUrl = imageUrl;
		mCurrentPageUrl =pageUrl;
		mNextPageUrl = nextPage;
		mPreviousPageUrl = previousPage;
		mPosition = position;
	}
	
	public String getCurrentImageUrl() {
		return mCurrentImageUrl;
	}
	
	public String getCurrentPageUrl() {
		return mCurrentPageUrl;
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
