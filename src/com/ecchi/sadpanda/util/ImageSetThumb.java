package com.ecchi.sadpanda.util;

import android.graphics.Matrix;

public class ImageSetThumb {
	String thumbUrl;
	Matrix drawableMatrix;
	
	public ImageSetThumb(String url, Matrix translation) {
		thumbUrl = url;
		drawableMatrix = translation;
	}
	
	public Matrix getDrawableMatrix() {
		return drawableMatrix;
	}
	
	public String getThumbUrl() {
		return thumbUrl;
	}
}
