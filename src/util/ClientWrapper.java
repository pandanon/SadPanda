package util;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.net.http.AndroidHttpClient;

import com.loopj.android.http.PersistentCookieStore;

public class ClientWrapper {
	HttpClient client;
	HttpContext context;
	CookieStore cookies;

	private ClientWrapper(HttpClient client, HttpContext context,
			CookieStore cookies) {
		this.client = client;
		this.context = context;
		this.cookies = cookies;
	}

	public HttpClient getClient() {
		return client;
	}

	public HttpContext getContext() {
		return context;
	}

	public CookieStore getCookies() {
		return cookies;
	}


	public static ClientWrapper newInstance(String userAgent, Context context) {
		HttpClient client = AndroidHttpClient.newInstance(userAgent, context);
		HttpContext httpContext = new BasicHttpContext();		
		CookieStore cookies = new PersistentCookieStore(context);

		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookies);

		return new ClientWrapper(client, httpContext, cookies);
	}
}
