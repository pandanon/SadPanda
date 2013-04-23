package dualpaneoverview;

import java.util.ArrayList;
import java.util.List;

import org.ocpsoft.pretty.time.PrettyTime;

import tasks.LoadPandaPageTask;
import util.BitmapLoader;
import util.ImageSetDescription;
import util.ImageSetDescription.ImageContent;
import util.PagedScrollAdapter;
import util.ViewHolder;
import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecchi.sadpanda.R;

public class ImageSetOverviewAdapter extends PagedScrollAdapter {

	List<ImageSetDescription> setImageList;
	int currentPage = 0;
	String baseUrl;
	PrettyTime timeFormat;
	BitmapLoader bitmapLoader;

	public ImageSetOverviewAdapter(String baseUrl, Activity activity) {
		setImageList = new ArrayList<ImageSetDescription>();
		timeFormat = new PrettyTime();
		this.baseUrl = baseUrl;
		bitmapLoader = new BitmapLoader(activity, true, 1, 100);
		// this.columnwidth = columnwidth;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return setImageList.size();
	}

	@Override
	public ImageSetDescription getItem(int position) {
		// TODO Auto-generated method stub
		return setImageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ImageSetDescription description = getItem(position);

		if (convertView == null) {			
			convertView = ((LayoutInflater) parent.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.image_set_item_layout, parent, false);
		}
		ImageView thumb = ViewHolder.get(convertView, R.id.setThumb);

		bitmapLoader.loadBitmap(description.getSetThumbUrl(), thumb);

		// set title
		TextView name = ViewHolder.get(convertView, R.id.setName);
		name.setText(description.getSetName());

		// set uploader
		TextView uploader = ViewHolder.get(convertView, R.id.setUploader);
		uploader.setText(description.getSetUploader());
	
		// set date published
		TextView published = ViewHolder.get(convertView, R.id.setPublished);
		if (description.getSetPublished() == null)
			published.setText("Time unknown");
		else
			published.setText(timeFormat.format(description.getSetPublished()));

		// color imageContent
		View imageContent = ViewHolder.get(convertView, R.id.imageContent);
		imageContent.setBackgroundColor(ImageContent.getColor(description
				.getSetContent()));

		// scale score
		TextView setScore = ViewHolder.get(convertView, R.id.setScore);

		setScore.setText(Html
				.fromHtml("\n<sup><b><font color=\"white\"><small><small>"
						+ description.getSetScore()
						+ "</small></small></font></b></sup><small>/</small><sub><small><small>10</small></small></sub>\n"));
		
		return convertView;
	}

	@Override
	public void loadNewDataSet() {
		new LoadPandaPageTask(this).execute(baseUrl + currentPage);
	}

	public void addItems(List<ImageSetDescription> newImages) {
		setImageList.addAll(newImages);
		currentPage++;
		notifyDataSetChanged();
		
		//bitmapLoader.new AsyncPrefetchTask(newImages).execute();		
	}

}
