package com.ecchi.sadpanda.tasks;

import android.os.AsyncTask;

import com.ecchi.sadpanda.util.HtmlManipulator;
import com.ecchi.sadpanda.util.ImageSetLink;
import com.ecchi.sadpanda.util.Utils;

public class LoadImageLinkTask extends AsyncTask<String, Void, ImageSetLink> {

	public interface ImageSetViewer {
		public void addImageSetLink(ImageSetLink link);
	}
	
	ImageSetViewer mViewer;
	int mPosition;

	public LoadImageLinkTask(ImageSetViewer viewer, int position) {
		mPosition = position;
		mViewer = viewer;
	}

	@Override
	protected ImageSetLink doInBackground(String... params) {		
		String imageUrl = null;
		String previousUrl = null;
		String nextUrl = null;
		
		String result = Utils.LoadSadPandaLink(params[0]);
		
		String token = "id=\"prev\"";
		int startIdx = result.indexOf(token);
		token = "href=\"";
		startIdx = result.indexOf(token, startIdx) + token.length();
		int endIdx = result.indexOf("\"", startIdx);
		previousUrl = result.substring(startIdx, endIdx);
		
		token = "id=\"next\"";
		startIdx = result.indexOf(token);
		token = "href=\"";
		startIdx = result.indexOf(token, startIdx) + token.length();
		endIdx = result.indexOf("\"", startIdx);
		nextUrl = result.substring(startIdx, endIdx);
		
		token = "<div id=\"i3\">";
		startIdx = result.indexOf(token);
		token = "src=\"";
		startIdx = result.indexOf(token, startIdx) + token.length();			
		endIdx = result.indexOf("\"", startIdx);
		
		imageUrl = HtmlManipulator.replaceHtmlEntities(result.substring(startIdx, endIdx));			
		
		return new ImageSetLink(previousUrl, nextUrl, imageUrl, params[0], mPosition);
	}

	@Override
	protected void onPostExecute(ImageSetLink result) {
		super.onPostExecute(result);

		mViewer.addImageSetLink(result);
	}
}
