package com.ecchi.sadpanda.imageviewer;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockActivity;
import com.ecchi.sadpanda.util.ImageSetItem;

public class ViewerActivity extends SherlockActivity {
	public static final String BASE_URL_KEY = "base_url";
	public static final String SIZE_KEY = "size_key";

	ImageSetItem mOpenedImagePage;
	private ViewPager mViewPager;
	private ViewerAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mViewPager = new ViewPager(this);

		setContentView(mViewPager);

		String baseUrl = getIntent().getExtras().getString(BASE_URL_KEY);		
		int size = getIntent().getExtras().getInt(SIZE_KEY);
		
		mAdapter = new ViewerAdapter(this,baseUrl,size);
		mViewPager.setAdapter(mAdapter);		
	}

}
