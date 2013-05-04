package com.ecchi.sadpanda.detailview;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.ecchi.sadpanda.R;
import com.ecchi.sadpanda.imageviewer.ViewerActivity;
import com.ecchi.sadpanda.util.ImageSetDescription;
import com.ecchi.sadpanda.util.ImageSetThumb;

/**
 * A fragment representing a single ImageSet detail screen. This fragment is
 * either contained in a {@link ImageSetListActivity} in two-pane mode (on
 * tablets) or a {@link ImageSetDetailActivity} on handsets.
 */
public class ImageSetDetailFragment extends SherlockFragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private ImageSetDescription mImageSet;
	private ImageSetDetailAdapter mImageAdapter;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ImageSetDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			if (getArguments().containsKey(ARG_ITEM_ID)) {
				// Load the dummy content specified by the fragment
				// arguments. In a real-world scenario, use a Loader
				// to load content from a content provider.
				mImageSet = getArguments().getParcelable(ARG_ITEM_ID);
			}
		} else {
			mImageSet = savedInstanceState.getParcelable(ARG_ITEM_ID);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_imageset_detail,
				container, false);

		// Show the dummy content as text in a TextView.
		if (mImageSet != null) {
			TextView name = (TextView) rootView.findViewById(R.id.set_name);
			name.setText(mImageSet.getSetName());

			TextView uploader = (TextView) rootView
					.findViewById(R.id.set_uploader);
			uploader.setText(mImageSet.getSetUploader());
		}

		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(ARG_ITEM_ID, mImageSet);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (mImageSet != null) {
			GridView thumbView = (GridView) view.findViewById(R.id.thumb_view);
			String baseUrl = mImageSet.getSetUrl() + "?p=";
			mImageAdapter = new ImageSetDetailAdapter(baseUrl,
					this.getActivity());
			thumbView.setAdapter(mImageAdapter);
			thumbView.setOnScrollListener(mImageAdapter);

			thumbView.setEmptyView(view.findViewById(R.id.empty));

			thumbView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					ImageSetThumb clickedPage = mImageAdapter.getItem(position);
					
					Intent viewerIntent = new Intent(getActivity(), ViewerActivity.class);
					viewerIntent.putExtra(ViewerActivity.CLICKED_PAGE_URL_KEY, clickedPage);
					startActivity(viewerIntent);
				}
			});
		}
	}
}
