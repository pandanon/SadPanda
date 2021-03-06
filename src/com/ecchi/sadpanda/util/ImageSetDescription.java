package com.ecchi.sadpanda.util;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Container class for all of sadPanda's image sets.
 * 
 * @author Alex
 * 
 */
public class ImageSetDescription implements Parcelable {

	final ImageContent setContent;
	final String setName;
	final String setUrl;
	final String setThumbUrl;
	final Date setPublished;
	final int setScore;
	final String setUploader;
	final String setTorrentUrl;

	/***
	 * 
	 * @param inSetContent Enumeration of the set's content
	 * @param inSetName Name of the set
	 * @param inSetThumbUrl Thumbnail that displays alongside the set
	 * @param inSetPublished date the set was posted
	 * @param inSetScore score the set has received
	 * @param inSetUploader user that uploaded the set 
	 * @param inSetTorrentUrl optional link to the torrent of the set
	 * @param inSetUrl link to the set itself
	 */
	public ImageSetDescription(ImageContent inSetContent, String inSetName,
			String inSetThumbUrl, Date inSetPublished, int inSetScore,
			String inSetUploader, String inSetTorrentUrl, String inSetUrl) {
		this.setContent = inSetContent;
		this.setName = inSetName;
		this.setThumbUrl = inSetThumbUrl;
		this.setPublished = inSetPublished;
		this.setScore = inSetScore;
		this.setUploader = inSetUploader;
		this.setTorrentUrl = inSetTorrentUrl;
		this.setUrl = inSetUrl;
	}

	public ImageContent getSetContent() {
		return setContent;
	}

	public String getSetName() {
		return setName;
	}

	public String getSetThumbUrl() {
		return setThumbUrl;
	}

	public Date getSetPublished() {
		return setPublished;
	}

	public int getSetScore() {
		return setScore;
	}

	public String getSetUploader() {
		return setUploader;
	}

	public String getSetTorrentUrl() {
		return setTorrentUrl;
	}

	public String getSetUrl() {
		return setUrl;
	}

	public static enum ImageContent {
		ImageSet, Doujinshi, Western, Manga, NonH, Cosplay, Misc, AsianPorn, GameCG, ArtistCG;

		public static ImageContent parseContent(String content) {
			if (content.equals("imageset"))
				return ImageSet;
			else if (content.equals("doujinshi"))
				return Doujinshi;
			else if (content.equals("manga"))
				return Manga;
			else if (content.equals("artistcg"))
				return ArtistCG;
			else if (content.equals("gamecg"))
				return GameCG;
			else if (content.equals("western"))
				return Western;
			else if (content.equals("non-h"))
				return NonH;
			else if (content.equals("cosplay"))
				return Cosplay;
			else if (content.equals("asianporn"))
				return AsianPorn;
			else
				return Misc;
		}

		public static int getColor(ImageContent content) {
			int out;

			switch (content) {
			case NonH:
				out = 0xFF80E6FF;
				break;
			case Doujinshi:
				out = 0xFFFF3333;
				break;
			case GameCG:
				out = 0xFF009933;
				break;
			case Manga:
				out = 0xFFFF9933;
				break;
			case Western:
				out = 0xFF66FF66;
				break;
			case ImageSet:
				out = 0xFF0066FF;
				break;
			case ArtistCG:
				out = 0xFFFFFF00;
				break;
			case Cosplay:
				out = 0xFF993399;
				break;
			case AsianPorn:
				out = 0xFFFF99FF;
				break;
			default:
				out = 0xFFE2E2E2;
				break;
			}

			return out;
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt((int) setContent.ordinal());
		dest.writeString(setName);
		dest.writeString(setThumbUrl);
		dest.writeLong(setPublished.getTime());
		dest.writeInt(setScore);
		dest.writeString(setUploader);
		dest.writeString(setTorrentUrl);
		dest.writeString(setUrl);
	}

	public static final Parcelable.Creator<ImageSetDescription> CREATOR = new Parcelable.Creator<ImageSetDescription>() {
		public ImageSetDescription createFromParcel(Parcel in) {
			return new ImageSetDescription(in);
		}

		public ImageSetDescription[] newArray(int size) {
			return new ImageSetDescription[size];
		}
	};

	protected ImageSetDescription(Parcel in) {
		setContent = ImageContent.values()[in.readInt()];
		setName = in.readString();

		setThumbUrl = in.readString();
		setPublished = new Date(in.readLong());
		setScore = in.readInt();
		setUploader = in.readString();
		setTorrentUrl = in.readString();
		setUrl = in.readString();
	}
}
