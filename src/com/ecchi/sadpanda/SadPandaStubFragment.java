package com.ecchi.sadpanda;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.actionbarsherlock.app.SherlockFragment;

public class SadPandaStubFragment extends SherlockFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		ImageView pandaView = new ImageView(getActivity());
		pandaView.setImageResource(R.drawable.sadpanda);
		pandaView.setScaleType(ScaleType.FIT_CENTER);
		
		return pandaView;
	}
}
