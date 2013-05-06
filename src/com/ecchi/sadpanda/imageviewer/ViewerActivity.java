package com.ecchi.sadpanda.imageviewer;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ecchi.sadpanda.R;
import com.ecchi.sadpanda.imageviewer.ViewerAdapter.ImageSetLinkDatabase;
import com.ecchi.sadpanda.imageviewer.ViewerAdapter.ViewPagerChildFinder;
import com.ecchi.sadpanda.tasks.LoadImageLinkTask;
import com.ecchi.sadpanda.util.ImageSetItem;
import com.ecchi.sadpanda.util.ImageSetLink;

public class ViewerActivity extends SherlockActivity implements
		ViewPagerChildFinder, ImageSetLinkDatabase {
	public static final String START_URL_KEY = "start_url";
	public static final String START_POSITION_KEY = "start_position";
	public static final String SIZE_KEY = "size_key";

	static final int retainedPages = 3;
	static final int refreshId = 480063552;
	static final String IMAGE_SET_DATA = "image_set_data";
	static final String STORED_POSITION = "stored_position";

	ImageSetItem mOpenedImagePage;
	ViewPager mViewPager;
	ViewerAdapter mAdapter;
	SparseArray<ImageSetLink> mData;

	public ViewerActivity() {
		mData = new SparseArray<ImageSetLink>();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mViewPager = new ViewPager(this);

		setContentView(mViewPager);

		int size = getIntent().getExtras().getInt(SIZE_KEY);
		mAdapter = new ViewerAdapter(this, size, this);
		mAdapter.setViewPagerChildFinder(this);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOffscreenPageLimit(retainedPages);

		if (savedInstanceState == null) {
			String startUrl = getIntent().getExtras().getString(START_URL_KEY);
			int startPosition = getIntent().getExtras().getInt(
					START_POSITION_KEY);

			new LoadImageLinkTask(mAdapter, startPosition).execute(startUrl);
			mViewPager.setCurrentItem(startPosition);
		} else {
			restoreState(savedInstanceState);
		}
	}

	void restoreState(Bundle savedInstanceState) {
		ImageSetLink[] imageData = (ImageSetLink[]) savedInstanceState
				.getParcelableArray(IMAGE_SET_DATA);
		for (int i = 0; i < imageData.length; i++) {
			mData.append(imageData[i].getPosition(), imageData[i]);
		}

		mViewPager.setCurrentItem(savedInstanceState.getInt(STORED_POSITION));
	}

	@Override
	public View findChild(int position) {
		return mViewPager.findViewWithTag(position);
	}

	@Override
	public ImageSetLink get(int position) {
		return mData.get(position);
	}

	@Override
	public void add(int position, ImageSetLink value) {
		mData.append(position, value);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		ImageSetLink[] outData = new ImageSetLink[mData.size()];

		for (int i = 0; i < mData.size(); i++) {
			outData[i] = mData.valueAt(i);
		}

		outState.putParcelableArray(IMAGE_SET_DATA, outData);
		outState.putInt(STORED_POSITION, mViewPager.getCurrentItem());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.viewer_menu, menu);				
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {		
		switch(item.getItemId()) {
		case R.id.menu_refresh:
			int pos = mViewPager.getCurrentItem();
			String url = mData.get(pos).getCurrentImageUrl();
			mAdapter.clearCachedImage(url);
			mAdapter.resetView(pos);
			return true;
		}
		return false;
	}
}
