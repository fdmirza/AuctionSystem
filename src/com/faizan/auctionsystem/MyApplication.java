package com.faizan.auctionsystem;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.L;

public class MyApplication extends Application {

	@Override
	public void onCreate() {

		super.onCreate();
		initImageLoader(getApplicationContext());
	}

	/**
	 * Initialized android universal image loader singleton
	 * 
	 * @param context
	 */
	public static void initImageLoader(Context context) {

		DisplayImageOptions options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.stub)

		.showImageOnFail(R.drawable.stub).showStubImage(R.drawable.stub).resetViewBeforeLoading(false).cacheInMemory(true).cacheOnDisc(true)

		.bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).defaultDisplayImageOptions(options).build();

		ImageLoader.getInstance().init(config);
		L.disableLogging();
	}

}
