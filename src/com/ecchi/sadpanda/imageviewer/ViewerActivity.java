package com.ecchi.sadpanda.imageviewer;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.actionbarsherlock.app.SherlockActivity;
import com.ecchi.sadpanda.imageviewer.ViewerAdapter.ViewPagerChildFinder;
import com.ecchi.sadpanda.util.ImageSetItem;

public class ViewerActivity extends SherlockActivity implements
		ViewPagerChildFinder {
	public static final String START_URL_KEY = "start_url";
	public static final String START_POSITION_KEY = "start_position";
	public static final String SIZE_KEY = "size_key";

	ImageSetItem mOpenedImagePage;
	private ViewPager mViewPager;
	private ViewerAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mViewPager = new ViewPager(this);

		setContentView(mViewPager);

		String startUrl = getIntent().getExtras().getString(START_URL_KEY);
		int startPosition = getIntent().getExtras().getInt(START_POSITION_KEY);
		int size = getIntent().getExtras().getInt(SIZE_KEY);

		mAdapter = new ViewerAdapter(this, startUrl, startPosition, size);
		mAdapter.setViewPagerChildFinder(this);
		mViewPager.setAdapter(mAdapter);
	}

	@Override
	public View findChild(int position) {
		return mViewPager.findViewWithTag(position);
	}

}
