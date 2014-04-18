package org.nypl.libretto;

import java.io.File;
import java.util.ArrayList;

import org.nypl.libretto.R;

import org.nypl.libretto.adapter.ViewPagerAdapter;
import org.nypl.libretto.database.AudioDAO;
import org.nypl.libretto.dataholder.VersionBean;
import org.nypl.libretto.utils.CustomTypefaceSpan;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.support.v4.app.FragmentActivity;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.webkit.WebView;

import android.widget.EditText;
import android.widget.ImageView;

import android.widget.TextView;

public class AboutActivity extends FragmentActivity {


	public static ImageView mFontsize;
	
	public static ImageView mPlayNote;

	public static Dialog mPlayNoteDialog;
	public EditText mNoteText;
	private File FilePath = Environment.getExternalStorageDirectory();


	private ArrayList<VersionBean> versionDetailList;
	
	private String AboutText="About";
	public int mPosition = 0;
	public String mFlag;
	public String Version = null ;
	public String mNotes = null;
	public String mSearchNote =null;

	private String savedUri;
	public static ImageView mPlayerEditAudio;

	public static int fontBtnCount = 0;
	private static final int REQUEST_CODE = 1;
	public static Context ctx;
	public static String HTMLFileName="";
	public static String VersionID;
	public static String VersionName;
	public static String PlayID;
	public static String mNoteID;
	public static String CONTENT_LOCATION ;
	public static String URLID;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
          
		setContentView(R.layout.s_plays_about_frag);
		 CONTENT_LOCATION = "Android/data/"+this.getPackageName()+File.separator+"contents";    
	      
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
	//	((ImageView)findViewById(R.id.s_background_img)).setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.bg_script, options)));
		//setCover();
		
		Typeface icomoon = Typeface.createFromAsset(getAssets(),
		        "fonts/icomoon.ttf");
		Typeface lato = Typeface.createFromAsset(getAssets(),
		        "fonts/Lato-Reg.ttf");


		
	
		//mPlayDetailPager = (ViewPager) findViewById(R.id.viewpager);

	
		///mSearchNote  = extras.getString("mSearchNote");
		///	if(mNotes!=null || mSearchNote!=null){
		TextView titleBar = (TextView) findViewById(R.id.s_main_title);
		WebView aboutWeb = (WebView) findViewById(R.id.s_about_webview);
		//String titleLogo ="&#xe012;";
		String titleText = "&#xe014; "+AboutText;
		titleBar.setTypeface(lato);
		titleBar.setMovementMethod(LinkMovementMethod.getInstance());
		titleBar.setText(titleText);
		Spannable s = new SpannableString(Html.fromHtml(titleText));
		NonUnderlineLink clickHome = new NonUnderlineLink() {
		    @Override
		    public void onClick(View view) {
		        // This will get "Lorem ipsum dolor sit amet", but I just want "dolor"
		       
		        finish();
		    }
		};
		s.setSpan (new CustomTypefaceSpan("", icomoon ), 0, 1,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan (new CustomTypefaceSpan("", lato ), 2, AboutText.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	//	s.setSpan (new CustomTypefaceSpan("", icomoon ), AboutText.length()+2,AboutText.length()+4,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan (clickHome,0,1,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	//	s.setSpan (clickNav,AboutText.length()+3,AboutText.length()+4,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	    s.setSpan(new ForegroundColorSpan(Color.parseColor("#7f2352")),0,AboutText.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	    
		titleBar.setText(s);
		//copyFromAssets("about.html");
		
		String filePath = "file://"+FilePath.getAbsolutePath() + File.separator+ CONTENT_LOCATION + File.separator+"about.html";
		
		aboutWeb.loadUrl(filePath);

	
	}

	
	@Override
	protected void onPause()
	{

		super.onPause();
		
			//	ViewPagerAdapter.mp.reset();
		
		//Save state here
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			savedUri = data.getData().getPath();
			ViewPagerAdapter.playAudio(savedUri);
			 HTMLFileName=versionDetailList.get(mPosition).getVersionHTMLFile();
			///	((SelectionWebView) mFocusedPage).SetAudio(savedUri,HTMLFileName);
			if(savedUri.contains("external"))
			{
			Uri parcialUri = Uri.parse("content://media"+savedUri);
			 Cursor cursor = this.getContentResolver().query(parcialUri, new String[] { android.provider.MediaStore.Audio.AudioColumns.DATA }, null, null, null);//("content://media/external/audio/media/4743.mp3", new String[] { android.provider.MediaStore.Audio.AudioColumns.DATA }, null, null, null);
	         cursor.moveToFirst();   
	         URLID = cursor.getString(0);
	         cursor.close();
			}else{
				URLID = savedUri;
			}
			ContentValues cv =new ContentValues();
			cv.put(AudioDAO.COLUMN_NAME_AUDIO_PATH, URLID);

				
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		
		
		super.onResume();
	}
	


	


	

	
	
	
	
	


	
	
}
