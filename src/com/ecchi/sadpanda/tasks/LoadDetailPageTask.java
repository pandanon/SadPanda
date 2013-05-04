package com.ecchi.sadpanda.tasks;

import java.util.ArrayList;
import java.util.List;

import com.ecchi.sadpanda.util.ImageLoader;
import com.ecchi.sadpanda.util.ImageSetItem;
import com.ecchi.sadpanda.util.OnAddPageListener;

public class LoadDetailPageTask extends LoadPageTask<ImageSetItem> {

	public interface ImageContainer extends OnAddPageListener<ImageSetItem> {
		public ImageLoader getImageLoader();
	}
	
	public LoadDetailPageTask(ImageContainer pageAdapter) {
		super(pageAdapter);
	}

	@Override
	protected List<ImageSetItem> parseHtmlContent(String content) {

		String token = "class=\"gdtm";
		String strippedContent = content.substring(content.indexOf(token)
				+ token.length());
		String[] thumbsContent = strippedContent.split(token);

		List<ImageSetItem> thumbUrls = new ArrayList<ImageSetItem>();

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
					((ImageContainer) mAdapter).getImageLoader()
							.cutBitmapToDisk(thumbUrl);
				}

				thumbUrl += "-" + i;
			}

			ImageSetItem newItem = new ImageSetItem(thumbUrl, imagePageUrl, height, i);
			thumbUrls.add(newItem);
			publishProgress(newItem);
		}

		return thumbUrls;
	}
}
