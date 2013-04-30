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
			String url;

			int startIdx = thumbsContent[i].indexOf("url(") + 4;
			if (startIdx == 3) {
				startIdx = thumbsContent[i].indexOf("src=\"") + 5;
				int endIdx = thumbsContent[i].indexOf("\"", startIdx);
				url = thumbsContent[i].substring(startIdx, endIdx);
			} else {
				int endIdx = thumbsContent[i].indexOf(")", startIdx);
				url = thumbsContent[i].substring(startIdx, endIdx);

				// easier to cut combined thumbs into pieces and write to file
				// than to translate the imageviews
				if (i == 0) {
					((ImageSetDetailAdapter) mAdapter).getImageLoader()
							.cutBitmapToDisk(url);
				}

				url += "-" + i;
			}

			thumbUrls.add(new ImageSetThumb(url, null));
		}

		return thumbUrls;
	}
}
