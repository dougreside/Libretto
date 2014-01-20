
package org.nypl;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import org.nypl.adapter.CoverFlowAdapter;
import org.nypl.adapter.ViewPagerAdapter;
import org.nypl.database.PlayDAO;
import org.nypl.dataholder.PlaysBean;
import org.nypl.utils.ZipExtracter;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class HomeActivity extends FragmentActivity implements OnEditorActionListener{
	public static String CONTENT_LOCATION ;
	Context mCtx;
	private File FilePath = Environment.getExternalStorageDirectory();
	private CoverFlow mCoverFlow;
	private int mCarouselSpacing =0;
	ArrayList<PlaysBean> playsList = null;
	/** Called when the activity is first created. */
	private TextView mCoverFlowText;
	private TextView mPlayName;
	private TextView mAuthorName;
	private LinearLayout mFullTextLayout;
	private int mPosition=0;
	private EditText mSearchPlay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		//((ImageView)findViewById(R.id.s_home_background_img)).setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.s_home_bg, options)));
		NavigationActivity.init(this, 0);
		refreshCarousel();


	}
	@Override
	protected void onPause()
	{
		super.onPause();
		//Save state here
		/*if(ViewPagerAdapter.mp.isPlaying())
			ViewPagerAdapter.mp.pause();*/
		    ///ViewPagerAdapter.mp.release();
	}

	@Override
	protected void onResume()
	{
		mSearchPlay.setText("");
		System.out.println("Back again...");
		refreshCarousel();
		/*if(ViewPagerAdapter.mp.isPlaying())
			ViewPagerAdapter.mp.pause();*/
		    ///ViewPagerAdapter.mp.release();
		/*new Handler(){
			public void handleMessage(android.os.Message msg) {
		ViewPagerAdapter.player.reset();
			};
		}.sendEmptyMessageDelayed(1, 1000);*/
		if(ViewPagerAdapter.mp!=null){
			///ViewPagerAdapter.mp.pause();
		ViewPagerAdapter.mp.release();
		ViewPagerAdapter.mHandler.removeCallbacks(ViewPagerAdapter.mUpdateTimeTask);
		}
		super.onResume();
		//Restore state here
	}

	
	
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_SEARCH ||
				actionId == EditorInfo.IME_ACTION_DONE ||
				event.getAction() == KeyEvent.ACTION_DOWN &&
				event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			InputMethodManager inputMgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMgr.toggleSoftInput(0, 0);
			///	isAuntheticate();
			final String currentString = mSearchPlay.getText().toString().trim();
			 if(!isWhitespace(currentString)){
				 Intent i = new Intent(HomeActivity.this,PlaysListActivity.class);
					i.putExtra("searchtext",currentString);
				
					startActivity(i);
					return true;
			 }else{
				 Toast.makeText(this, "Please enter text.", Toast.LENGTH_LONG).show();
			 }
			/////mSearchplaysList = PlayDAO.getAllSearchPlays(HomeActivity.this,currentString);
			
		}
		return false;

	}
	public void refreshCarousel(){

		
		CONTENT_LOCATION = "Android/data/"+this.getPackageName()+File.separator+"contents";
		mCtx = this;

		
		
		
		mCoverFlow = (CoverFlow) findViewById(R.id.coverflow); 
		mCoverFlowText = (TextView) findViewById(R.id.s_home_coverflowtext);
		mPlayName = (TextView) findViewById(R.id.s_home_play_name);
		mAuthorName = (TextView) findViewById(R.id.s_home_author_name);
		mFullTextLayout = (LinearLayout) findViewById(R.id.s_home_fulltext_layout);

		mSearchPlay =(EditText) findViewById(R.id.s_home_serch_text);
	
		mSearchPlay.setOnEditorActionListener(this);
		
		playsList = PlayDAO.getFeaturedPlays(HomeActivity.this);
		if (playsList.size()<1){
			mPlayName.setText("No plays available");
			mAuthorName.setText("Add a play using the menu below.");
		    mFullTextLayout.setVisibility(View.GONE);
			
		}
		else{
		CoverFlowAdapter coverImageAdapter =  new CoverFlowAdapter(playsList,this);
		mCoverFlow.setAdapter(coverImageAdapter);
	
		mCarouselSpacing  = -((int)getResources().getDimension(R.dimen.carousel_image_spacing));
		mCoverFlow.setSpacing(mCarouselSpacing);
		mCoverFlow.setSelection(0, true);
		mCoverFlow.setAnimationDuration(300);

		mPlayName.setText(playsList.get(0).getPlayName().toString());
		mAuthorName.setText(playsList.get(0).getPlayAuthors().toString());

		
		mCoverFlow.setOnItemSelectedListener(new OnItemSelectedListener() {
		
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				mPlayName.setText(playsList.get(position).getPlayName().toString());
				if (playsList.get(position).getPlayAuthors()!=null){
				mAuthorName.setText(playsList.get(position).getPlayAuthors().toString());
				}
				else{
					mAuthorName.setText("");
				}
				mPosition=position;
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				mPlayName.setText(playsList.get(0).getPlayName().toString());


			}
			
		});
		mCoverFlow.setOnItemClickListener(new OnItemClickListener() {

			private String mVersion = null;
            private String mNote = "Home";
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i = new Intent(HomeActivity.this,PlaysDetailActivity.class);
				i.putExtra("playsId",playsList.get(mPosition).getPlayID());
				i.putExtra("playsName",playsList.get(mPosition).getPlayName());
				i.putExtra("mNote",mNote);
				i.putExtra("position",mPosition);
				i.putExtra("mVersion",playsList.get(mPosition).getPlayVersionID() );
				startActivity(i);

			}
		});
		Log.v("mposition",""+mPosition);
		mFullTextLayout.setOnClickListener(new OnClickListener() {

			private String mVersion = null;
			private String mNote = "Home";

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.v("playsList.get(mPosition).getPlayID()",""+playsList.get(mPosition).getPlayID());
				Intent i = new Intent(HomeActivity.this,PlaysDetailActivity.class);
				i.putExtra("playsId",playsList.get(mPosition).getPlayID());
				i.putExtra("playsName",playsList.get(mPosition).getPlayName());
				i.putExtra("position",mPosition);
				i.putExtra("mNote",mNote );
				i.putExtra("mVersion",playsList.get(mPosition).getPlayVersionID() );
				startActivity(i);
				
			}
		});
		/*    FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
         Bundle bundle = new Bundle();
        HomeFragment homeFragment = (HomeFragment) HomeFragment.instantiate(this, "home", bundle);
        fragmentTransaction.add(R.id.blank, homeFragment);
        fragmentTransaction.commit();*/

		
	}
	}

	public boolean isWhitespace(String str) {
		if (str == null) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}//end of isWhitespace

	
	


}