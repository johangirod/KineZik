package com.kinezik.music;

import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/**
 * Basic usage:
 * 
 * SongJacketImageView jacket = new SongJacketImageView(context);
 * jacket.setImageDrawable("http://some.url.to.your.image.jpg");
 * 
 * @see constructor for more information
 * 
 * @author barraq
 */
public class SongJacketImageView extends LinearLayout {

	private static final int COMPLETE = 0;
	private static final int FAILED = 1;

	private Context mContext;
	private Drawable mDrawable;
	private ProgressBar mSpinner;
	private ImageView mImage;

	String imageName;
	
	/**
	 * This is used when creating the view in XML
	 * To have an image load in XML use the tag 'image="http://developer.android.com/images/dialog_buttons.png"'
	 * Replacing the url with your desired image
	 * Once you have instantiated the XML view you can call
	 * setImageDrawable(url) to change the image
	 * @param context
	 * @param attrSet
	 */
	public SongJacketImageView(final Context context, final AttributeSet attrSet) {
		super(context, attrSet);
		final String url = attrSet.getAttributeValue(null, "image");
		if(url != null){
			instantiate(context, url, android.R.attr.progressBarStyle, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, ImageView.ScaleType.CENTER);
		} else {
			instantiate(context, null, android.R.attr.progressBarStyle, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, ImageView.ScaleType.CENTER);
		}
	}
	
	/**
	 * This is used when creating the view programatically
	 * Once you have instantiated the view you can call
	 * setImageDrawable(url) to change the image
	 * @param context the Activity context
	 * @param imageUrl the Image URL you wish to load
	 */
	public SongJacketImageView(final Context context, final String imageUrl) {
		this(context, imageUrl, android.R.attr.progressBarStyle, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, ImageView.ScaleType.CENTER);
	}
	
	/**
	 * This is used when creating the view programatically
	 * Once you have instantiated the view you can call
	 * setImageDrawable(url) to change the image
	 * @param context the Activity context
	 * @param imageUrl the Image URL you wish to load
	 * @param spinnerDefStyle the style of the spinner to use
	 */
	public SongJacketImageView(final Context context, final String imageUrl, int spinnerDefStyle, int layoutWidth, int layoutHeight, ImageView.ScaleType scaleType) {
		super(context);
		instantiate(context, imageUrl, spinnerDefStyle, layoutWidth, layoutHeight, scaleType);
		
	}

	/**
	 *  First time loading of the LoaderImageView
	 *  Sets up the LayoutParams of the view, you can change these to
	 *  get the required effects you want
	 */
	private void instantiate(final Context context, final String imageUrl, int spinnerDefStyle, int layoutWidth, int layoutHeight, ImageView.ScaleType scaleType) {
		mContext = context;

        //TextView textView = new TextView(mContext);
        //textView.setText("64");
        //textView.setTextAppearance(mContext, android.R.attr.textAppearanceSmall);

		mImage = new ImageView(mContext);
		mImage.setLayoutParams(new LayoutParams(layoutWidth, layoutHeight));
		mImage.setScaleType(scaleType);
		
		mSpinner = new ProgressBar(mContext, null, spinnerDefStyle);
		mSpinner.setLayoutParams(new LayoutParams(layoutWidth, layoutHeight));
		mSpinner.setIndeterminate(true);

		addView(mSpinner);
		addView(mImage);
		
		if(imageUrl != null){
			setImageDrawable(imageUrl);
		}
	}

	/**
	 * Set's the view's drawable, this uses the internet to retrieve the image
	 * don't forget to add the correct permissions to your manifest
	 * @param imageUrl the url of the image you wish to load
	 */
	public void setImageDrawable(final String imageUrl) {
		mDrawable = null;
		mSpinner.setVisibility(View.VISIBLE);
		mImage.setVisibility(View.GONE);
		new Thread(){
			@Override
			public void run() {
				try {
					mDrawable = getDrawableFromUrl(imageUrl);
					imageLoadedHandler.sendEmptyMessage(COMPLETE);
				} catch (MalformedURLException e) {
					imageLoadedHandler.sendEmptyMessage(FAILED);
				} catch (IOException e) {
					imageLoadedHandler.sendEmptyMessage(FAILED);
				}
			};
		}.start();
	}

	/**
	 * Set's the view's drawable
	 * @param image the drawable to set
	 */
	/*
	public void setImageDrawable(Drawable image) {
		mDrawable = image;
		mImage.setImageDrawable(mDrawable);
	}
	 */

	/**
	 * Return the image drawable object
	 */
	public Drawable getImageDrawable() {
		return mDrawable;
	}

	/**
	 * Callback that is received once the image has been downloaded
	 */
	
	private final Handler imageLoadedHandler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case COMPLETE:
				mImage.setImageDrawable(mDrawable);
				mImage.setVisibility(View.VISIBLE);
				mSpinner.setVisibility(View.GONE);
				break;
			case FAILED:
			default:
				// Could change image here to a 'failed' image
				// otherwise will just keep on spinning
				break;
			}
			return true;
		}
	});

	/**
	 * Pass in an image url to get a drawable object
	 * @return a drawable object
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public static Drawable getDrawableFromUrl(final String url) throws IOException, MalformedURLException {
		return Drawable.createFromStream(((java.io.InputStream)new java.net.URL(url).getContent()), "name");
	}
	
}
