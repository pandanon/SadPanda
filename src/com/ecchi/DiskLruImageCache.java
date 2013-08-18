package com.ecchi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.ecchi.sadpanda.BuildConfig;
import com.jakewharton.DiskLruCache;
import com.jakewharton.DiskLruCache.Editor;

public class DiskLruImageCache {
    public static final int IO_BUFFER_SIZE = 8 * 1024;

    private DiskLruCache mDiskCache;
    private CompressFormat mCompressFormat = CompressFormat.JPEG;
    private int mCompressQuality = 70;
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;
    @SuppressWarnings("unused")
    private static final String TAG = "DiskLruImageCache";

    public DiskLruImageCache(final Context context, final String uniqueName,
	    final int diskCacheSize, final CompressFormat compressFormat,
	    final int quality) {
	try {
	    final File diskCacheDir = getDiskCacheDir(context, uniqueName);
	    mDiskCache = DiskLruCache.open(diskCacheDir, APP_VERSION,
		    VALUE_COUNT, diskCacheSize);
	    mCompressFormat = compressFormat;
	    mCompressQuality = quality;
	} catch (final IOException e) {
	    e.printStackTrace();
	}
    }

    private boolean writeBitmapToFile(final Bitmap bitmap,
	    final DiskLruCache.Editor editor) throws IOException,
	    FileNotFoundException {
	OutputStream out = null;
	try {
	    out = new BufferedOutputStream(editor.newOutputStream(0),
		    IO_BUFFER_SIZE);
	    return bitmap.compress(mCompressFormat, mCompressQuality, out);
	} finally {
	    if (out != null) {
		out.close();
	    }
	}
    }

    private File getDiskCacheDir(final Context context, final String uniqueName) {

	// Check if media is mounted or storage is built-in, if so, try and use
	// external cache dir
	// otherwise use internal cache dir
	final String cachePath = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
		|| !isExternalStorageRemovable() ? getExternalCacheDir(context)
		.getPath() : context.getCacheDir().getPath();

	return new File(cachePath + File.separator + uniqueName);
    }

    public void put(final int keyIn, final Bitmap data) {

	final String key = String.valueOf(keyIn);

	DiskLruCache.Editor editor = null;
	try {
	    editor = mDiskCache.edit(key);
	    if (editor == null) {
		return;
	    }

	    if (writeBitmapToFile(data, editor)) {
		mDiskCache.flush();
		editor.commit();
	    } else {
		editor.abort();
	    }
	} catch (final IOException e) {
	    if (BuildConfig.DEBUG) {
		Log.d("cache_test_DISK_", "ERROR on: image put on disk cache "
			+ key);
	    }
	    try {
		if (editor != null) {
		    editor.abort();
		}
	    } catch (final IOException ignored) {
	    }
	}

    }

    public Bitmap getBitmap(final int keyIn) {

	final String key = String.valueOf(keyIn);

	Bitmap bitmap = null;
	DiskLruCache.Snapshot snapshot = null;
	try {

	    snapshot = mDiskCache.get(key);
	    if (snapshot == null) {
		return null;
	    }
	    final InputStream in = snapshot.getInputStream(0);
	    if (in != null) {
		final BufferedInputStream buffIn = new BufferedInputStream(in,
			IO_BUFFER_SIZE);
		bitmap = BitmapFactory.decodeStream(buffIn);
	    }
	} catch (final IOException e) {
	    e.printStackTrace();
	} finally {
	    if (snapshot != null) {
		snapshot.close();
	    }
	}

	return bitmap;

    }

    public Bitmap getBitmap(final int keyIn, final int reqWidth,
	    final int reqHeight) {
	final String key = String.valueOf(keyIn);

	Bitmap bitmap = null;
	DiskLruCache.Snapshot snapshot = null;
	try {

	    snapshot = mDiskCache.get(key);

	    if (snapshot == null) {
		return null;
	    }

	    final Editor editor = snapshot.edit();

	    // get original bitmap size first
	    final BitmapFactory.Options options = new Options();
	    options.inJustDecodeBounds = true;

	    final InputStream in1 = editor.newInputStream(0);
	    if (in1 != null) {
		final BufferedInputStream buffIn = new BufferedInputStream(in1,
			IO_BUFFER_SIZE);
		BitmapFactory.decodeStream(buffIn, null, options);
		buffIn.close();
	    }

	    options.inJustDecodeBounds = false;
	    options.inSampleSize = calculateInSampleSize(options, reqWidth,
		    reqHeight);

	    final InputStream in2 = editor.newInputStream(0);
	    if (in2 != null) {
		final BufferedInputStream buffIn = new BufferedInputStream(in2,
			IO_BUFFER_SIZE);
		bitmap = BitmapFactory.decodeStream(buffIn, null, options);
		buffIn.close();
	    }

	    editor.commit();

	} catch (final IOException e) {
	    e.printStackTrace();
	} finally {
	    if (snapshot != null) {
		snapshot.close();
	    }
	}

	return bitmap;
    }

    public static int calculateInSampleSize(
	    final BitmapFactory.Options options, final int reqWidth,
	    final int reqHeight) {
	// Raw height and width of image
	final int height = options.outHeight;
	final int width = options.outWidth;
	int inSampleSize = 1;

	if (height > reqHeight || width > reqWidth) {

	    // Calculate ratios of height and width to requested height and
	    // width
	    final int heightRatio = Math.round((float) height
		    / (float) reqHeight);
	    final int widthRatio = Math.round((float) width / (float) reqWidth);

	    // Choose the smallest ratio as inSampleSize value, this will
	    // guarantee
	    // a final image with both dimensions larger than or equal to the
	    // requested height and width.
	    inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	}

	return inSampleSize;
    }

    public boolean containsKey(final int keyIn) {

	final String key = String.valueOf(keyIn);

	boolean contained = false;
	DiskLruCache.Snapshot snapshot = null;
	try {
	    snapshot = mDiskCache.get(key);
	    contained = snapshot != null;
	} catch (final IOException e) {
	    e.printStackTrace();
	} finally {
	    if (snapshot != null) {
		snapshot.close();
	    }
	}

	return contained;

    }

    public void remove(final int keyIn) {
	final String key = String.valueOf(keyIn);
	try {
	    mDiskCache.remove(key);
	} catch (final IOException e) {
	    e.printStackTrace();
	}
    }

    public void clearCache() {

	try {
	    mDiskCache.delete();
	} catch (final IOException e) {
	    e.printStackTrace();
	}
    }

    public File getCacheFolder() {
	return mDiskCache.getDirectory();
    }

    @SuppressLint("NewApi")
    public static boolean isExternalStorageRemovable() {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
	    return Environment.isExternalStorageRemovable();
	}
	return true;
    }

    public static File getExternalCacheDir(final Context context) {
	if (hasExternalCacheDir()) {
	    final File val = context.getExternalCacheDir();
	    return val;
	}

	// Before Froyo we need to construct the external cache dir ourselves
	final String cacheDir = "/Android/data/" + context.getPackageName()
		+ "/cache/";
	final File val = new File(Environment.getExternalStorageDirectory()
		.getPath() + cacheDir);
	return val;
    }

    public static boolean hasExternalCacheDir() {
	return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }
}