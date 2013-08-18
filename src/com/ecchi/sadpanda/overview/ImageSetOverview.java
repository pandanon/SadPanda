package com.ecchi.sadpanda.overview;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ecchi.sadpanda.R;
import com.ecchi.sadpanda.util.ImageSetDescription;
import com.michaelnovakjr.numberpicker.NumberPickerDialog;
import com.michaelnovakjr.numberpicker.NumberPickerDialog.OnNumberSetListener;

public class ImageSetOverview extends SherlockFragment implements
	OnNumberSetListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
	/**
	 * Callback for when an item has been selected.
	 */
	public void onItemSelected(ImageSetDescription id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
	@Override
	public void onItemSelected(final ImageSetDescription id) {
	}
    };

    ListView mImageSetView;
    ImageSetOverviewAdapter mImageSetAdapter;
    boolean activateOnItemClick;

    @Override
    public void onAttach(final Activity activity) {
	super.onAttach(activity);

	mImageSetAdapter = new ImageSetOverviewAdapter(getArguments()
		.getString("url"), activity);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {

	mImageSetView.setAdapter(mImageSetAdapter);
	mImageSetView.setOnScrollListener(mImageSetAdapter);

	mImageSetView.setOnItemClickListener(new OnItemClickListener() {

	    @Override
	    public void onItemClick(final AdapterView<?> parent,
		    final View view, final int position, final long id) {
		if (mCallbacks != null)
		    mCallbacks.onItemSelected(mImageSetAdapter
			    .getItem(position));
	    }
	});

	if (savedInstanceState != null
		&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
	    setActivatedPosition(savedInstanceState
		    .getInt(STATE_ACTIVATED_POSITION));
	}

	setActivateOnItemClick(activateOnItemClick);

	super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
	    final ViewGroup container, final Bundle savedInstanceState) {
	mImageSetView = new ListView(getActivity());

	final ProgressBar emptyView = new ProgressBar(getActivity());

	final FrameLayout.LayoutParams emptyViewParams = new FrameLayout.LayoutParams(
		80, 80, Gravity.CENTER);

	container.addView(emptyView, emptyViewParams);

	mImageSetView.setEmptyView(emptyView);

	return mImageSetView;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
	super.onSaveInstanceState(outState);
	if (mActivatedPosition != ListView.INVALID_POSITION) {
	    // Serialize and persist the activated item position.
	    outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
	}
    }

    public void setCallbackListener(final Callbacks listener) {
	mCallbacks = listener;
    }

    @Override
    public void onDetach() {
	super.onDetach();

	mCallbacks = sDummyCallbacks;
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(final boolean activateOnItemClick) {
	// When setting CHOICE_MODE_SINGLE, ListView will automatically
	// give items the 'activated' state when touched.

	this.activateOnItemClick = activateOnItemClick;

	if (mImageSetView != null)
	    mImageSetView
		    .setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
			    : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(final int position) {
	if (position == ListView.INVALID_POSITION) {
	    mImageSetView.setItemChecked(mActivatedPosition, false);
	} else {
	    mImageSetView.setItemChecked(position, true);
	}

	mActivatedPosition = position;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
	inflater.inflate(R.menu.overview_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
	switch (item.getItemId()) {
	case R.id.menu_refresh:
	    mImageSetAdapter.clear(true);
	    return true;
	case R.id.go_to_page:
	    final NumberPickerDialog dialog = new NumberPickerDialog(
		    getActivity(), -1, mImageSetAdapter.getCurrentPage());
	    dialog.setOnNumberSetListener(this);
	    dialog.show();
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

    @Override
    public void onNumberSet(final int selectedNumber) {
	if (selectedNumber >= 0)
	    mImageSetAdapter.goToPage(selectedNumber);
    }

    public void setUrl(final String url, final Activity activity) {
	mImageSetAdapter = new ImageSetOverviewAdapter(url, activity);
    }
}
