package login;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;

import util.ClientWrapper;
import android.os.AsyncTask;

public class LogoutTask extends AsyncTask<Void, Void, Void> {

	private final String logoutURL = "https://forums.e-hentai.org/index.php?act=Login&CODE=03&k=cabd579871bc89de01a8d057b75bfd19";
	ClientWrapper client;
	OnLoggedoutListener listener;

	public LogoutTask(ClientWrapper client, OnLoggedoutListener listener) {
		this.client = client;
		this.listener = listener;
	}

	@Override
	protected Void doInBackground(Void... params) {

		HttpGet logout = new HttpGet(logoutURL);

		try {
			client.getClient().execute(logout, client.getContext());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		client.getCookies().clear();
		
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		
		listener.onLoggedout();
	}

}
