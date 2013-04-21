package com.ecchi.sadpanda;

import login.LoginFragment;
import login.LoginTask;
import login.LogoutTask;
import login.OnLoggedinListener;
import login.OnLoggedoutListener;
import login.OnLoginRequestListener;

import org.apache.http.cookie.Cookie;

import util.ClientWrapper;
import util.ImageSetDescription;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ecchi.sadpanda.ImageSetOverview.Callbacks;

import dualpaneoverview.ImageSetDetailActivity;
import dualpaneoverview.ImageSetDetailFragment;

public class HomePageBrowser extends SherlockFragmentActivity implements
		OnLoggedinListener, OnLoginRequestListener, OnLoggedoutListener, Callbacks {

	private final String UA = "Exhentai Mobile/1.0";
	public static final String Domain = ".exhentai.org";
	public static final String URL = "http://exhentai.org/?page=";
	
	public static ClientWrapper CLIENT;
	boolean isLoggedIn = false;
	MenuItem login;
	
	boolean mTwoPane = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_imageset_list);

		CLIENT = ClientWrapper.newInstance(UA, this);
		
		if (findViewById(R.id.imageset_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;
		}

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
		Fragment unneeded = getSupportFragmentManager().findFragmentById(R.id.imageset_list);
		if(unneeded != null && !unneeded.isDetached())
			ft.remove(unneeded);
		
		Bundle arguments = new Bundle();
		arguments.putString("url", URL);
		
		ImageSetOverview setImageOverview = new ImageSetOverview();
		setImageOverview.setArguments(arguments);
		setImageOverview.setCallbackListener(this);
		if(mTwoPane)
			setImageOverview.setActivateOnItemClick(true);
		
		ft.replace(R.id.imageset_list, setImageOverview);
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

	@Override
	public void onItemSelected(ImageSetDescription id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();			
			arguments.putParcelable(ImageSetDetailFragment.ARG_ITEM_ID, id);
			ImageSetDetailFragment fragment = new ImageSetDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.imageset_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, ImageSetDetailActivity.class);
			detailIntent.putExtra(ImageSetDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}		
	}
}
