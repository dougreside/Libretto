package org.nypl.libretto;

import org.nypl.libretto.R;
import org.nypl.libretto.adapter.ViewPagerAdapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class BookmarkActivity extends SherlockFragmentActivity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmark_activity);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		//((ImageView)findViewById(R.id.s_background_img)).setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.img_background, options)));
		if(ViewPagerAdapter.mp!=null){
			ViewPagerAdapter.mp.release();
		ViewPagerAdapter.mHandler.removeCallbacks(ViewPagerAdapter.mUpdateTimeTask);
		}
	}


	@Override
	protected void onResume()
	{
		
		if(ViewPagerAdapter.mp!=null){
			ViewPagerAdapter.mp.release();
		ViewPagerAdapter.mHandler.removeCallbacks(ViewPagerAdapter.mUpdateTimeTask);
		}
		super.onResume();
		//Restore state here
	}


}
