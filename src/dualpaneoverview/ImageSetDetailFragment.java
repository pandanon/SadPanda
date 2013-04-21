package dualpaneoverview;

import util.ImageSetDescription;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.ecchi.sadpanda.R;

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

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ImageSetDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mImageSet = getArguments().getParcelable(ARG_ITEM_ID);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_imageset_detail,
				container, false);

		// Show the dummy content as text in a TextView.
		if (mImageSet != null) {
			((TextView) rootView.findViewById(R.id.imageset_detail))
					.setText(mImageSet.getSetName());
		}

		return rootView;
	}
}
