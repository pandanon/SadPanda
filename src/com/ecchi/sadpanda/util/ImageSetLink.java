package com.ecchi.sadpanda.util;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageSetLink implements Parcelable {
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
	
	private ImageSetLink(Parcel in) {
		mPreviousPageUrl = in.readString();
		mNextPageUrl = in.readString();
		mCurrentImageUrl = in.readString();
		mCurrentPageUrl = in.readString();
		mPosition = in.readInt();
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

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mPreviousPageUrl);
		dest.writeString(mNextPageUrl);
		dest.writeString(mCurrentImageUrl);
		dest.writeString(mCurrentPageUrl);
		dest.writeInt(mPosition);
	}
	
	public static final Parcelable.Creator<ImageSetLink> CREATOR = new Parcelable.Creator<ImageSetLink>() {
		public ImageSetLink createFromParcel(Parcel in) {
			return new ImageSetLink(in);
		}

		public ImageSetLink[] newArray(int size) {
			return new ImageSetLink[size];
		}
	};
}
