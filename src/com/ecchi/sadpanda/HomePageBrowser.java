package com.ecchi.sadpanda;

import login.LoginFragment;
import login.LoginTask;
import login.LogoutTask;
import login.OnLoggedinListener;
import login.OnLoggedoutListener;
import login.OnLoginRequestListener;

import org.apache.http.cookie.Cookie;

import util.ClientWrapper;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class HomePageBrowser extends SherlockFragmentActivity implements
		OnLoggedinListener, OnLoginRequestListener, OnLoggedoutListener {

	private final String UA = "Exhentai Mobile/1.0";
	public static final String Domain = ".exhentai.org";
	public static final String URL = "http://exhentai.org/?page=";
	
	public static ClientWrapper CLIENT;
	boolean isLoggedIn = false;
	MenuItem login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.sad_panda_home_layout);

		CLIENT = ClientWrapper.newInstance(UA, this);

		verifyLoggedIn();

	}

	public boolean isLoggedIn() {
		return isLoggedIn;
	}
	
	private void verifyLoggedIn()
	{
		for(Cookie cookie: CLIENT.getCookies().getCookies())
		{
			if(cookie.getDomain().equals(Domain))
			{
				loginAndLoad();
				return;
			}
		}
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
			{
				LoginFragment login = new LoginFragment();
				login.setLoginRequestListener(this);
				login.show(getSupportFragmentManager(), "Login");
			}
			else
				new LogoutTask(CLIENT, this).execute();
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onLoginFailed() {
		isLoggedIn = false;
		login.setTitle("Log in");
		//TODO: display sadpanda
	}

	@Override
	public void onLoggedIn() {	
		login.setTitle("Log out");
		loginAndLoad();
	}
	
	private void loginAndLoad()
	{
		isLoggedIn = true;
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		//remove any fragments that are currently in loading screen.
		Fragment unneeded = getSupportFragmentManager().findFragmentById(R.id.setImageOverview);
		if(unneeded != null && !unneeded.isDetached())
			ft.remove(unneeded);
		
		Bundle arguments = new Bundle();
		arguments.putString("url", URL);
		
		Fragment setImageOverview = new ImageSetOverview();
		setImageOverview.setArguments(arguments);
		
		ft.replace(R.id.setImageOverview, setImageOverview);
		ft.commit();
		
		//TODO: allow fragment(s) to load
		//new LoadPandaPageTask(CLIENT).execute(URL);
	}

	@Override
	public void onLoginRequest(String username, String password) {
		new LoginTask(CLIENT, this).execute(username, password);

	}

	@Override
	public void onLoggedout() {
		isLoggedIn = false;
		login.setTitle("Log in");
	}
}
