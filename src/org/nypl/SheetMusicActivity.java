package org.nypl;

import java.io.File;
import java.util.ArrayList;


import org.nypl.adapter.SheetMusicPagerAdapter;
import org.nypl.database.SheetMusicDAO;
import org.nypl.dataholder.SheetMusicBean;
import org.nypl.utils.CustomTypefaceSpan;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;

import android.webkit.WebView;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.SeekBar;
import android.widget.TextView;

public class SheetMusicActivity extends FragmentActivity {

	protected static final String TAG = "SheetMusicActivity";

	public static ImageView mFontsize;

	private ViewPager mSheetMusicPager;

	private WebView mFocusedPage;

	public static LinearLayout mAudiolayout;
	public static ImageButton btnPlay;
	public static SeekBar songProgressBar;
	public static TextView songCurrentDurationLabel;
	public static TextView songTotalDurationLabel;
	public static TextView songTitleLabel;
	public static ImageView mPlayerCancel;
	public static String mPlaysId;
	private SheetMusicPagerAdapter mMusicPagerAdapter;

	private String SheetMusicId;
	private String SheetMusicName;
	public int mPosition = 0;

	private ArrayList<SheetMusicBean> SheetMusicPages;

	private String savedUri;
	public static ImageView mPlayerEditAudio;

	public static int fontBtnCount = 0;
	private static final int REQUEST_CODE = 1;
	public static Context ctx;

	// public static String mNoteID;
	public static String CONTENT_LOCATION;
	public static String URLID;
	public static String currentChapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.sheetmusic_activity);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		// ((ImageView)findViewById(R.id.s_background_img)).setBackgroundDrawable(new
		// BitmapDrawable(BitmapFactory.decodeResource(getResources(),
		// R.drawable.bg_script, options)));
		// setCover();

		Typeface lato = Typeface.createFromAsset(getAssets(),
				"fonts/Lato-Reg.ttf");

		Typeface icomoon = Typeface.createFromAsset(getAssets(),
				"fonts/icomoon.ttf");

		mSheetMusicPager = (ViewPager) findViewById(R.id.sheetmusicpager);

		Bundle extras = getIntent().getExtras();
		SheetMusicId = extras.getString("SheetMusicID");
		mPosition = extras.getInt("position");
		CONTENT_LOCATION = extras.getString("content_location");
		
		// mPosition = 1;
		System.out.println(SheetMusicId);
		SheetMusicPages = SheetMusicDAO.getPagesForID(SheetMusicActivity.this,
				SheetMusicId);
		System.out.println("There are: " + SheetMusicPages.size());
		for (int i=0;i<SheetMusicPages.size();i++){
		System.out.println("Page "+i+": "
				+ SheetMusicPages.get(i).getSheetMusicHTML());
		}
		mMusicPagerAdapter = new SheetMusicPagerAdapter(SheetMusicPages,
				SheetMusicActivity.this, CONTENT_LOCATION);
		SheetMusicName = SheetMusicPages.get(0).getSheetMusicName();

		
		// /mSearchNote = extras.getString("mSearchNote");
		// / if(mNotes!=null || mSearchNote!=null){
		TextView titleBar = (TextView) findViewById(R.id.s_main_title);
		// String titleLogo ="&#xe012;";
		String titleText = "&#xe011; " + SheetMusicName;
		titleBar.setTypeface(lato);
		titleBar.setMovementMethod(LinkMovementMethod.getInstance());
		titleBar.setText(titleText);

		Spannable s = new SpannableString(Html.fromHtml(titleText));
		NonUnderlineLink clickHome = new NonUnderlineLink() {
			@Override
			public void onClick(View view) {
				// This will get "Lorem ipsum dolor sit amet", but I just want
				// "dolor"
				System.out.println("Home");
				finish();
			}
		};

		s.setSpan(new CustomTypefaceSpan("", icomoon), 0, 1,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new CustomTypefaceSpan("", lato), 2,
				SheetMusicName.length() - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(clickHome, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new ForegroundColorSpan(Color.parseColor("#7f2352")), 0,
				SheetMusicName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		titleBar.setText(s);

		mAudiolayout = (LinearLayout) findViewById(R.id.s_playsdetail_audio_layout);
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
		mPlayerCancel = (ImageView) findViewById(R.id.player_cancel);
		mPlayerEditAudio = (ImageView) findViewById(R.id.player_edit_audio);
		songTitleLabel = (TextView) findViewById(R.id.songTitle);

		System.out.println("After ViewPageAdapter");
		mSheetMusicPager.setAdapter(mMusicPagerAdapter);

		mSheetMusicPager.setCurrentItem(mPosition);

		if (SheetMusicPagerAdapter.mp != null) {
			// SheetMusicPagerAdapter.mp.pause();
			SheetMusicPagerAdapter.mp.release();
			// SheetMusicPagerAdapter.mp.stop();
			SheetMusicPagerAdapter.mHandler
					.removeCallbacks(SheetMusicPagerAdapter.mUpdateTimeTask);
			mAudiolayout.setVisibility(View.GONE);
		}

		mSheetMusicPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {

				// SheetMusicPagerAdapter.playAudio(null);
				mFocusedPage = (WebView) mMusicPagerAdapter.getChildAtIndex(
						arg0).findViewById(R.id.s_plays_detail_webview);
				mFocusedPage.getSettings().setBuiltInZoomControls(true);
				if (SheetMusicPagerAdapter.mp != null) {
					// SheetMusicPagerAdapter.mp.pause();
					SheetMusicPagerAdapter.mp.release();
					// SheetMusicPagerAdapter.mp.stop();
					SheetMusicPagerAdapter.mHandler
							.removeCallbacks(SheetMusicPagerAdapter.mUpdateTimeTask);
					mAudiolayout.setVisibility(View.GONE);
				}
				// /
				// mPlaysName.setText(versionDetailList.get(arg0).getVersionPlayName()+":");

				// mVersionName.setText(versionDetailList.get(arg0).getVersionName());
				mPosition = arg0;

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	protected void onPause() {

		super.onPause();

		// SheetMusicPagerAdapter.mp.reset();

		// Save state here
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			savedUri = data.getData().getPath();
			SheetMusicPagerAdapter.updateAudio(savedUri);
			SheetMusicPagerAdapter.playAudio(savedUri);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "" + mMusicPagerAdapter);
		Log.i(TAG, "" + mMusicPagerAdapter.getChildAtIndex(0));
		new Handler() {
			public void handleMessage(android.os.Message msg) {
				mFocusedPage = (WebView) mMusicPagerAdapter.getChildAtIndex(
						mPosition).findViewById(R.id.s_plays_detail_webview);
				mFocusedPage.getSettings().setBuiltInZoomControls(true);
			};
		}.sendEmptyMessageDelayed(1, 500);
		super.onResume();
	}
}
