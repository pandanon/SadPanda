package com.ecchi.sadpanda.util;

import android.os.Parcel;
import android.os.Parcelable;


public class ImageSetThumb implements Parcelable {
	String mThumbUrl;
	String mImagePageUrl;
	String mImageLinkUrl;
	int mHeight;
	int mPosition;
	
	public ImageSetThumb(String url, String imagePageUrl,int height, int position) {
		mThumbUrl = url;
		mHeight = height;
		mImagePageUrl = imagePageUrl;
		mPosition = position;
	}
	
	private ImageSetThumb(Parcel in) {
		mThumbUrl = in.readString();
		mImagePageUrl = in.readString();
		mHeight = in.readInt();
		mPosition = in.readInt();
		String tempLink = in.readString();
		if(!tempLink.equals(""))
			mImageLinkUrl = tempLink;
	}

	public int getHeight() {
		return mHeight;
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
	
	public String getImageLinkUrl() {
		return mImageLinkUrl;
	}
	
	public void setImageLinkUrl(String imageLinkUrl) {
		mImageLinkUrl = imageLinkUrl;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mThumbUrl);
		dest.writeString(mImagePageUrl);
		dest.writeInt(mHeight);
		dest.writeInt(mPosition);
		dest.writeString(mImageLinkUrl==null?"":mImageLinkUrl);
	}
	
	public static final Parcelable.Creator<ImageSetThumb> CREATOR = new Parcelable.Creator<ImageSetThumb>() {
		public ImageSetThumb createFromParcel(Parcel in) {
			return new ImageSetThumb(in);
		}

		public ImageSetThumb[] newArray(int size) {
			return new ImageSetThumb[size];
		}
	};
}
