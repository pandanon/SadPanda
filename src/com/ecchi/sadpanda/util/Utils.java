package com.ecchi.sadpanda.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import com.ecchi.sadpanda.HomePageBrowser;

public class Utils {
	public static String LoadSadPandaLink(String url) {
		ClientWrapper client = HomePageBrowser.CLIENT;

		HttpGet httpget = new HttpGet(url);
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

			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			resString = sb.toString();

			is.close();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return resString;
	}
}
