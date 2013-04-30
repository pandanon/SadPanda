package com.ecchi.sadpanda.tasks;

import java.util.List;

import com.ecchi.sadpanda.detailview.ImageSetDetailAdapter;
import com.ecchi.sadpanda.util.ImageSetDescription.ImageContent;
import com.ecchi.sadpanda.util.ImageSetDetailDescription;
import com.ecchi.sadpanda.util.ImageSetThumb;

public class LoadDetailDescriptionPageTask extends LoadDetailPageTask {

	ImageSetDetailDescription mDetailDescription;

	public LoadDetailDescriptionPageTask(ImageSetDetailAdapter pageAdapter) {
		super(pageAdapter);
	}

	@Override
	protected List<ImageSetThumb> parseHtmlContent(String content) {
		String token = "class=\"ip\">";
		int startIdx = content.indexOf(token) + token.length();
		int endIdx = content.indexOf(" images", startIdx);
		String[] imageNumber = content.substring(startIdx, endIdx).split(" ");
		int totalPages = Integer.parseInt(imageNumber[imageNumber.length - 1])/20 + 1;

		mDetailDescription = new ImageSetDetailDescription(ImageContent.Misc, null, null,
				null, 0, null, null, null, totalPages);

		return super.parseHtmlContent(content);
	}

	@Override
	protected void onPostExecute(List<ImageSetThumb> result) {

		if (mDetailDescription != null)
			((ImageSetDetailAdapter) mAdapter)
					.setImageSetDetailDescription(mDetailDescription);

		super.onPostExecute(result);
	}
}
