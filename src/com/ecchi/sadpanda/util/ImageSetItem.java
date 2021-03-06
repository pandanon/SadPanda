package com.ecchi.sadpanda.util;

import android.os.Parcel;
import android.os.Parcelable;


public class ImageSetItem implements Parcelable {
	String mThumbUrl;
	String mImagePageUrl;
	int mHeight, mWidth;
	int mOffset;
	int mPosition;
	
	/***
	 * 
	 * @param url link to the thumbnail of the image
	 * @param imagePageUrl link to the page with contains the image
	 * @param height height of the thumbnail, if available
	 * @param position position in the entire set
	 */
	public ImageSetItem(String url, String imagePageUrl,int height, int width, int offset, int position) {
		mThumbUrl = url;
		mHeight = height;
		mWidth = width;
		mOffset = offset;
		mImagePageUrl = imagePageUrl;
		mPosition = position;
	}
	
	private ImageSetItem(Parcel in) {
		mThumbUrl = in.readString();
		mImagePageUrl = in.readString();
		mHeight = in.readInt();
		mWidth = in.readInt();
		mOffset = in.readInt();
		mPosition = in.readInt();		
	}

	public int getHeight() {
		return mHeight;
	}
	
	public int getWidth() {
		return mWidth;
	}
	
	public int getOffset() {
		return mOffset;
	}
	
	public String getThumbUrl() {
		return mThumbUrl;
	}
	
	public String getImagePageUrl() {
		return mImagePageUrl;
	}
	
	public int getPosition() {
		return mPosition;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mThumbUrl);
		dest.writeString(mImagePageUrl);
		dest.writeInt(mHeight);
		dest.writeInt(mWidth);
		dest.writeInt(mOffset);
		dest.writeInt(mPosition);
	}
	
	public static final Parcelable.Creator<ImageSetItem> CREATOR = new Parcelable.Creator<ImageSetItem>() {
		public ImageSetItem createFromParcel(Parcel in) {
			return new ImageSetItem(in);
		}

		public ImageSetItem[] newArray(int size) {
			return new ImageSetItem[size];
		}
	};
}
