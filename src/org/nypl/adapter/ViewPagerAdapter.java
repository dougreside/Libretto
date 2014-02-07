package org.nypl.adapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nypl.LibrettoContentProvider;
import org.nypl.PlaysDetailActivity;
import org.nypl.R;
import org.nypl.SelectionWebView;
import org.nypl.SheetMusicActivity;
import org.nypl.database.AudioDAO;
import org.nypl.database.ChaptersDAO;
import org.nypl.database.PlayDAO;
import org.nypl.database.PlayNoteDAO;
import org.nypl.dataholder.AudioBean;
import org.nypl.dataholder.ChaptersBean;
import org.nypl.dataholder.PlayNoteBean;
import org.nypl.dataholder.PlaysBean;
import org.nypl.dataholder.VersionBean;
import org.nypl.utils.Utilities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class ViewPagerAdapter extends PagerAdapter{
	
	private int currentChapterPos=0;
	private String currentChapterId;
	public String getVersionID(){
		return Version;
	}
	public void setCurrentChapter(String chapterId){
		this.currentChapterId = chapterId;
	}
	public void setCurrentChapterPos(int pos){
		this.currentChapterPos=pos;
	}
	public String getCurrentChapter(){
		return this.currentChapterId;
	}
	public int getChapterPosFromId(String chapterId,String versionId){
		ArrayList<ChaptersBean> chaps = ChaptersDAO.getChaptersForVersion(mContext, versionId);
		int i=0;
		while ((i<chaps.size())&&
				(!(chaps.get(i).getChapterMappingID().equalsIgnoreCase(chapterId)))){
			i++;
		}
		if (i==chaps.size()){
			i=0;
		}
		return i;
	
	}
	public int getCurrentChapterPos(){
		return currentChapterPos;
	}
	
	 public class ScrollPosition {
		    
		    public ArrayList<ChaptersBean> Chapters = new ArrayList<ChaptersBean>();;
		    @JavascriptInterface
		    public void reportHeads(String heads){
		    	Chapters.clear();
		    	try{
		    	JSONArray jheads = new JSONArray(heads);
				
				for(int i = 0; i < jheads.length(); i++){
					JSONObject headobject = jheads.getJSONObject(i);	
					String chapterId = headobject.getString("id").trim();
					String chapterText = headobject.getString("text").trim();
					ChaptersBean cb = new ChaptersBean();
					cb.setChapterID(chapterId);
					cb.setChapterName(chapterText);
					Chapters.add(cb);
					
				}	
		    }catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    }
		    public void setScrollPosition(String sp ) {
		    	PlayDAO.updateScrollPosition(mContext,mPlaysId,sp);
		    	
		    	
		    	//scrollposition = sp;
		    	
		    }
		    @JavascriptInterface
		    public String getScrollPosition() {
		    	String sp = PlayDAO.getPlayByID(mContext,mPlaysId).get(0).getScrollPosition();
		    	
		    	return sp;
		    }
	 }
	
		 //webView.loadUrl("javascript:alert(injectedObject.toString())");
	public ScrollPosition jsScrollPosition = new ScrollPosition();
	private View[] mChilds;
	public File xml_file_path;
	private static File FilePath = Environment.getExternalStorageDirectory();
	public static String CONTENT_LOCATION ;
	private static ArrayList<VersionBean> mVersionDetailList;
	private static ArrayList<ChaptersBean> mChaptersList;
	private static Context mContext;
	private Dialog mVersionDialog;
	public static SelectionWebView mPlayDetailView;
	private String VersionHtmlId = null;
	private ProgressDialog pd;
	private LinearLayout progress;
	private String mNotes;
	private int mPosition;
	private String path = Environment.getExternalStorageDirectory().toString();
	

	public static  MediaPlayer mp=new MediaPlayer();
	public static Handler mHandler = new Handler();
	private int seekForwardTime = 5000; // 5000 milliseconds
	private int seekBackwardTime = 5000; // 5000 milliseconds

	private static Utilities utils;
	private static String mPlaysId;

	public static Dialog mPlayNoteDialog;

	private static EditText mNoteText;

	private static Button mSaveBtn;

	private static Button mCancelBtn;
	private static String Version;
	public static String mNoteID;

	private static ArrayList<PlayNoteBean> playNoteList;

	private static Dialog mAudioDialog;
	public static SharedPreferences app_preferences;
	private static String PKG;
	public static int mPlayFlag=0;
	public static String audioid = null;
	public static int currentPosition1 =0;
	private static Animation slideUpIn;
	private static final int REQUEST_CODE = 1;
	static int aflag = 0;
	public int webposition ;
	public static ArrayList<AudioBean> AudioDatalist = null;
	public static ArrayList<ChaptersBean> ChaptersList = null;
	private static Dialog mEditAudioDialog;
	public SelectionWebView getCurrentView(){
		return mPlayDetailView;
	}
	public ViewPagerAdapter(ArrayList<VersionBean> versionDetailList,Context ctx,String Version,String content_location,String currentChapter) {
		super();
		this.currentChapterId=currentChapter;
		this.mVersionDetailList = versionDetailList;
		this.mContext=ctx;
		mChilds = new View[this.mVersionDetailList.size()];
		this.Version=Version;
		this.CONTENT_LOCATION=content_location;
	}

	public View getChildAtIndex(int index){

		return mChilds[index];
	}

	@Override
	public int getCount() {

		return mVersionDetailList.size();
	}

	@Override
	public Object instantiateItem(ViewGroup collection,   int position) {
		
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View vg = (View) inflater.inflate(R.layout.e_play_fulltext, null);
		PlaysDetailActivity.mAudiolayout.setVisibility(View.GONE);
		mPlayDetailView= (SelectionWebView) vg.findViewById(R.id.s_plays_detail_webview);
		((ViewPager) collection).addView(vg,0);
		mChilds[position] = vg;
		progress =(LinearLayout) vg.findViewById(R.id.progress);
		mPlayDetailView.getSettings().setJavaScriptEnabled(true);
		mPlayDetailView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		mPlayDetailView.clearCache(true);
		mPlayDetailView.setVerticalScrollBarEnabled(true);

		mPlayDetailView.setBackgroundColor(Color.WHITE);
		mPosition=((ViewPager) collection).getCurrentItem();
		webposition=position;
		if(mPosition==position){
			pd = ProgressDialog.show(mContext, "Loading", "please wait...");
			mPlayDetailView.setTag("web");
		}
		mPlaysId = mVersionDetailList.get(position).getVersionPlayID();
		String versionId =  mVersionDetailList.get(position).getVersionUUID();
		Version = versionId;
		ArrayList<ChaptersBean> chaptersList = ChaptersDAO.getChaptersForVersion(mContext,versionId);  
		ChaptersBean currentChapterBean;
		if (chaptersList.size()>=currentChapterPos){
		 currentChapterBean= chaptersList.get(currentChapterPos);
		}
		else{
		 currentChapterBean= chaptersList.get(0);
				
		}
		String filePath = "file:///"+FilePath.getAbsolutePath() + File.separator+ CONTENT_LOCATION + File.separator +currentChapterBean.getHTMLFile();
	
		
		mPlayDetailView.addJavascriptInterface(jsScrollPosition, "appScrollManager");
		mPlayDetailView.loadUrl(filePath);
		
		mPlayDetailView.setWebChromeClient(new WebChromeClient() {
			  public void onConsoleMessage(String message, int lineNumber, String sourceID) {
			   
			  }
			});
		mPlayDetailView.setWebViewClient(new myclient());
		return vg;
	}


	private class myclient extends WebViewClient{

		private Dialog mNoteSelectDialog;
		private String mNoteId;

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);

		}
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);

			if(url.contains("#")){
				VersionHtmlId=null;
			}
			if(VersionHtmlId!=null )
			{
				
				view.loadUrl("javascript:scrollToElement('" + VersionHtmlId.trim() + "')");
				
			}
			//Log.v("OnPageFinished","About to report chapters");
			//view.loadUrl("javascript:reportChapters()");
			//Log.v("OnPageFinished","Done reporting chapters");
		
			String s=	(String) view.getTag();
			if(s!=null)
				if(s.equals("web")){
					new Handler(){
						public void handleMessage(android.os.Message msg) {
							pd.dismiss();
							//if(mNotes!=null&&  !mNotes.equals("Home")){
							//	saveNote(mNoteID);
							//}
						};
					}.sendEmptyMessageDelayed(1, 2000);

				}


		}
		@Override
		public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);

		}

		@Override
		public boolean shouldOverrideUrlLoading(final WebView view, String url) { 
			
			aflag= 0;
			if(url.contains("nypl_audio")){
				audioid= url.substring(url.indexOf("nypl_audio-")+11);
			AudioDatalist = AudioDAO.getAudioData(mContext, audioid,mPlaysId);

//
			File extStore = Environment.getExternalStorageDirectory();
			if(AudioDatalist.get(0).getAudioPath()!=null ){
			//if(url.contains(".mp3") || url.contains("/audio") || url.contains("/media") || url.contains(".m4a")  || url.contains(".aac")  || url.contains(".f4a")){
				AudioDatalist = AudioDAO.getAudioData(mContext, audioid,mPlaysId);
				if(mp!=null){
					mp.release();
					///mp.reset();
				}
				PlaysDetailActivity.mAudiolayout.setVisibility(View.GONE);
				
				url = AudioDatalist.get(0).getAudioPath().replace("file://", "").replace("%20", " ").trim();
				File file = new File(url);
				AudioDatalist = AudioDAO.getAudioData(mContext, audioid,mPlaysId);
				if(file.exists()){
					playAudio(AudioDatalist.get(0).getAudioPath().replace("file://", "").replace("%20", " ").trim());

				}else{
					Toast.makeText(mContext, "Audio file unavailable at this location", Toast.LENGTH_SHORT).show();
					//getAudio();
					editAudio();
				}



			}
			
			else if(AudioDatalist.get(0).getAudioPath()==null){
				AudioDatalist = AudioDAO.getAudioData(mContext, audioid,mPlaysId);
				getAudio();
			}
			}
		    else if(url.contains("highlight")){

				mNoteId = url.replace("highlight:", "");


				mNoteSelectDialog=new Dialog(mContext,android.R.style.Theme_Translucent);
				mNoteSelectDialog.getWindow();
				mNoteSelectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				mNoteSelectDialog.setContentView(R.layout.note_popup);
				TextView mUpdateNote=(TextView) mNoteSelectDialog.findViewById(R.id.txt_update_note);
				TextView mDeleteNote=(TextView) mNoteSelectDialog.findViewById(R.id.txt_delete_note);
				TextView mCancelNote=(TextView) mNoteSelectDialog.findViewById(R.id.txt_cancel_note);
				mUpdateNote.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {


						saveNote(mNoteId);
						mNoteSelectDialog.dismiss();
					}
				});
				mDeleteNote.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						mNoteSelectDialog.dismiss();
						int rowUpdated=mContext.getContentResolver().delete(Uri.parse(LibrettoContentProvider.CONTENT_URI+"/"+LibrettoContentProvider.PLAYNOTE_NOTEID), PlayNoteDAO.COLUMN_NAME_NOTE_ID+ "=" + mNoteId, null);
						view.loadUrl("javascript:deletetagValue("+mNoteId+");");
						view.loadUrl("javascript:window.HTMLOUT.showHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
						Toast.makeText(mContext, "Note deleted successfully.", Toast.LENGTH_LONG).show();
					}
				});
				mCancelNote.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {


						mNoteSelectDialog.cancel();
					}
				});
				mNoteSelectDialog.show();
			}
		    else if (url.contains("SheetMusic")){
		    	String SheetMusicID = url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("-"));
		    	Intent i = new Intent(mContext,SheetMusicActivity.class);
				i.putExtra("SheetMusicID",SheetMusicID);
				i.putExtra("position",0);
				i.putExtra("content_location", CONTENT_LOCATION);
				mContext.startActivity(i);
		    }
			 else{
				getVersion(url);
			}

			return true;

		}
	}

	 

	
	


	public static Animation setLayoutAnim_slideup() {

		AnimationSet set = new AnimationSet(true);


		Animation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
				Animation.RELATIVE_TO_SELF, -0.0f);
		animation.setDuration(800);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {

			}
		});
		set.addAnimation(animation);

		LayoutAnimationController controller = new LayoutAnimationController(
				set, 0.25f);

		return animation;
	}

	public static Animation setLayoutAnim_slidedown() {

		AnimationSet set = new AnimationSet(true);
		Animation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, -0.0f,
				Animation.RELATIVE_TO_SELF, 1.0f);
		animation.setDuration(400);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				// MapContacts.this.mapviewgroup.setVisibility(View.VISIBLE);

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub

			}
		});
		set.addAnimation(animation);

		LayoutAnimationController controller = new LayoutAnimationController(
				set, 0.25f);


		return animation;
	}

	public static void playAudio(String id){
		mp=new MediaPlayer();
		if(mp!=null){
			mp.pause();

		}


		utils = new Utilities();

		// Listeners
		PlaysDetailActivity.songProgressBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
				//int totalDuration1 = (Integer.parseInt(AudioDatalist.get(0).getClipTo())*1000 - Integer.parseInt(AudioDatalist.get(0).getClipFrom())*1000);///mp.getDuration();
				int totalDuration1 = mp.getDuration();
				currentPosition1 = utils.progressToTimer(seekBar.getProgress(), totalDuration1);

				if(AudioDatalist!=null){

					if((currentPosition1 == totalDuration1) && (totalDuration1>0))
					{
						mp.pause();
						mp.stop();
						mHandler.removeCallbacks(ViewPagerAdapter.mUpdateTimeTask);
						Animation slideUp = setLayoutAnim_slidedown(); 
						PlaysDetailActivity.mAudiolayout.startAnimation(slideUp);
						PlaysDetailActivity.mAudiolayout.setVisibility(View.GONE);


					}
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				mHandler.removeCallbacks(mUpdateTimeTask);

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mHandler.removeCallbacks(mUpdateTimeTask);
				//int totalDuration =(Integer.parseInt(AudioDatalist.get(0).getClipTo())*1000 - Integer.parseInt(AudioDatalist.get(0).getClipFrom())*1000);// mp.getDuration();//
				int totalDuration = mp.getDuration();
				int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

				// forward or backward to certain seconds
				mp.seekTo(currentPosition);

				if(currentPosition >= totalDuration)
				{
					mp.pause();
					mp.stop();
					mHandler.removeCallbacks(ViewPagerAdapter.mUpdateTimeTask);
					Animation slideUp = setLayoutAnim_slidedown(); 
					PlaysDetailActivity.mAudiolayout.startAnimation(slideUp);
					PlaysDetailActivity.mAudiolayout.setVisibility(View.GONE);


				}
				updateProgressBar();

			}

		}); // Important
		mp.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				///	mp.reset();
				mp.stop();
				//mp.release(); 
				//mp.pause();
				//mp=null;
				mHandler.removeCallbacks(ViewPagerAdapter.mUpdateTimeTask);
				Animation slideUp = setLayoutAnim_slidedown(); 
				PlaysDetailActivity.mAudiolayout.startAnimation(slideUp);

				PlaysDetailActivity.mAudiolayout.setVisibility(View.GONE);
				///mAudioDialog.cancel();
			}
		}); // Important

		// By default play first song
		playSong(id);

		/**
		 * Play button click event
		 * plays a song and changes button to pause image
		 * pauses a song and changes button to play image
		 * */
		PlaysDetailActivity.btnPlay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				long totalDuration = 0;
				//long currentDuration = 0;
				//if(aflag==0){
					
				
				//totalDuration = Integer.parseInt(AudioDatalist.get(0).getClipTo())*1000 - Integer.parseInt(AudioDatalist.get(0).getClipFrom())*1000;///mp.getDuration();//Integer.parseInt(AudioDatalist.get(0).getClipTo())*1000;///mp.getDuration(); ///
				
				totalDuration = mp.getDuration();
				//currentDuration = mp.getCurrentPosition();
				//}
				if(currentPosition1 >= totalDuration)
				{
					mp.release();
					aflag = 1; 
					mHandler.removeCallbacks(ViewPagerAdapter.mUpdateTimeTask);
					Animation slideUp = setLayoutAnim_slidedown(); 
					PlaysDetailActivity.mAudiolayout.startAnimation(slideUp);
					PlaysDetailActivity.mAudiolayout.setVisibility(View.GONE);


				}
				else if(mp.isPlaying()){
					if(mp!=null){
						mp.pause();
						// Changing button image to play button
						PlaysDetailActivity.btnPlay.setImageResource(R.drawable.btn_audio_play);
						
					}
				}else{
					// Resume song
					if(mp!=null){
						mp.start();
						// Changing button image to pause button
						PlaysDetailActivity.btnPlay.setImageResource(R.drawable.btn_pause);
						
						
					}
				}

			}
		});
		PlaysDetailActivity.mPlayerCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mp.release();
				//mp.stop();
				mHandler.removeCallbacks(ViewPagerAdapter.mUpdateTimeTask);
				Animation slideUp = setLayoutAnim_slidedown(); 
				PlaysDetailActivity.mAudiolayout.startAnimation(slideUp);
				PlaysDetailActivity.mAudiolayout.setVisibility(View.GONE);

			}
		});
		PlaysDetailActivity.mPlayerEditAudio.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mp.release();
				//mp.stop();
				mHandler.removeCallbacks(ViewPagerAdapter.mUpdateTimeTask);
				Animation slideUp = setLayoutAnim_slidedown(); 
				PlaysDetailActivity.mAudiolayout.startAnimation(slideUp);
				PlaysDetailActivity.mAudiolayout.setVisibility(View.GONE);
				
				editAudio();
			}
		});

	}
	
	/**
	 * Function to play a song
	 * @param songIndex - index of song
	 * */
	public static void  playSong(String songIndex){
		// Play song


		String songTitle = null ;
		try {
			
			mp.reset();
			
			

			///songIndex.trim()
			if(songIndex.contains("external") ){
				String  filePath ;
				Uri parcialUri = Uri.parse("content://media"+songIndex);
				Cursor cursor = mContext.getContentResolver().query(parcialUri, new String[] { android.provider.MediaStore.Audio.AudioColumns.DATA }, null, null, null);//("content://media/external/audio/media/4743.mp3", new String[] { android.provider.MediaStore.Audio.AudioColumns.DATA }, null, null, null);
				cursor.moveToFirst();   
				filePath = cursor.getString(0);
				mp.setDataSource(filePath);
				cursor.close();
			}else{
				mp.setDataSource(songIndex.trim());
			}

			songTitle=songIndex;
			//}
		
			mp.prepare();
			if(mp.getDuration()>Integer.parseInt(AudioDatalist.get(0).getClipFrom())*1000){
				PlaysDetailActivity.mAudiolayout.setVisibility(View.VISIBLE);
				Animation slideUp = setLayoutAnim_slideup(); 
				PlaysDetailActivity.mAudiolayout.startAnimation(slideUp);
				mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			        @Override
			        public void onPrepared(MediaPlayer mp) {
			            // TODO Auto-generated method stub

			            mp.start();

			        }
			    });
			if(AudioDatalist!=null){
				//mp.seekTo(0);
				mp.seekTo(Integer.parseInt(AudioDatalist.get(0).getClipFrom())*1000);///
			}
			
			mp.start();
		
			PlaysDetailActivity.btnPlay.setImageResource(R.drawable.btn_pause);

			PlaysDetailActivity.songProgressBar.setProgress(0);
			PlaysDetailActivity.songProgressBar.setMax(100);

			updateProgressBar();	
			}else{
				PlaysDetailActivity.mAudiolayout.setVisibility(View.GONE);
				Toast.makeText(mContext, "Audio length is short. ", Toast.LENGTH_LONG).show();
				}
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			PlaysDetailActivity.mAudiolayout.setVisibility(View.GONE);

			getAudio();			
			e.printStackTrace();
		}
	}
	/**
	 * Update timer on seekbar
	 * */
	public static void updateProgressBar() {


		mHandler.postDelayed(mUpdateTimeTask, 1);


	}	


	/**
	 * Background Runnable thread
	 * */
	public static Runnable mUpdateTimeTask = new Runnable() {

		public void run() {
			try {
				if(mp!=null){
					//long totalDuration = Integer.parseInt(AudioDatalist.get(0).getClipTo())*1000 - Integer.parseInt(AudioDatalist.get(0).getClipFrom())*1000;///mp.getDuration();//Integer.parseInt(AudioDatalist.get(0).getClipTo())*1000;///mp.getDuration(); ///
					long totalDuration = mp.getDuration();
					long currentDuration = mp.getCurrentPosition();//Integer.parseInt(AudioDatalist.get(0).getClipFrom())*1000;//mp.getCurrentPosition();//Integer.parseInt(AudioDatalist.get(0).getClipFrom());///
					
					// Displaying Total Duration time
					
					
					if(totalDuration>0){
						PlaysDetailActivity.songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
						
						//PlaysDetailActivity.songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(Integer.parseInt(AudioDatalist.get(0).getClipTo())*1000));
					}else{
						PlaysDetailActivity.songTotalDurationLabel.setText("0:00");
					}
					// Displaying time completed playing
					PlaysDetailActivity.songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

					// Updating progress bar
					//int progress = (int)(utils.getProgressPercentage(currentDuration, Integer.parseInt(AudioDatalist.get(0).getClipTo())*1000));
					int progress = (int)(utils.getProgressPercentage(currentDuration, mp.getDuration()));
					
					
					//Log.d("Progress", ""+progress);
					PlaysDetailActivity.songProgressBar.setProgress(progress);
					
					if(currentDuration == totalDuration)
					{
						mp.pause();
						mp.stop();
						mHandler.removeCallbacks(ViewPagerAdapter.mUpdateTimeTask);
						Animation slideUp = setLayoutAnim_slidedown(); 
						PlaysDetailActivity.mAudiolayout.startAnimation(slideUp);
						PlaysDetailActivity.mAudiolayout.setVisibility(View.GONE);


					}
					mHandler.postDelayed(this, 1);


				}
				
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}

	};


	public static void getAudio(){

		PlaysDetailActivity.mAudiolayout.setVisibility(View.GONE);
		mAudioDialog=new Dialog(mContext,android.R.style.Theme_Translucent);
		mAudioDialog.getWindow();
		mAudioDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mAudioDialog.setContentView(R.layout.audio_popup);
		TextView mPlayFromLibrary=(TextView) mAudioDialog.findViewById(R.id.txt_import);
		TextView mPlayPurchase=(TextView) mAudioDialog.findViewById(R.id.txt_purchase);
		TextView mPlayCancel=(TextView) mAudioDialog.findViewById(R.id.txt_cancel);
		mPlayCancel.setVisibility(ViewGroup.VISIBLE);
		mPlayFromLibrary.setVisibility(ViewGroup.VISIBLE);
		mPlayPurchase.setVisibility(ViewGroup.VISIBLE);
		//TextView mPlayFromLibrary=(TextView) mAudioDialog.findViewById(R.id.txt_import);
		mPlayFromLibrary.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pickAudio();
			}
		});
		mPlayCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mAudioDialog.cancel();
			}
		});
		mAudioDialog.show();
	}


	public static void playAttachedAudio(final String url ){
		PlaysDetailActivity.mAudiolayout.setVisibility(View.GONE);
		mAudioDialog=new Dialog(mContext,android.R.style.Theme_Translucent);
		mAudioDialog.getWindow();
		mAudioDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mAudioDialog.setContentView(R.layout.audio_popup);
		TextView mPlayAttached=(TextView) mAudioDialog.findViewById(R.id.txt_play);
		TextView mPlayDelete=(TextView) mAudioDialog.findViewById(R.id.txt_delete);
		mPlayAttached.setVisibility(ViewGroup.VISIBLE);
		mPlayDelete.setVisibility(ViewGroup.VISIBLE);
		mPlayAttached.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				playAudio(url.replace(audioid, ""));
				mAudioDialog.cancel();
			}
		});
		mPlayDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				///((SelectionWebView) mFocusedPage).SetAudio(savedUri,HTMLFileName);
				mPlayDetailView.loadUrl("javascript:deletetagValueAudio("+audioid+");");
				mPlayDetailView.loadUrl("javascript:window.HTMLOUT.showHTML1('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
				Toast.makeText(mContext, "Audio deleted successfully.", Toast.LENGTH_LONG).show();
				mAudioDialog.cancel();
			}
		});
		mAudioDialog.show();
	}

	public static void updateAudio(String savedUri){
		String URLID;
		String clip_id=AudioDatalist.get(0).getClipID().toString();
		if(savedUri.contains("external"))
		{
		 Uri partialUri = Uri.parse("content://media"+savedUri);
		 Cursor cursor = mContext.getContentResolver().query(partialUri, new String[] { android.provider.MediaStore.Audio.AudioColumns.DATA }, null, null, null);//("content://media/external/audio/media/4743.mp3", new String[] { android.provider.MediaStore.Audio.AudioColumns.DATA }, null, null, null);
         cursor.moveToFirst();   
         URLID = cursor.getString(0);
         cursor.close();
		}else{
			URLID = savedUri;
		}
		/*try{
		URLID=URLEncoder.encode(URLID, "ISO-8859-1");
		}
		catch (Exception e){
		}*/
		ContentValues cv =new ContentValues();
		cv.put(AudioDAO.COLUMN_NAME_AUDIO_PATH, URLID);
		//int rowUpdated=mContext.getContentResolver().update(Uri.parse(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.AUDIO_PATH), cv, AudioDAO.COLUMN_NAME_AUDIO_CLIP_ID+"='"+clip_id+"' and ("+ AudioDAO.COLUMN_NAME_AUDIO_VERSION_ID + "='" + Version +"' and "+ AudioDAO.COLUMN_NAME_AUDIO_PLAY_ID + "='" + mPlaysId+"')" , null);
		int rowUpdated=mContext.getContentResolver().update(Uri.parse(LibrettoContentProvider.CONTENT_URI+"/"+LibrettoContentProvider.AUDIO_PATH), cv, AudioDAO.COLUMN_NAME_AUDIO_CLIP_ID+"='"+clip_id+"' and "+ AudioDAO.COLUMN_NAME_AUDIO_PLAY_ID + "='" + mPlaysId+"'" , null);
		
		
	
	}
	public static void pickAudio() {
		Intent intent = new Intent();

		intent.setType("audio/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		((FragmentActivity) mContext).startActivityForResult(intent, REQUEST_CODE);
		if (mEditAudioDialog!=null){
		mEditAudioDialog.cancel();}
		//mAudioDialog.cancel();
	}
	

	private void getVersion(final String mVersion){
		///	versionDetailList = VersionDAO.getVersionOf(PlaysDetailActivity.this, mPlaysId);

		mVersionDialog=new Dialog(mContext,android.R.style.Theme_Translucent);
		mVersionDialog.getWindow();
		mVersionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mVersionDialog.setContentView(R.layout.popup_plays_version);
		ListView mVersionList=(ListView) mVersionDialog.findViewById(R.id.s_play_version_list);
		///mNotes=null;
		////mSearchNote= null; 
		mVersionList.setAdapter(new VersionListAdapter(mVersionDetailList));
		mVersionList.setOnItemClickListener(new OnItemClickListener() {



			private ArrayList<PlaysBean> PlayName;

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(mp!=null)
					//mp.pause();
					mp.release();
				mp=null;
				PlaysDetailActivity.mAudiolayout.setVisibility(View.GONE);

				mVersionDialog.cancel();
				PlayName = PlayDAO.getPlayName(mContext, mVersionDetailList.get(arg2).getVersionPlayID());
				((Activity) mContext).finish();
				Intent i = new Intent(mContext,PlaysDetailActivity.class);
				i.putExtra("playsId",mVersionDetailList.get(arg2).getVersionPlayID());
				i.putExtra("playsName",PlayName.get(0).getPlayName());
				i.putExtra("position",arg2);
				i.putExtra("mVersion",mVersionDetailList.get(arg2).getVersionID());
				i.putExtra("VersionHtmlID",mVersion);
				i.putExtra("mNote","Home");


				mContext.startActivity(i);


			}
		});
		mVersionDialog.show();

	}





	@Override
	public void destroyItem(View collection, int position, Object view) {
		mChilds[position] = null;
		((ViewPager) collection).removeView((View) view);
	}



	@Override
	public boolean isViewFromObject(View view, Object object) {

		return view==((View)object);

	}



	@Override
	public void finishUpdate(View arg0) {

	}


	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {


	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {}


	public static void saveNote(final String mNoteId){
		mPlayNoteDialog=new Dialog(mContext,R.style.FullHeightDialog);
		mPlayNoteDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		mPlayNoteDialog.setContentView(R.layout.popup_notes);
		mNoteText=(EditText) mPlayNoteDialog.findViewById(R.id.popup_note_txt);
		mSaveBtn= (Button) mPlayNoteDialog.findViewById(R.id.btn_save);
		mCancelBtn= (Button) mPlayNoteDialog.findViewById(R.id.btn_cancel);

		playNoteList=PlayNoteDAO.getNoteDetail(mContext,mNoteId);
		if(playNoteList.get(0).getNotes()!=null ){
			mNoteText.setText(playNoteList.get(0).getNotes());
			mNoteText.setSelection(playNoteList.get(0).getNotes().length());
		}

		if( playNoteList.get(0).getNotes().length()>0 && playNoteList.get(0).getNotes()!=null){
			mSaveBtn.setText("Update");
		}
		mSaveBtn.setOnClickListener(new OnClickListener() {
			private ImageView mPlayNote;
			@Override
			public void onClick(View v) {
				String textString = mNoteText.getText().toString(); 
				if (textString != null && textString.trim().length() ==0)
				{
					Toast.makeText(mContext, "Please enter note text", Toast.LENGTH_LONG).show();
				} else
				{
					ContentValues cv =new ContentValues();
					cv.put(PlayNoteDAO.COLUMN_NAME_NOTE_PLAY_NOTE, textString.trim());

					int rowUpdated=mContext.getContentResolver().update(Uri.parse(LibrettoContentProvider.CONTENT_URI+"/"+LibrettoContentProvider.PLAYNOTE_NOTEID), cv, PlayNoteDAO.COLUMN_NAME_NOTE_PLAY_ID + "=" + playNoteList.get(0).getPlayID()+" "+ "and"+ " " + PlayNoteDAO.COLUMN_NAME_NOTE_ID+ "=" + mNoteId, null);
					mPlayNoteDialog.cancel();
					if(rowUpdated>0){

						if(playNoteList.get(0).getNotes().length()>0 &&  playNoteList.get(0).getNotes()!=null){
							Toast.makeText(mContext, "Note updated successfully.", Toast.LENGTH_LONG).show();
							//PlaysDetailActivity.mPlayNote.setImageResource(drawable.img_notes);
						}else{
							//PlaysDetailActivity.mPlayNote.setImageResource(drawable.img_notes);
							Toast.makeText(mContext, "Note saved successfully.", Toast.LENGTH_LONG).show();
						}
					}
				}

			}
		});
		mCancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPlayNoteDialog.cancel();
			}
		});
		InputMethodManager inputMethodManager=(InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null){
			mPlayNoteDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		} 
		mPlayNoteDialog.show();

	}

	public static  void editAudio(){

		PlaysDetailActivity.mAudiolayout.setVisibility(View.GONE);
		mEditAudioDialog=new Dialog(mContext,android.R.style.Theme_Translucent);
		mEditAudioDialog.getWindow();
		mEditAudioDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mEditAudioDialog.setContentView(R.layout.edit_audio_popup);
		TextView mRestoreAudio=(TextView) mEditAudioDialog.findViewById(R.id.txt_restore_audio);
		TextView mEditAudio=(TextView) mEditAudioDialog.findViewById(R.id.txt_edit_audio);
		//TextView mDeleteAudio=(TextView) mEditAudioDialog.findViewById(R.id.txt_remove);
		TextView mCancel=(TextView) mEditAudioDialog.findViewById(R.id.txt_cancel);
		
		//TextView mPlayFromLibrary=(TextView) mAudioDialog.findViewById(R.id.txt_import);
		mEditAudio.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pickAudio();
				mEditAudioDialog.cancel();
			}
		});
		mRestoreAudio.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String clip_id=AudioDatalist.get(0).getClipID().toString();	
				String filePath = "file:///"+FilePath.getAbsolutePath() + File.separator+ CONTENT_LOCATION + File.separator +"Audio"+File.separator+clip_id+".mp3";
				updateAudio(filePath);
				mEditAudioDialog.cancel();
				//Toast.makeText(mContext, "Audio Successfully Deleted.", Toast.LENGTH_LONG).show();
			}
		});
		mCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mEditAudioDialog.cancel();
			}
		});
		mEditAudioDialog.show();
	}
     

	public class MyJavaScriptInterface   
	{  

		@SuppressWarnings("unused")  
		public void showHTML(String html)  
		{  
			File cacheDir;

			cacheDir=new File(android.os.Environment.getExternalStorageDirectory(), "HTMLContent");
			xml_file_path = new File(cacheDir,PlaysDetailActivity.HTMLFileName);

			FileOutputStream fos;
			byte[] data = new String(html).getBytes();
			try {
				fos = new FileOutputStream(xml_file_path);
				fos.write(data);
				fos.flush();
				fos.close();


			} catch (FileNotFoundException e) {
				// handle exception
			} catch (IOException e) {
				// handle exception
			}


		}  


		@SuppressWarnings("unused")  
		public void showHTML1(String html)  
		{  
			File cacheDir;

			cacheDir=new File(android.os.Environment.getExternalStorageDirectory(), "HTMLContent");
			xml_file_path = new File(cacheDir,PlaysDetailActivity.HTMLFileName);

			FileOutputStream fos;
			byte[] data = new String(html).getBytes();
			try {
				fos = new FileOutputStream(xml_file_path);
				fos.write(data);
				fos.flush();
				fos.close();


			} catch (FileNotFoundException e) {
				// handle exception
			} catch (IOException e) {
				// handle exception
			}
			/*
			new AlertDialog.Builder(mContext)  
			.setTitle("HTML")  
			.setMessage(html)  
			.setPositiveButton(android.R.string.ok, null)  
			.setCancelable(false)  
			.create()  
			.show(); */

		}  

	} 



}
