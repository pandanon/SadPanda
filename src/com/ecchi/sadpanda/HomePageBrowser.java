package com.ecchi.sadpanda;

import org.apache.http.cookie.Cookie;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.ecchi.sadpanda.detailview.ImageSetDetailActivity;
import com.ecchi.sadpanda.detailview.ImageSetDetailFragment;
import com.ecchi.sadpanda.login.LoginFragment;
import com.ecchi.sadpanda.login.LoginTask;
import com.ecchi.sadpanda.login.LogoutTask;
import com.ecchi.sadpanda.login.OnLoggedinListener;
import com.ecchi.sadpanda.login.OnLoggedoutListener;
import com.ecchi.sadpanda.login.OnLoginRequestListener;
import com.ecchi.sadpanda.overview.ImageSetOverview;
import com.ecchi.sadpanda.overview.ImageSetOverview.Callbacks;
import com.ecchi.sadpanda.util.ClientWrapper;
import com.ecchi.sadpanda.util.ImageSetDescription;
import com.novoda.imageloader.core.ImageManager;
import com.novoda.imageloader.core.LoaderSettings;
import com.novoda.imageloader.core.cache.LruBitmapCache;
import com.novoda.imageloader.core.loader.Loader;

public class HomePageBrowser extends SherlockFragmentActivity implements
	OnLoggedinListener, OnLoginRequestListener, OnLoggedoutListener,
	Callbacks {

    private final String UA = "Exhentai Mobile/1.0";
    public static final String Domain = ".exhentai.org";
    public static final String BASE_URL = "http://exhentai.org/";

    public static ClientWrapper CLIENT;

    boolean isLoggedIn = false;
    MenuItem login;

    boolean mTwoPane = false;

    public static ImageManager mManager;
    private String searchQuery = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	final Intent intent = this.getIntent();
	if (intent != null && Intent.ACTION_SEARCH.equals(intent.getAction()))
	    searchQuery = intent.getStringExtra(SearchManager.QUERY);

	// Replace whitespaces with "+" for URL format
	if (searchQuery != null) {
	    searchQuery = searchQuery.trim().replace(" ", "+");
	}

	setContentView(R.layout.activity_imageset_list);

	final LoaderSettings settings = new LoaderSettings.SettingsBuilder()
		.withCacheManager(new LruBitmapCache(this)).build(this);

	mManager = new ImageManager(settings);

	CLIENT = ClientWrapper.newInstance(UA, this);

	if (findViewById(R.id.imageset_detail_container) != null) {
	    // The detail container view will be present only in the
	    // large-screen layouts (res/values-large and
	    // res/values-sw600dp). If this view is present, then the
	    // activity should be in two-pane mode.
	    mTwoPane = true;
	}

	if (!verifyLoggedIn()) {
	    setLoggedOut();
	    login();
	} else {
	    setLoggedIn();
	    // Load the overview fragment
	    loadFragment();
	}
    }

    public boolean isLoggedIn() {
	return isLoggedIn;
    }

    private boolean verifyLoggedIn() {
	for (final Cookie cookie : CLIENT.getCookies().getCookies()) {
	    if (cookie.getDomain().equals(Domain)) {
		return true;
	    }
	}

	return false;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

	getSupportMenuInflater().inflate(R.menu.browser_menu, menu);

	// TODO Add this to the menu xml
	login = menu.add(isLoggedIn ? "Log out" : "Log in");
	login.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
	// Get the SearchView and set the searchable configuration
	final SearchManager searchManager = (SearchManager) this
		.getSystemService(Context.SEARCH_SERVICE);
	// Get the SearchView
	final SearchView searchView = (SearchView) menu.findItem(
		R.id.action_search).getActionView();

	searchView.setSearchableInfo(searchManager.getSearchableInfo(this
		.getComponentName()));
	searchView.setIconifiedByDefault(false); // Do not iconify the widget;
						 // expand it by default

	// TODO Do something with the search
	return true;
    }

    @Override
    public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
	if (item.equals(login)) {
	    if (!isLoggedIn) {
		login();
	    } else
		new LogoutTask(CLIENT, this).execute();
	}
	return super.onMenuItemSelected(featureId, item);
    }

    private void login() {
	final LoginFragment login = new LoginFragment();
	login.setLoginRequestListener(this);
	login.show(getSupportFragmentManager(), "Login");
    }

    @Override
    public void onLoginFailed() {
	setLoggedOut();
    }

    @Override
    public void onLoggedIn() {
	setLoggedIn();
	loadFragment();
    }

    private void loadFragment() {
	final FragmentTransaction ft = getSupportFragmentManager()
		.beginTransaction();
	// remove any fragments that are currently in loading screen.
	final Fragment unneeded = getSupportFragmentManager().findFragmentById(
		R.id.imageset_list);
	if (unneeded != null && !unneeded.isDetached())
	    ft.detach(unneeded);

	final Bundle arguments = new Bundle();
	// Activity created from search
	String url;
	if (searchQuery != null) {
	    url = BASE_URL + "?f_search=" + searchQuery + "&page=";
	} else
	    url = BASE_URL + "?page=";
	// DEBUG
	Log.i(this.getClass().getName(), "URL opened: " + url);

	arguments.putString("url", url);

	final ImageSetOverview setImageOverview = new ImageSetOverview();
	setImageOverview.setArguments(arguments);
	setImageOverview.setCallbackListener(this);
	setImageOverview.setHasOptionsMenu(true);
	if (mTwoPane)
	    setImageOverview.setActivateOnItemClick(true);

	ft.replace(R.id.imageset_list, setImageOverview);
	ft.commit();
    }

    @Override
    public void onLoginRequest(final String username, final String password) {
	new LoginTask(CLIENT, this).execute(username, password);

    }

    @Override
    public void onLoggedout() {
	setLoggedOut();
    }

    @Override
    public void onItemSelected(final ImageSetDescription id) {
	if (mTwoPane) {
	    // In two-pane mode, show the detail view in this activity by
	    // adding or replacing the detail fragment using a
	    // fragment transaction.
	    final Bundle arguments = new Bundle();
	    arguments.putParcelable(ImageSetDetailFragment.ARG_ITEM_ID, id);
	    final ImageSetDetailFragment fragment = new ImageSetDetailFragment();
	    fragment.setArguments(arguments);
	    getSupportFragmentManager().beginTransaction()
		    .replace(R.id.imageset_detail_container, fragment).commit();

	} else {
	    // In single-pane mode, simply start the detail activity
	    // for the selected item ID.
	    final Intent detailIntent = new Intent(this,
		    ImageSetDetailActivity.class);
	    detailIntent.putExtra(ImageSetDetailFragment.ARG_ITEM_ID, id);
	    startActivity(detailIntent);
	}
    }

    private void setLoggedIn() {
	isLoggedIn = true;
	if (login != null)
	    login.setTitle("Log out");
    }

    private void setLoggedOut() {
	getSupportFragmentManager().beginTransaction()
		.replace(R.id.imageset_list, new SadPandaStubFragment())
		.commit();
	isLoggedIn = false;
	if (login != null)
	    login.setTitle("Log in");
    }

    public static Loader getImageLoader() {
	return mManager.getLoader();
    }
}
