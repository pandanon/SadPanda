package tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;

import util.ClientWrapper;
import util.ImagePageDescription;
import android.os.AsyncTask;

public class LoadPandaPageTask extends
		AsyncTask<String, ImagePageDescription, Void> {

	ClientWrapper client;

	public LoadPandaPageTask(ClientWrapper client) {
		this.client = client;
	}

	@Override
	protected Void doInBackground(String... arg0) {

		HttpGet httpget = new HttpGet(arg0[0]); 

		try {
			HttpResponse response = client.getClient().execute(httpget, client.getContext()); 

			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY)
			{
			}
			
			HttpEntity entity = response.getEntity(); 
			InputStream is = entity.getContent();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);

			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null)
				sb.append(line + "\n");

			String resString = sb.toString(); 

			is.close(); 
			
			parseHtml(resString);
			
		} catch (Exception e) {
			e.printStackTrace();
			cancel(true);
			return null;
		}

		
		
		return null;
	}

	private void parseHtml(String html)
	{
		
	}
	
	@Override
	protected void onProgressUpdate(ImagePageDescription... values) {
		super.onProgressUpdate(values);

		// TODO: probably add shit to the adapter
	}

}
