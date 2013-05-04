package com.ecchi.sadpanda.tasks;

import java.util.List;

import com.ecchi.sadpanda.util.ImageSetDescription;
import com.ecchi.sadpanda.util.ImageSetDetailDescription;
import com.ecchi.sadpanda.util.ImageSetItem;

public class LoadDetailDescriptionPageTask extends LoadDetailPageTask {

	public interface ImageDescriptionContainer {
		public void setImageSetDetailDescription(
				ImageSetDetailDescription description);
	}

	ImageSetDetailDescription mDetailDescription;
	ImageDescriptionContainer mContainer;
	ImageSetDescription mExistingDescription;

	public LoadDetailDescriptionPageTask(ImageContainer pageAdapter,
			ImageDescriptionContainer container, ImageSetDescription description) {
		super(pageAdapter);
		mContainer = container;
		mExistingDescription = description;
	}

	@Override
	protected List<ImageSetItem> parseHtmlContent(String content) {
		String token = "class=\"ip\">";
		int startIdx = content.indexOf(token) + token.length();
		int endIdx = content.indexOf(" images", startIdx);
		String[] imageNumber = content.substring(startIdx, endIdx).split(" ");
		int totalPages = Integer.parseInt(imageNumber[imageNumber.length - 1]) / 20 + 1;

		copyExistingDescription(totalPages);

		return super.parseHtmlContent(content);
	}

	public void copyExistingDescription(int totalPages) {
		mDetailDescription = new ImageSetDetailDescription(
				mExistingDescription.getSetContent(),
				mExistingDescription.getSetName(),
				mExistingDescription.getSetThumbUrl(),
				mExistingDescription.getSetPublished(),
				mExistingDescription.getSetScore(),
				mExistingDescription.getSetUploader(),
				mExistingDescription.getSetTorrentUrl(),
				mExistingDescription.getSetUrl(), 
				totalPages);
	}

	@Override
	protected void onPostExecute(List<ImageSetItem> result) {

		if (mDetailDescription != null)
			mContainer.setImageSetDetailDescription(mDetailDescription);

		super.onPostExecute(result);
	}
}
