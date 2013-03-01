package com.ecchi.sadpanda;

import org.apache.http.cookie.Cookie;

import login.LoginFragment;
import login.LoginTask;
import login.LogoutTask;
import login.OnLoggedinListener;
import login.OnLoggedoutListener;
import login.OnLoginRequestListener;
import tasks.LoadPandaPageTask;
import util.ClientWrapper;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class HomePageBrowser extends SherlockFragmentActivity implements
		OnLoggedinListener, OnLoginRequestListener, OnLoggedoutListener {

	private final String UA = "Exhentai Mobile/1.0";
	public static final String Domain = ".exhentai.org";
	public static final String URL = "http://exhentai.org";
	
	ClientWrapper client;
	TextView debugText;
	boolean isLoggedIn = false;
	MenuItem login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View content = View.inflate(this, R.layout.activity_home_page_browser,
				null);

		setContentView(content);

		debugText = (TextView) content.findViewById(R.id.notifytext);

		client = ClientWrapper.newInstance(UA, this);

		verifyLoggedIn();

	}

	private void verifyLoggedIn()
	{
		for(Cookie cookie: client.getCookies().getCookies())
		{
			if(cookie.getDomain().equals(Domain))
			{
				loginAndLoad();
				return;
			}
		}
		
		debugText.setText("e-hentai not logged in!");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(!isLoggedIn)
			login = menu.add("Log in");
		else
			login = menu.add("Log out");
		login.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.equals(login))
		{
			if(item.getTitle().equals("Log in"))
				new LoginFragment().show(getSupportFragmentManager(), "Login");
			else
				new LogoutTask(client, this).execute();
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onLoginFailed() {
		debugText.setText("e-hentai login failed!");
		isLoggedIn = false;
		login.setTitle("Log in");
	}

	@Override
	public void onLoggedIn() {	
		login.setTitle("Log out");
		loginAndLoad();
	}
	
	private void loginAndLoad()
	{
		debugText.setText("e-hentai login succeeded!");	
		isLoggedIn = true;
		new LoadPandaPageTask(client).execute(URL);
	}

	@Override
	public void onLoginRequest(String username, String password) {
		new LoginTask(client, this).execute(username, password);

	}

	@Override
	public void onLoggedout() {
		debugText.setText("e-hentai logout succeeded!");
		isLoggedIn = false;
		login.setTitle("Log in");
	}
}
