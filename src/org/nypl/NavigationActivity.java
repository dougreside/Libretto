package org.nypl;




import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class NavigationActivity {

	private int mTabNumber;
	private ImageView mPlayImage;
	private ImageView mAnnotateImage;
	private ImageView mBookmarkImage;
	private ImageView mAboutImage;
	private Activity mActivity;
	private LinearLayout mPlayNavigation;
	private LinearLayout mAnnotateNavigation;
	private LinearLayout mBookmarkNavigation;
	private LinearLayout mAboutNavigation;

	public static void init(Activity activity, int tabNumber) {
		new NavigationActivity(activity, tabNumber);

	}
	private NavigationActivity(Activity activity, int number){
		this.mActivity = activity;
		this.mTabNumber = number;
		initNavigationBar(mTabNumber);
	}

	public  void initNavigationBar(int tab){
		mPlayImage = (ImageView) mActivity.findViewById(R.id.s_navigation_img_play);
		mAnnotateImage = (ImageView) mActivity.findViewById(R.id.s_navigation_img_annotate);
	//	mBookmarkImage = (ImageView) mActivity.findViewById(R.id.s_navigation_img_bookmark);
		mAboutImage = (ImageView) mActivity.findViewById(R.id.s_navigation_img_about);
		mPlayNavigation =(LinearLayout) mActivity.findViewById(R.id.s_navigation_play);
		mAnnotateNavigation =(LinearLayout) mActivity.findViewById(R.id.s_navigation_annotate);
	//	mBookmarkNavigation =(LinearLayout) mActivity.findViewById(R.id.s_navigation_bookmark);
		mAboutNavigation =(LinearLayout) mActivity.findViewById(R.id.s_navigation_about);

		setListeners();
		switch(tab){
		case 1:
			mPlayImage.setImageResource(R.drawable.img_plays);
			break;
		case 2:
			mAnnotateImage.setImageResource(R.drawable.img_annotate);
			break;
		/*case 3:
			mBookmarkImage.setImageResource(R.drawable.img_bookmarks);
			break;*/
		case 4:
			mAboutImage.setImageResource(R.drawable.img_about);
			break;
		}//end of switch
	}
	/**
	 * Listener method
	 */
	public void setListeners(){
		mPlayNavigation.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String searchtext= null;
				Intent i=new Intent();
				///((Activity)v.getContext()).finish();
				i.setClass(v.getContext(), PlaysListActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				i.putExtra("searchtext",searchtext);
				v.getContext().startActivity(i);
			}
		});//one
	
		mAnnotateNavigation.setOnClickListener(new View.OnClickListener() {
			

			public void onClick(View v) {
				Intent i=new Intent();
				//((Activity)v.getContext()).finish();
				i.setClass(v.getContext(), PlaysAddActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			
				v.getContext().startActivity(i);
			}
		});//two
		/*
		mBookmarkNavigation.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i=new Intent();
				//((Activity)v.getContext()).finish();
				i.setClass(v.getContext(), BookmarkActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				v.getContext().startActivity(i);
			}
		});//three*/

		mAboutNavigation.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i=new Intent();
				//((Activity)v.getContext()).finish();
				i.setClass(v.getContext(), PlaysAboutActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				v.getContext().startActivity(i);
			}
		});//four



	}// end of set listener method
	
}
