package com.ecchi.sadpanda.util;

import java.util.Date;

public class ImageSetDetailDescription extends ImageSetDescription {

	int totalPages; 
	
	public ImageSetDetailDescription(ImageContent inSetContent,
			String inSetName, String inSetThumbUrl, Date inSetPublished,
			int inSetScore, String inSetUploader, String inSetTorrentUrl,
			String inSetUrl, int inTotalPages) {
		super(inSetContent, inSetName, inSetThumbUrl, inSetPublished, inSetScore,
				inSetUploader, inSetTorrentUrl, inSetUrl);
		
		totalPages = inTotalPages; 
	}

	public int getTotalPages() {
		return totalPages;
	}
}
