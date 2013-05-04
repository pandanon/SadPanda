package com.ecchi.sadpanda.tasks;

import java.util.ArrayList;
import java.util.List;

import com.ecchi.sadpanda.detailview.ImageSetDetailAdapter;
import com.ecchi.sadpanda.util.ImageSetThumb;

public class LoadDetailPageTask extends LoadPageTask<ImageSetThumb> {

	public LoadDetailPageTask(ImageSetDetailAdapter pageAdapter) {
		super(pageAdapter);
	}

	@Override
	protected List<ImageSetThumb> parseHtmlContent(String content) {

		String token = "class=\"gdtm";
		String strippedContent = content.substring(content.indexOf(token)
				+ token.length());
		String[] thumbsContent = strippedContent.split(token);

		List<ImageSetThumb> thumbUrls = new ArrayList<ImageSetThumb>();

		for (int i = 0; i < thumbsContent.length; i++) {
			String thumbUrl;
			int height = -1;
			token = "href=\"";
			int startIdx = thumbsContent[i].indexOf(token) + token.length();
			int endIdx = thumbsContent[i].indexOf("\"", startIdx);
			String imagePageUrl = thumbsContent[i].substring(startIdx, endIdx);
			
			token = "url(";
			startIdx = thumbsContent[i].indexOf(token) + token.length();
			if (startIdx == 3) {
				startIdx = thumbsContent[i].indexOf("src=\"") + 5;
				endIdx = thumbsContent[i].indexOf("\"", startIdx);
				thumbUrl = thumbsContent[i].substring(startIdx, endIdx);
			} else {
				endIdx = thumbsContent[i].indexOf(")", startIdx);
				thumbUrl = thumbsContent[i].substring(startIdx, endIdx);

				startIdx = thumbsContent[i].indexOf(" height:") + 8;
				endIdx = thumbsContent[i].indexOf("px;", startIdx);
				height = Integer.parseInt(thumbsContent[i].substring(startIdx,
						endIdx));

				// easier to cut combined thumbs into pieces and write to file
				// than to translate the imageviews
				if (i == 0) {
					((ImageSetDetailAdapter) mAdapter).getImageLoader()
							.cutBitmapToDisk(thumbUrl);
				}

				thumbUrl += "-" + i;
			}

			thumbUrls.add(new ImageSetThumb(thumbUrl, imagePageUrl, height, i));
		}

		return thumbUrls;
	}
}
