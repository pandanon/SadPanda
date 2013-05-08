package com.ecchi.sadpanda.tasks;

import java.util.ArrayList;
import java.util.List;

import com.ecchi.sadpanda.util.ImageLoader;
import com.ecchi.sadpanda.util.ImageSetItem;

public class LoadDetailPageTask extends LoadPageTask<ImageSetItem> {

	public LoadDetailPageTask(OnAddPageListener<ImageSetItem> pageAdapter) {
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
			int height = -1, width = -1, offset = -1;
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

				//get offset
				startIdx = endIdx +2;
				endIdx = thumbsContent[i].indexOf("px", startIdx);
				offset = Integer.parseInt(thumbsContent[i].substring(startIdx, endIdx));
				
				//get width
				tokenIn = " width:";
				startIdx = thumbsContent[i].indexOf(tokenIn) + tokenIn.length();
				endIdx = thumbsContent[i].indexOf("px;", startIdx);
				width = Integer.parseInt(thumbsContent[i].substring(startIdx,
						endIdx));
				
				//get height
				tokenIn = " height:";
				startIdx = thumbsContent[i].indexOf(tokenIn) + tokenIn.length();
				endIdx = thumbsContent[i].indexOf("px;", startIdx);
				height = Integer.parseInt(thumbsContent[i].substring(startIdx,
						endIdx));
			}

			ImageSetItem newItem = new ImageSetItem(thumbUrl, imagePageUrl,
					height, width, offset, position);
			thumbUrls.add(newItem);
			publishProgress(newItem);
		}

		return thumbUrls;
	}
}
