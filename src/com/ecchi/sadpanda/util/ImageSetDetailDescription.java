package com.ecchi.sadpanda.util;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageSetDetailDescription extends ImageSetDescription {

	int totalItems;

	public ImageSetDetailDescription(ImageContent inSetContent,
			String inSetName, String inSetThumbUrl, Date inSetPublished,
			int inSetScore, String inSetUploader, String inSetTorrentUrl,
			String inSetUrl, int inTotalItems) {
		super(inSetContent, inSetName, inSetThumbUrl, inSetPublished,
				inSetScore, inSetUploader, inSetTorrentUrl, inSetUrl);

		totalItems = inTotalItems;
	}

	public int getTotalItems() {
		return totalItems;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(totalItems);
	}

	public static final Parcelable.Creator<ImageSetDetailDescription> CREATOR = new Parcelable.Creator<ImageSetDetailDescription>() {
		public ImageSetDetailDescription createFromParcel(Parcel in) {
			return new ImageSetDetailDescription(in);
		}

		public ImageSetDetailDescription[] newArray(int size) {
			return new ImageSetDetailDescription[size];
		}
	};

	protected ImageSetDetailDescription(Parcel in) {
		super(in);
		totalItems = in.readInt();
	}
}
