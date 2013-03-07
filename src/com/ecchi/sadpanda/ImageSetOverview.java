package com.ecchi.sadpanda;

import util.ImageSetOverviewAdapter;
import util.InfiniteScrollAdapter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockListFragment;

public class ImageSetOverview extends SherlockFragment {
	
	GridView imageSetView;
	ImageSetOverviewAdapter imageSetAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		imageSetAdapter = new ImageSetOverviewAdapter(getArguments().getString("url"));
	}
	
	/*@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		imageSetView = (GridView) inflater.inflate(R.layout.image_set_overview_layout, null);
		imageSetView.setAdapter(imageSetAdapter);
		
		return imageSetView;
	}*/
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		imageSetView.setAdapter(imageSetAdapter);
		imageSetView.setOnScrollListener(imageSetAdapter);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		ViewGroup gridParent = (ViewGroup) inflater.inflate(R.layout.image_set_overview_layout, null);
		imageSetView =  (GridView)gridParent.findViewById(R.id.grid_View);
		//View empty = inflater.inflate(R.layout.loading_view, gridParent);
		//imageSetView.setEmptyView(empty);
		return gridParent;
	}
}