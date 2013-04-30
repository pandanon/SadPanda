package com.ecchi.sadpanda.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.ecchi.sadpanda.HomePageBrowser;

public class ImageLoader {
	private static final int thumbWidth = 100;
	
	final String mDiskCacheName = "Images";
	final int mDiskCacheSize = 1024 * 1024 * 50; // 50MB

	final boolean downSample;
	final int bitmapWidth;
	final int bitmapHeight;
		
	LruCache<String, Bitmap> mMemoryCache;
	DiskLruImageCache mDiskCache;

	/***
	 * 
	 * @param context
	 * @param downSample store downsampled versions locally
	 * @param bitmapHeight requested size for the height
	 * @param bitmapWidth requested size for the width
	 */
	public ImageLoader(Context context, boolean downSample, int bitmapHeight, int bitmapWidth)
	{
		this.downSample = downSample;
		
		this.bitmapHeight = bitmapHeight;
		this.bitmapWidth = bitmapWidth;
		
		createCache(context);
	}
	
	public ImageLoader(Context context) {
		this.downSample = false;
		
		this.bitmapHeight = 0;
		this.bitmapWidth = 0;
		
		createCache(context);
	}
	
	private void createCache(Context context)
	{
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 8;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				// in kilobytes rather than bytes
				return value.getRowBytes() * value.getHeight() / 1024;
			}
		};

		mDiskCache = new DiskLruImageCache(context, "", mDiskCacheSize,
				CompressFormat.JPEG, 100);
	}
	
	public void clearCache()
	{
		mDiskCache.clearCache();
		mMemoryCache.evictAll();
	}

	public void loadBitmap(String url, ImageView imageView) {
		// always cancel possible previous tasks, or bad things will happen
		if (cancelPotentialWork(url, imageView)) {
			// check memory first
			Bitmap bm = mMemoryCache.get(url);
			if (bm != null) {
				imageView.setImageBitmap(bm);
			} else {
				final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
				final AsyncDrawable asyncDrawable = new AsyncDrawable(
						imageView.getResources(), null, task);				
				imageView.setImageDrawable(asyncDrawable);
				task.execute(url);
			}
		}
	}

	public static boolean cancelPotentialWork(String url, ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

		if (bitmapWorkerTask != null) {
			final String imageUrl = bitmapWorkerTask.url;
			if (!url.equals(imageUrl)) {
				// Cancel previous task
				bitmapWorkerTask.cancel(true);
			} else {
				// The same work is already in progress
				return false;
			}
		}
		// No task associated with the ImageView, or an existing task was
		// cancelled
		return true;
	}

	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}

	private Bitmap getBitmapFromURL(String url)
	{
		InputStream is = null;
		Bitmap bitmap = null;
		HttpURLConnection conn = null;
		try {
			HttpGet imageGet = new HttpGet(url);
			HttpResponse response = HomePageBrowser.CLIENT.getClient()
					.execute(imageGet, HomePageBrowser.CLIENT.getContext());
			
			is = response.getEntity().getContent();
			
			bitmap = BitmapFactory.decodeStream(is);
			
		} catch (Throwable ex) {
			ex.printStackTrace();
			// memory full, clean up needed
			if (ex instanceof OutOfMemoryError)
				mMemoryCache.evictAll();
		} finally {
			// cleaning up resources
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					// Welp, we've tried everything we could
					e.printStackTrace();
				}

			if (conn != null)
				conn.disconnect();
		}	
		
		return bitmap;
	}
	
	private Bitmap putBitmapInDisk(String url, boolean downSample, int reqWidth, int reqHeight)
	{
		if(mDiskCache.containsKey(url.hashCode()))
		{
			return mDiskCache.getBitmap(url.hashCode());
		}
				
		//get full sized bitmap from network
		Bitmap temp = getBitmapFromURL(url);		
				
		Bitmap bitmap = null;
		
		if(temp == null)
			return null;
		
		if(downSample)
		{
			bitmap = Bitmap.createScaledBitmap(temp, reqWidth, reqWidth, true);
			temp.recycle();
		}
		else
			bitmap = temp;
					
		if(bitmap == null)
			return null;
		
		mDiskCache.put(url.hashCode(), bitmap);
		
		return bitmap;
	}
	
	public synchronized void cutBitmapToDisk(String url)
	{
		InputStream is = null;
		Bitmap bitmap = null;
		HttpURLConnection conn = null;
		try {
			HttpGet imageGet = new HttpGet(url);
			HttpResponse response = HomePageBrowser.CLIENT.getClient()
					.execute(imageGet, HomePageBrowser.CLIENT.getContext());
			
			is = response.getEntity().getContent();
			
			bitmap = BitmapFactory.decodeStream(is);
			
		} catch (Throwable ex) {
			ex.printStackTrace();			
		} finally {
			// cleaning up resources
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					// Welp, we've tried everything we could
					e.printStackTrace();
				}

			if (conn != null)
				conn.disconnect();
		}	
		
		int offset = 0;
		int i = 0;
		while(offset < bitmap.getWidth())
		{
			String thumbUrl = url + "-" + i++;
			Bitmap thumb = Bitmap.createBitmap(bitmap, offset, 0, thumbWidth, bitmap.getHeight(), null, false);
			mDiskCache.put(thumbUrl.hashCode(), thumb);
			thumb.recycle();
			offset += thumbWidth;
		}
	}
	
	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		public String url;

		/***
		 * Creates a new loader task for the imageview. Stores the image at full
		 * size on the disk cache.
		 * 
		 * @param imageView
		 *            the relevant ImageView
		 */
		public BitmapWorkerTask(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		// Decode image in background.
		@Override
		protected Bitmap doInBackground(String... params) {
			url = params[0];

			return putBitmapInDisk(url, downSample, bitmapWidth, bitmapHeight);
		}
		
		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}

			if (imageViewReference != null && bitmap != null) {
				// we obviously retrieved it for a reason, so put it in memcache
				mMemoryCache.put(url, bitmap);

				// if imageview hasn't changed, set bitmap as drawable
				final ImageView imageView = imageViewReference.get();
				final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
				if (this == bitmapWorkerTask && imageView != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		}
	}

	class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

		public AsyncDrawable(Resources res, Bitmap bitmap,
				BitmapWorkerTask bitmapWorkerTask) {
			super(res, bitmap);
			bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(
					bitmapWorkerTask);
		}

		public BitmapWorkerTask getBitmapWorkerTask() {
			return bitmapWorkerTaskReference.get();
		}
	}
}
