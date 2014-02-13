package org.nypl.libretto;

import java.io.File;
import java.util.ArrayList;

import org.nypl.libretto.R;
import org.nypl.libretto.adapter.ChapterListAdapter;
import org.nypl.libretto.adapter.VersionListAdapter;
import org.nypl.libretto.adapter.ViewPagerAdapter;
import org.nypl.libretto.database.ChaptersDAO;
import org.nypl.libretto.database.VersionDAO;
import org.nypl.libretto.dataholder.ChaptersBean;
import org.nypl.libretto.dataholder.VersionBean;
import org.nypl.libretto.utils.CustomTypefaceSpan;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class PlaysDetailActivity extends FragmentActivity {

	protected static final String TAG = "PlaysDetailActivity";
	private TextView mVersionName;
	public static ImageView mFontsize;
	private Dialog mVersionDialog;
	private Dialog mChapterDialog;
	public static ImageView mPlayNote;

	public static Dialog mPlayNoteDialog;
	public EditText mNoteText;
	private Dialog mCoverDialog;
	private ViewPager mPlayDetailPager;
	private LinearLayout mPlayversionDropDown;
	private WebView mFocusedPage;
	
    public static LinearLayout mAudiolayout;
    public static ImageButton btnPlay;
	public static SeekBar songProgressBar;
	public static TextView songCurrentDurationLabel;
	public static TextView songTotalDurationLabel;
	public static TextView songTitleLabel;
	public static ImageView mPlayerCancel;
	private ViewPagerAdapter mPlayPagerAdapter;
	
	private String mPlaysId;
	private ArrayList<VersionBean> versionDetailList;
	private String PlaysName;
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
	public static String CONTENT_LOCATION ;
	public static String URLID;
	public static String currentChapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
          
		setContentView(R.layout.plays_detail_activity);
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
	
		Typeface icomoon = Typeface.createFromAsset(getAssets(),
		        "fonts/icomoon.ttf");
		Typeface lato = Typeface.createFromAsset(getAssets(),
		        "fonts/Lato-Reg.ttf");


		
	
		mPlayDetailPager = (ViewPager) findViewById(R.id.viewpager);

		Bundle extras = getIntent().getExtras();
		mPlaysId = extras.getString("playsId");
		PlaysName = extras.getString("playsName");
		mPosition  = extras.getInt("position");
		Version  = extras.getString("mVersion");
		
		 currentChapter = extras.getString("mChapter");

		//mNotes  = extras.getString("mNote");
		//VersionHtmlID =extras.getString("VersionHtmlID");
		//mNoteID=extras.getString("mNoteID");
		CONTENT_LOCATION = "Android/data/"+this.getPackageName()+File.separator+"contents"+File.separator+mPlaysId;
		
		
		///mSearchNote  = extras.getString("mSearchNote");
		///	if(mNotes!=null || mSearchNote!=null){
		TextView titleBar = (TextView) findViewById(R.id.s_main_title);
		TextView leftText = (TextView) findViewById(R.id.s_plays_chapter_left_text);
		TextView rightText = (TextView) findViewById(R.id.s_plays_chapter_right_text);
		leftText.setTypeface(lato);
		rightText.setTypeface(lato);
		//String titleLogo ="&#xe012;";
		String titleText = "&#xe014; "+PlaysName+" &#xe015;";
		titleBar.setTypeface(lato);
		titleBar.setMovementMethod(LinkMovementMethod.getInstance());
		titleBar.setText(titleText);
		String previousText="&#xe604; Previous ";
		String nextText = "Next &#xe605;";
		Spannable prevArrow = new SpannableString(Html.fromHtml(previousText));
		Spannable nextArrow = new SpannableString(Html.fromHtml(nextText));
		
		Spannable s = new SpannableString(Html.fromHtml(titleText));
		NonUnderlineLink clickHome = new NonUnderlineLink() {
		    @Override
		    public void onClick(View view) {
		        // This will get "Lorem ipsum dolor sit amet", but I just want "dolor"
		       
		        finish();
		    }
		};
		NonUnderlineLink clickNav = new NonUnderlineLink() {
		    @Override
		    public void onClick(View view) {
		        // This will get "Lorem ipsum dolor sit amet", but I just want "dolor"
		    	getChapters();
		    }
		};

	
		s.setSpan (new CustomTypefaceSpan("", icomoon ), 0, 1,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan (new CustomTypefaceSpan("", lato ), 2, PlaysName.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan (new CustomTypefaceSpan("", icomoon ), PlaysName.length()+2,PlaysName.length()+4,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan (clickHome,0,1,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan (clickNav,PlaysName.length()+3,PlaysName.length()+4,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	    s.setSpan(new ForegroundColorSpan(Color.parseColor("#7f2352")),0,PlaysName.length()+4,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	    
	    prevArrow.setSpan(new CustomTypefaceSpan("", icomoon ), 0, 1,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	    prevArrow.setSpan (new CustomTypefaceSpan("", lato ), 2, previousText.length()-8,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	    prevArrow.setSpan(new ForegroundColorSpan(Color.parseColor("#7f2352")),0,previousText.length()-7,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		   
	    
	    
	    nextArrow.setSpan(new CustomTypefaceSpan("", icomoon ), nextText.length()-8,nextText.length()-7,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	    nextArrow.setSpan (new CustomTypefaceSpan("", lato ), 0, nextText.length()-8,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	    nextArrow.setSpan(new ForegroundColorSpan(Color.parseColor("#7f2352")),0,nextText.length()-7,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		
	    leftText.setText(prevArrow);
	    rightText.setText(nextArrow);
	    
	    leftText.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
		      	int curChap = mPlayPagerAdapter.getCurrentChapterPos();
		      	Version=versionDetailList.get(mPosition).getVersionUUID();
		    	final ArrayList<ChaptersBean> cList = ChaptersDAO.getChaptersForVersion(getApplicationContext(), Version);
		    	if (curChap>1){
		    		curChap--;
		    		goToChapter(curChap,cList);
		    	}
		    	else{
		    		Toast.makeText(getApplicationContext(), "This version only has one chapter.  Swipe right or left to switch versions.", Toast.LENGTH_SHORT).show();
					
		    	}
	        }
	    });
	    rightText.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
		    	int curChap = mPlayPagerAdapter.getCurrentChapterPos();
		    	Version=versionDetailList.get(mPosition).getVersionUUID();
		    	final ArrayList<ChaptersBean> cList = ChaptersDAO.getChaptersForVersion(getApplicationContext(), Version);
		    	if (curChap<cList.size()-1){
					curChap++;
					goToChapter(curChap,cList);
				}
		    	else{
		    		Toast.makeText(getApplicationContext(), "This version only has one chapter.  Swipe right or left to switch versions.", Toast.LENGTH_SHORT).show();
					
		    	}
	        }
	    });
		titleBar.setText(s);

		
		mVersionName=(TextView) findViewById(R.id.s_plays_version_name);
		//mFontsize = (ImageView) findViewById(R.id.s_plays_detail_text);
	
		//mPlayNote = (ImageView) findViewById(R.id.s_plays_detial_img_note);
		mPlayversionDropDown=(LinearLayout) findViewById(R.id.play_version_drop_down_layout);
		mAudiolayout= (LinearLayout) findViewById(R.id.s_playsdetail_audio_layout);
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
		mPlayerCancel = (ImageView) findViewById(R.id.player_cancel);
		mPlayerEditAudio =(ImageView) findViewById(R.id.player_edit_audio);
		songTitleLabel = (TextView) findViewById(R.id.songTitle);
		versionDetailList = VersionDAO.getVersionOf(PlaysDetailActivity.this, mPlaysId);
		if(Version==null){
			Version=versionDetailList.get(mPosition).getVersionUUID();
		}
		mPlayPagerAdapter = new ViewPagerAdapter(versionDetailList,PlaysDetailActivity.this,Version,CONTENT_LOCATION,currentChapter);
		mPlayDetailPager.setAdapter(mPlayPagerAdapter);
		/*if(mNotes!=null){
			for(int i=0;i<versionDetailList.size();i++){
				if(versionDetailList.get(i).getVersionID().equals(Version)){
					mPosition=i;
				}
			}
		}*/


		mPlayDetailPager.setCurrentItem(mPosition);
		
		/**
		 * vairiable for selectionwebview class
		 * 
		 */	

	     VersionID = versionDetailList.get(mPosition).getVersionID() ;
		 VersionName = versionDetailList.get(mPosition).getVersionName();
		 PlayID = versionDetailList.get(mPosition).getVersionPlayID();
		
		CharSequence fullVName = versionDetailList.get(mPosition).getVersionName();
	
		if (fullVName.length()>40){
			fullVName=fullVName.subSequence(0,40)+"...";
		}
		;
		mVersionName.setText(fullVName);
		

		
		
		if(ViewPagerAdapter.mp!=null){
			ViewPagerAdapter.mp.release();
		ViewPagerAdapter.mHandler.removeCallbacks(ViewPagerAdapter.mUpdateTimeTask);
		mAudiolayout.setVisibility(View.GONE);
		}
			
		mPlayDetailPager.setOnPageChangeListener(new OnPageChangeListener() {

			

			@Override
			public void onPageSelected(int arg0) {

				mFocusedPage = (WebView) mPlayPagerAdapter.getChildAtIndex(arg0).findViewById(R.id.s_plays_detail_webview);
				if(ViewPagerAdapter.mp!=null){
					ViewPagerAdapter.mp.release();
					ViewPagerAdapter.mHandler.removeCallbacks(ViewPagerAdapter.mUpdateTimeTask);
					mAudiolayout.setVisibility(View.GONE);
				}
			
				
				CharSequence fullVName = versionDetailList.get(arg0).getVersionName();
				
				if (fullVName.length()>40){
					fullVName=fullVName.subSequence(0,40)+"...";
				}
				
				mVersionName.setText(fullVName);
				mPosition=arg0;
				Version=versionDetailList.get(arg0).getVersionID();
	
				
				/**
				 * vairiable for selectionwebview class
				 * 
				 */	
				
				 HTMLFileName=versionDetailList.get(arg0).getVersionHTMLFile();
				 VersionID = versionDetailList.get(arg0).getVersionID() ;
				 VersionName = versionDetailList.get(arg0).getVersionName();
				 PlayID = versionDetailList.get(arg0).getVersionPlayID();
				
			
		

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
				mFocusedPage.loadUrl("javascript:scrollToElement("+mPlayPagerAdapter.jsScrollPosition.getScrollPosition()+")");
				
							}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});



		mPlayversionDropDown.setOnClickListener(new OnClickListener() {
			String version = null;
			@Override
			public void onClick(View v) {
				getVersion(version);
			}
		});
		

	
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
			ViewPagerAdapter.updateAudio(savedUri);
			ViewPagerAdapter.playAudio(savedUri);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		new Handler(){
			public void handleMessage(android.os.Message msg) {
				mFocusedPage = (WebView) mPlayPagerAdapter.getChildAtIndex(mPosition).findViewById(R.id.s_plays_detail_webview);		
			};
		}.sendEmptyMessageDelayed(1, 500);
		super.onResume();
	}
	private void goToChapter(int chapNum, ArrayList<ChaptersBean> cList){
		
		String chapId = cList.get(chapNum).getChapterMappingID();
		if (!(chapId.equals(-1))){
			
	//	chapId = chapId.substring(0,chapId.indexOf("-"));
		String chapUrl = cList.get(chapNum).getHTMLFile();
		mPlayPagerAdapter.setCurrentChapter(chapId);
		mPlayPagerAdapter.setCurrentChapterPos(chapNum);
		mFocusedPage.loadUrl("file:///"+Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ CONTENT_LOCATION + File.separator+chapUrl);

		
		String nextVersionId = mPlayPagerAdapter.getVersionID();
		ArrayList<ChaptersBean> chaptersList = ChaptersDAO.getChaptersForVersion(getApplicationContext(),nextVersionId);  
		int nextChapPos = mPlayPagerAdapter.getChapterPosFromId(chapId,nextVersionId);
		ChaptersBean nextChapterBean= chaptersList.get(nextChapPos);
		String filePath = "file:///"+Environment.getExternalStorageDirectory().getAbsolutePath()  + File.separator+ CONTENT_LOCATION + File.separator +nextChapterBean.getHTMLFile();
		mPlayPagerAdapter.mPlayDetailView.loadUrl(filePath);
		}
	}
	private void getChapters(){
		//final ArrayList<ChaptersBean> chapterList = mPlayPagerAdapter.jsScrollPosition.Chapters;
	
			Version=versionDetailList.get(mPosition).getVersionUUID();
		
		
		final ArrayList<ChaptersBean> chapterList = ChaptersDAO.getChaptersForVersion(getApplicationContext(), Version);
		
		mChapterDialog=new Dialog(PlaysDetailActivity.this,android.R.style.Theme_Translucent);
		mChapterDialog.getWindow();
		mChapterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mChapterDialog.setContentView(R.layout.popup_plays_chapters);
		ListView mChapterList=(ListView) mChapterDialog.findViewById(R.id.s_play_chapter_list);

		mChapterList.setAdapter(new ChapterListAdapter(chapterList));
		mChapterList.setOnItemClickListener(new OnItemClickListener() {



			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Version=versionDetailList.get(mPosition).getVersionUUID();
		    	final ArrayList<ChaptersBean> cList = ChaptersDAO.getChaptersForVersion(getApplicationContext(), Version);
				
				mChapterDialog.cancel();
				goToChapter(arg2,cList);


			}
		});
		mChapterDialog.show();
		

	}
	private void getVersion(final String mVersion){
		
		mVersionDialog=new Dialog(PlaysDetailActivity.this,android.R.style.Theme_Translucent);
		mVersionDialog.getWindow();
		mVersionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mVersionDialog.setContentView(R.layout.popup_plays_version);
		ListView mVersionList=(ListView) mVersionDialog.findViewById(R.id.s_play_version_list);
		mVersionList.setAdapter(new VersionListAdapter(versionDetailList));
		mVersionList.setOnItemClickListener(new OnItemClickListener() {



			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				mVersionDialog.cancel();
				versionDetailList = VersionDAO.getVersionOf(PlaysDetailActivity.this, mPlaysId);
				finish();
				Intent i = new Intent(PlaysDetailActivity.this,PlaysDetailActivity.class);
				i.putExtra("playsId",versionDetailList.get(arg2).getVersionPlayID());
				i.putExtra("playsName",PlaysName);
				i.putExtra("position",arg2);
				i.putExtra("mVersion",mVersion);
				i.putExtra("VersionHtmlID",mVersion);
				i.putExtra("mNote","Home");
				i.putExtra("mChapter",mPlayPagerAdapter.getCurrentChapter()+"");
				startActivity(i);


			}
		});
		mVersionDialog.show();

	}



	

	
	
	
	
	

	public void setCover(){
		mCoverDialog=new Dialog(PlaysDetailActivity.this,android.R.style.Theme_Translucent);
		mCoverDialog.getWindow();
		mCoverDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mCoverDialog.setContentView(R.layout.cover);
		mCoverDialog.show();
	}
	public void cancelCover(){

		mCoverDialog.cancel();
	}

	
	
}
