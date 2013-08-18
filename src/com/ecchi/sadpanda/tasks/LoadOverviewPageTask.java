package com.ecchi.sadpanda.tasks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.ecchi.sadpanda.util.ImageSetDescription;
import com.ecchi.sadpanda.util.ImageSetDescription.ImageContent;
import com.ecchi.sadpanda.util.PagedScrollAdapter;

public class LoadOverviewPageTask extends LoadPageTask<ImageSetDescription> {

	public LoadOverviewPageTask(
			final PagedScrollAdapter<ImageSetDescription> pageAdapter) {
		super(pageAdapter);
	}

	@Override
	protected List<ImageSetDescription> parseHtmlContent(final String content) {
		if (!(content != null && content.length() > 0))
			return null;

		final List<ImageSetDescription> imageSets = new ArrayList<ImageSetDescription>();

		final SimpleDateFormat commentDate = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm", Locale.ENGLISH);

		final String[] imageSet = content.split("class=\"gtr");

		for (int i = 1; i < imageSet.length; i++) {
			int idxStart = -1;
			int idxEnd = -1;

			// content
			String token = "alt=\"";
			idxStart = imageSet[i].indexOf(token) + token.length();
			idxEnd = imageSet[i].indexOf("\"", idxStart);
			final ImageContent setContent = ImageContent
					.parseContent(imageSet[i].substring(idxStart, idxEnd));

			// published
			token = "nowrap\">";
			idxStart = imageSet[i].indexOf(token, idxStart) + token.length();
			idxEnd = imageSet[i].indexOf("</td>", idxStart);
			Date setPublished;
			try {
				setPublished = commentDate.parse(imageSet[i].substring(
						idxStart, idxEnd));
			} catch (final ParseException e) {
				setPublished = null;
			}

			// thumb
			token = "class=\"it2";
			idxStart = imageSet[i].indexOf(token, idxStart) + token.length();
			token = "src=\"";
			final int tempIdxStart = imageSet[i].indexOf(token, idxStart)
					+ token.length();
			String setThumbUrl = null;
			int tempIdxEnd = imageSet[i].indexOf("</div>", idxStart);

			if (tempIdxStart > tempIdxEnd || tempIdxStart == token.length() - 1) {
				token = "~";
				final int temp2IdxStart = imageSet[i].indexOf(token, idxStart)
						+ token.length();
				final int temp3IdxStart = imageSet[i].indexOf(token,
						temp2IdxStart) + token.length();
				tempIdxEnd = imageSet[i].indexOf("~", temp3IdxStart);
				final String tempThumbUrl = imageSet[i].substring(
						temp2IdxStart, tempIdxEnd);
				setThumbUrl = "http://" + tempThumbUrl.replace("~", "/");
			} else {
				idxStart = tempIdxStart;
				idxEnd = imageSet[i].indexOf("\"", idxStart);
				setThumbUrl = imageSet[i].substring(idxStart, idxEnd);
			}

			// torrent url
			token = "href=\"";
			idxStart = imageSet[i].indexOf(token, idxStart) + token.length();
			idxEnd = imageSet[i].indexOf("</div>", idxStart);
			String setTorrentUrl = null;
			if (imageSet[i].substring(idxStart, idxEnd).contains(".php")) {

				idxEnd = imageSet[i].indexOf("\"", idxStart);
				setTorrentUrl = imageSet[i].substring(idxStart, idxEnd);

				// make ready for name lookup
				token = "href=\"";
				idxStart = imageSet[i].indexOf(token, idxStart)
						+ token.length();
			}

			// set url
			idxEnd = imageSet[i].indexOf("\"", idxStart);
			final String setUrl = imageSet[i].substring(idxStart, idxEnd);

			// name
			token = ">";
			idxStart = imageSet[i].indexOf(token, idxStart) + token.length();
			idxEnd = imageSet[i].indexOf("</a>", idxStart);
			final String setName = imageSet[i].substring(idxStart, idxEnd);

			// score
			token = "img/r/";

			// FIXME Not a number exception, hardcoding score to 1 "fixed
			// idxStart = imageSet[i].indexOf(token, idxStart) + token.length();
			// idxEnd = imageSet[i].indexOf(".gif", idxStart);
			// int score = Integer.parseInt(imageSet[i]
			// .substring(idxStart, idxEnd));
			final int score = 1;

			// uploader
			token = "href";
			idxStart = imageSet[i].indexOf(token, idxStart) + token.length();
			token = ">";
			idxStart = imageSet[i].indexOf(token, idxStart) + token.length();
			idxEnd = imageSet[i].indexOf("</a>", idxStart);
			final String setUploader = imageSet[i].substring(idxStart, idxEnd);

			imageSets.add(new ImageSetDescription(setContent, setName,
					setThumbUrl, setPublished, score, setUploader,
					setTorrentUrl, setUrl));
		}

		return imageSets;
	}

}
