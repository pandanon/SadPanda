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
			int position = -1;

			String tokenIn = "href=\"";
			int startIdx = thumbsContent[i].indexOf(tokenIn) + tokenIn.length();
			int endIdx = thumbsContent[i].indexOf("\"", startIdx);
			String imagePageUrl = thumbsContent[i].substring(startIdx, endIdx);

			//retrieves the thumbnail position in the set from the link
			position = Integer.parseInt(imagePageUrl.substring(imagePageUrl
					.lastIndexOf("-") + 1));

			tokenIn = "url(";
			startIdx = thumbsContent[i].indexOf(tokenIn) + tokenIn.length();
			if (startIdx == 3) {
				tokenIn = "src=\"";
				startIdx = thumbsContent[i].indexOf(tokenIn) + tokenIn.length();
				endIdx = thumbsContent[i].indexOf("\"", startIdx);
				thumbUrl = thumbsContent[i].substring(startIdx, endIdx);
			} else {
				endIdx = thumbsContent[i].indexOf(")", startIdx);
				thumbUrl = thumbsContent[i].substring(startIdx, endIdx);

				tokenIn = " height:";
				startIdx = thumbsContent[i].indexOf(tokenIn) + tokenIn.length();
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

			ImageSetItem newItem = new ImageSetItem(thumbUrl, imagePageUrl,
					height, position);
			thumbUrls.add(newItem);
			publishProgress(newItem);
		}

		return thumbUrls;
	}
}
