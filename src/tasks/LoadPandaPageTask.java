package tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import util.ClientWrapper;
import util.HtmlManipulator;
import util.ImageSetDescription;
import util.ImageSetDescription.ImageContent;
import util.ImageSetOverviewAdapter;
import util.ThumbnailLoader.PageThumbLoader;
import android.os.AsyncTask;

import com.ecchi.sadpanda.HomePageBrowser;

public class LoadPandaPageTask extends
		AsyncTask<String, ImageSetDescription, List<ImageSetDescription>> {

	ClientWrapper client;
	ImageSetOverviewAdapter adapter;

	public LoadPandaPageTask(ImageSetOverviewAdapter adapter) {
		this.client = HomePageBrowser.CLIENT;
		this.adapter = adapter;
	}

	@Override
	protected List<ImageSetDescription> doInBackground(String... arg0) {

		HttpGet httpget = new HttpGet(arg0[0]);
		httpget.addHeader("Accept-charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.3");
		httpget.addHeader("Accept-encoding", "gzip,deflate,sdch");
		String resString = null;

		try {
			HttpResponse response = client.getClient().execute(httpget,
					client.getContext());

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
			}

			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new GZIPInputStream(is), "UTF-8"), 8);

			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null)
			{
				sb.append(line);
			}

			resString = sb.toString();

			is.close();

		} catch (Exception e) {
			e.printStackTrace();
			cancel(true);
			return null;
		}

		return parseHtml(HtmlManipulator.replaceHtmlEntities(resString));
	}

	private List<ImageSetDescription> parseHtml(String html) {
		if (!(html != null && html.length() > 0))
			return null;

		List<ImageSetDescription> imageSets = new ArrayList<ImageSetDescription>();

		SimpleDateFormat commentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm",
				Locale.ENGLISH);

		String[] imageSet = html.split("class=\"gtr");

		for (int i = 1; i < imageSet.length; i++) {
			int idxStart = -1;
			int idxEnd = -1;

			// content
			String token = "alt=\"";
			idxStart = imageSet[i].indexOf(token) + token.length();
			idxEnd = imageSet[i].indexOf("\"", idxStart);
			ImageContent setContent = ImageContent.parseContent(imageSet[i]
					.substring(idxStart, idxEnd));

			// published
			token = "nowrap\">";
			idxStart = imageSet[i].indexOf(token, idxStart) + token.length();
			idxEnd = imageSet[i].indexOf("</td>", idxStart);
			Date setPublished;
			try {
				setPublished = commentDate.parse(imageSet[i].substring(
						idxStart, idxEnd));
			} catch (ParseException e) {
				setPublished = null;
			}

			// thumb
			token = "class=\"it2";
			idxStart = imageSet[i].indexOf(token, idxStart) + token.length();
			token = "src=\"";
			int tempIdxStart = imageSet[i].indexOf(token, idxStart)
					+ token.length();
			String setThumbUrl = null;
			int tempIdxEnd = imageSet[i].indexOf("</div>", idxStart);
			
			if (tempIdxStart > tempIdxEnd || tempIdxStart == token.length() - 1) {
				token = "~";
				int temp2IdxStart = imageSet[i].indexOf(token, idxStart) + token.length();
				int temp3IdxStart = imageSet[i].indexOf(token, temp2IdxStart) + token.length();
				tempIdxEnd = imageSet[i].indexOf("~", temp3IdxStart);
				String tempThumbUrl = imageSet[i].substring(temp2IdxStart, tempIdxEnd);
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
				token = "href";
				idxStart = imageSet[i].indexOf(token, idxStart)
						+ token.length();
			}

			// name
			token = ">";
			idxStart = imageSet[i].indexOf(token, idxStart) + token.length();
			idxEnd = imageSet[i].indexOf("</a>", idxStart);
			String setName = imageSet[i].substring(idxStart, idxEnd);

			// score
			token = "img/r/";
			idxStart = imageSet[i].indexOf(token, idxStart) + token.length();
			idxEnd = imageSet[i].indexOf(".gif", idxStart);
			int score = Integer.parseInt(imageSet[i]
					.substring(idxStart, idxEnd));

			// uploader
			token = "href";
			idxStart = imageSet[i].indexOf(token, idxStart) + token.length();
			token = ">";
			idxStart = imageSet[i].indexOf(token, idxStart) + token.length();
			idxEnd = imageSet[i].indexOf("</a>", idxStart);
			String setUploader = imageSet[i].substring(idxStart, idxEnd);

			imageSets.add(new ImageSetDescription(setContent, setName,
					setThumbUrl, setPublished, score, setUploader,
					setTorrentUrl));
		}

		return imageSets;
	}

	@Override
	protected void onPostExecute(List<ImageSetDescription> result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);

		if (!(result != null && result.size() > 0))
			return;

		adapter.addItems(result);
		
		adapter.getThumbNailLoader().new PageThumbLoader(adapter, result).execute(); 
	}

}
