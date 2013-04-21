package util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;

import com.ecchi.sadpanda.HomePageBrowser;
import com.ecchi.sadpanda.ImageSetOverviewAdapter;
import com.ecchi.sadpanda.R;

public class ThumbnailLoader {
	private DiskLruImageCache storageCache;
	final String storageCacheName = "Thumbnails";
	private LruCache<Integer, Bitmap> memoryCache;

	public ThumbnailLoader(Activity activity) {
		final int storageSize = 1024 * 1024 * 10;

		storageCache = new DiskLruImageCache(activity, storageCacheName,
				storageSize, CompressFormat.PNG, 100);

		// Get max available VM memory, exceeding this amount will throw an
		// OutOfMemory exception. Stored in kilobytes as LruCache takes an
		// int in its constructor.
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 16;

		memoryCache = new LruCache<Integer, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(Integer key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getHeight() * bitmap.getWidth() * 4 / 1024;
			}

			@Override
			protected void entryRemoved(boolean evicted, Integer key,
					Bitmap oldValue, Bitmap newValue) {
				// TODO Auto-generated method stub
				super.entryRemoved(evicted, key, oldValue, newValue);

				if (evicted)
					storageCache.put(key, oldValue);
				else if (newValue != null)
					storageCache.put(key, newValue);
			}
		};
	}

	public void putBitmap(String url, View imageSet) {
		int key = url.hashCode();

		addImageToView(imageSet, getBitmap(key));
	}

	public void putBitmap(String url, ImageView v) {
		int key = url.hashCode();

		v.setImageBitmap(getBitmap(key));
	}

	private Bitmap getBitmap(int key) {
		Bitmap bm = memoryCache.get(key);

		if (bm == null) {
			bm = storageCache.getBitmap(key);

			if (bm != null)
				memoryCache.put(key, bm);
		}

		return bm;
	}

	public boolean containsBitmap(int key) {
		return storageCache.containsKey(key) || memoryCache.get(key) != null;
	}

	public class PageThumbLoader extends AsyncTask<Void, Void, Void> {
		ImageSetOverviewAdapter fakkuListAdapter;
		List<ImageSetDescription> volumes;

		public PageThumbLoader(ImageSetOverviewAdapter adapter,
				List<ImageSetDescription> volumes) {
			this.fakkuListAdapter = adapter;
			this.volumes = volumes;
		}

		@Override
		protected Void doInBackground(Void... zzz) {
			for (ImageSetDescription imageSet : volumes) {
				String stringUrl = imageSet.getSetThumbUrl();
				int key = stringUrl.hashCode();

				if (containsBitmap(key))
					continue;

				Bitmap bm = null;

				bm = loadBitmap(stringUrl);

				if (bm == null) {
					Log.w("hBrowser", "Welp, failed to load image...");
					continue;
				}

				if (bm.getWidth() != -1 && bm.getHeight() != -1) {
					cacheImage(key, bm);
				}

				publishProgress();
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
			fakkuListAdapter.notifyDataSetChanged();
		}

		private Bitmap loadBitmap(String stringUrl) {
			InputStream is = null;
			Bitmap bm = null;
			try {
				HttpGet imageGet = new HttpGet(stringUrl);
				HttpResponse response = HomePageBrowser.CLIENT.getClient()
						.execute(imageGet, HomePageBrowser.CLIENT.getContext());
				
				is = response.getEntity().getContent();
				
				bm = BitmapFactory.decodeStream(is);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (is != null)
						is.close();
				} catch (IOException e) {
					Log.w("hBrowser", "Error closing stream");
				}
			}

			return bm;
		}
	}

	private void addImageToView(View v, Bitmap bm) {
		ImageView image = ViewHolder.get(v, R.id.setThumb);
		image.setImageBitmap(bm);
	}

	public void cacheImage(int key, Bitmap bm) {
		memoryCache.put(key, bm);
	}
}
