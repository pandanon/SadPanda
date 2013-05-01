package com.ecchi.sadpanda.util;


public class ImageSetThumb {
	String mThumbUrl;
	int mHeight;
	
	public ImageSetThumb(String url, int height) {
		mThumbUrl = url;
		mHeight = height;
	}
	
	public int getHeight() {
		return mHeight;
	}
	
	public String getThumbUrl() {
		return mThumbUrl;
	}
}
