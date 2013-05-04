package com.ecchi.sadpanda.imageviewer;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockActivity;

public class ViewerActivity extends SherlockActivity {
	public static final String AVAILABLE_URL_KEY = "available_url_key";
	static final String IMAGE_URL_KEY = "image_url_key";
	
	private ViewPager mViewPager; 
	private ViewerAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mViewPager = new ViewPager(this);
		
		setContentView(mViewPager);

		String[] availableUrls = null;
		if(savedInstanceState == null)
			 availableUrls = getIntent().getExtras().getStringArray(AVAILABLE_URL_KEY);
		
		mAdapter = new ViewerAdapter(this, availableUrls);	
		if(savedInstanceState != null)
			mAdapter.setData(savedInstanceState.getStringArrayList(IMAGE_URL_KEY));
		
		
		mViewPager.setAdapter(mAdapter);		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putStringArrayList(IMAGE_URL_KEY, mAdapter.getData());		
	}
}
