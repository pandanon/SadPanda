package util;

import java.util.ArrayList;
import java.util.List;

import org.ocpsoft.pretty.time.PrettyTime;

import tasks.LoadPandaPageTask;
import util.ImageSetDescription.ImageContent;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecchi.sadpanda.R;

public class ImageSetOverviewAdapter extends InfiniteScrollAdapter {

	List<ImageSetDescription> setImageList;
	int currentPage = 0;
	String baseUrl;
	PrettyTime timeFormat;
	//int columnwidth;

	public ImageSetOverviewAdapter(String baseUrl) {
		setImageList = new ArrayList<ImageSetDescription>();
		timeFormat = new PrettyTime();
		this.baseUrl = baseUrl;
		//this.columnwidth = columnwidth;
	}

	/*public void setColumnwidth(int columnwidth)
	{
		this.columnwidth = columnwidth;
		notifyDataSetChanged();
	}*/
	
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

		if (convertView == null)
			convertView = View.inflate(parent.getContext(), R.layout.image_set_item_layout, null); 
			
		
		//set appropriate size of image 
		ImageView thumb = ViewHolder.get(convertView, R.id.setThumb);
		thumb.setImageResource(R.drawable.sadpanda);
		
		thumb.setAdjustViewBounds(true);
		
		//set title
		TextView name = ViewHolder.get(convertView, R.id.setName);
		name.setText(description.getSetName());

		//set set uploader
		TextView uploader = ViewHolder.get(convertView, R.id.setUploader);
		uploader.setText(description.getSetUploader());

		//set date published
		TextView published = ViewHolder.get(convertView, R.id.setPublished);
		if (description.getSetPublished() == null)
			published.setText("Time unknown");
		else
			published.setText(timeFormat.format(description.getSetPublished()));

		//color imageContent
		View imageContent = ViewHolder.get(convertView, R.id.imageContent);
		imageContent.setBackgroundColor(ImageContent.getColor(description
				.getSetContent()));

		int pixelHeight = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, (float) 2, parent.getResources()
						.getDisplayMetrics());

		//scale score
		View setScore = ViewHolder.get(convertView, R.id.setScore);
		if (description.getSetScore() == 0)
		{
			setScore.setVisibility(View.INVISIBLE);
		}
		else {
			setScore.setVisibility(View.VISIBLE);
			LayoutParams params = setScore.getLayoutParams();
			params.height = pixelHeight * description.getSetScore();
			setScore.setLayoutParams(params);
		}

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
	}
}
