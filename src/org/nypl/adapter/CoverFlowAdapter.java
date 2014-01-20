package org.nypl.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.nypl.CoverFlow;
import org.nypl.R;
import org.nypl.dataholder.PlaysBean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class CoverFlowAdapter extends BaseAdapter{
	int mGalleryItemBackground;
	private Context mContext;

	private FileInputStream fis;
	public static String CONTENT_LOCATION ;
	private static File FilePath = Environment.getExternalStorageDirectory();
	
	private Integer[] mImageIds = {
			//R.drawable.kasabian_kasabian,
				//  R.drawable.kasabian_kasabian,
			
	};

	private ImageView[] mImages;
	private int mChildWidth = 0;
	private int mChildHeight = 0;
	private int mCoverWidth = 0;
	private int mCoverHeight = 0;
	
	private ArrayList<PlaysBean> playsList;

	public CoverFlowAdapter(ArrayList<PlaysBean> playsList, Context c) {
		this.playsList=playsList;
		mContext = c;
		// mImages = new ImageView[mImageIds.length];
	}

	public int getCount() {
		// return Integer.MAX_VALUE;
		Log.v("playsList.size()",""+playsList.size());
		return playsList.size();
	}

	public Object getItem(int position) {
		/* if (position >= (playsList.size()-1) ) {
   		  Log.v("position2",""+(playsList.size()-1));
   	   position = position % (playsList.size()-1);
   	  }
         return position;*/
		return position;
	}

	public long getItemId(int position) {
		/* if (position >= (playsList.size()-1) ) {
   		  Log.v("position2",""+(playsList.size()-1));
   	   position = position % (playsList.size()-1);
   	  }
         return position;*/
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Log.v("position",""+position);
		//Log.v("position",""+position);
		//position = checkPosition( position);
		mChildWidth  = (int)mContext.getResources().getDimension(R.dimen.carousel_image_width);
		mChildHeight = (int)mContext.getResources().getDimension(R.dimen.carousel_image_height);
		
		mCoverWidth  = (int)mContext.getResources().getDimension(R.dimen.cover_image_width);
		mCoverHeight = (int)mContext.getResources().getDimension(R.dimen.cover_image_height);
		//Use this code if you want to load from resources
		ImageView i = new ImageView(mContext);
		i.setBackgroundResource(R.drawable.bigframe);

		///i.setImageBitmap(getBitmapFromAsset(playsList.get(0).getPlayImage().toString()));

		CONTENT_LOCATION = "Android/data/"+mContext.getPackageName()+File.separator+"contents";
		
		InputStream bitmap=null;
		try {
		    Log.v("CFA","before");
		    Log.v("CFA",FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+playsList.get(position).getPlayImage().toString());
		//	bitmap=mContext.getResources().getAssets().open(playsList.get(0).getPlayImage().toString());
		    bitmap = new FileInputStream(FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+playsList.get(position).getPlayID()+File.separator+playsList.get(position).getPlayImage().toString());
		    //bitmap = mContext.openFileInput(FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+playsList.get(0).getPlayImage().toString());  
		    Log.v("bitmap",""+bitmap);
			Bitmap bit=BitmapFactory.decodeStream(bitmap);
			Bitmap scaled = Bitmap.createScaledBitmap(bit, mCoverWidth, mCoverHeight, true);

			i.setImageBitmap(scaled);
		} catch (IOException e1) {
			
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally{
			try {
				bitmap.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		i.setLayoutParams(new CoverFlow.LayoutParams(mChildWidth, mChildHeight));
	
		i.setScaleType(ImageView.ScaleType.CENTER_INSIDE); 
		return i;

	}

	public int checkPosition(int position) {
		Log.v("position1",""+mImageIds.length);
		if (position >= (mImageIds.length-1) ) {
			Log.v("position2",""+(mImageIds.length-1));
			position = position % (mImageIds.length-1);
		}
		return position;
	}




}

