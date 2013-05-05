package com.ecchi.sadpanda.imageviewer;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockActivity;
import com.ecchi.sadpanda.util.ImageSetItem;

public class ViewerActivity extends SherlockActivity {
	public static final String AVAILABLE_URL_KEY = "available_url_key";
	static final String IMAGE_URL_KEY = "image_url_key";
	public static final String CLICKED_PAGE_URL_KEY = "clicked_page_url_key";
	public static final String BASE_URL = "base_url";

	ImageSetItem mOpenedImagePage;
	private ViewPager mViewPager;
	private ViewerAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mViewPager = new ViewPager(this);

		setContentView(mViewPager);

		String baseUrl = getIntent().getExtras().getString(BASE_URL);		
		
		mAdapter = new ViewerAdapter(this,baseUrl);
		mViewPager.setAdapter(mAdapter);		
	}
}
