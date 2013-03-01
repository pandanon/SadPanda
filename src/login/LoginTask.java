package login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;

import util.ClientWrapper;
import android.os.AsyncTask;

public class LoginTask extends AsyncTask<String, Void, HttpResponse> {

	private final String login = "https://forums.e-hentai.org/index.php?act=Login&CODE=01";

	ClientWrapper client;
	OnLoggedinListener listener;

	public LoginTask(ClientWrapper client, OnLoggedinListener listener) {
		this.client = client;
		this.listener = listener;
	}

	@Override
	protected HttpResponse doInBackground(String... arg0) {
		HttpPost post = new HttpPost(login);

		String username = arg0[0];
		String pass = arg0[1];

		List<NameValuePair> login = new ArrayList<NameValuePair>(3);
		login.add(new BasicNameValuePair("UserName", username));
		login.add(new BasicNameValuePair("PassWord", pass));
		login.add(new BasicNameValuePair("CookieDate", "1"));

		try {
			post.setEntity(new UrlEncodedFormEntity(login));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			cancel(false);
			return null;
		}

		HttpResponse response = null;
		try {
			response = client.getClient().execute(post, client.getContext());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			cancel(false);
		} catch (IOException e) {
			e.printStackTrace();
			cancel(false);
		}

		if(!verifyLogin(response))
		{
			cancel(false);
		}
		
		return response;
	}

	@Override
	protected void onPostExecute(HttpResponse result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (isCancelled()) {
			listener.onLoginFailed();
			return;
		}

		Cookie[] cookies = new Cookie[client.getCookies().getCookies().size()];
		client.getCookies().getCookies().toArray(cookies);
		for (Cookie cookie : cookies) {
			if (cookie.getName().indexOf("ipb_") != -1
					|| cookie.getName().indexOf("uconfig") != -1) {
				BasicClientCookie newCookie = new BasicClientCookie(
						cookie.getName(), cookie.getValue());
				newCookie.setDomain(".exhentai.org");
				newCookie.setPath("/");
				newCookie.setAttribute("url", "http://exhentai.org");
				client.getCookies().addCookie(newCookie);
			}
		}

		listener.onLoggedIn();

	}

	private boolean verifyLogin(HttpResponse result) {
		try {
			HttpEntity entity = result.getEntity();
			InputStream is = entity.getContent();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);

			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null) {
				if (!line.contains("The following errors were found:"))
					sb.append(line);
				else
					return false;
			}

			is.close();
		} catch (Exception e) {
			return false;
		}

		return true;
	}

}
