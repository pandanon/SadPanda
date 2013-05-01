package com.ecchi.sadpanda.util;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageSetDetailDescription extends ImageSetDescription {

	int totalPages;

	public ImageSetDetailDescription(ImageContent inSetContent,
			String inSetName, String inSetThumbUrl, Date inSetPublished,
			int inSetScore, String inSetUploader, String inSetTorrentUrl,
			String inSetUrl, int inTotalPages) {
		super(inSetContent, inSetName, inSetThumbUrl, inSetPublished,
				inSetScore, inSetUploader, inSetTorrentUrl, inSetUrl);

		totalPages = inTotalPages;
	}

	public int getTotalPages() {
		return totalPages;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(totalPages);
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
		totalPages = in.readInt();
	}
}
