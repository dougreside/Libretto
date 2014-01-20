package org.nypl.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.nypl.R;
import org.nypl.dataholder.PlaysBean;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 import android.content.ContextWrapper;

public class PlaysListAdapter extends BaseExpandableListAdapter{

	private Context mContext;
	private HashMap<String, Object> mPlaysList;
	private ArrayList<String> mKeyList;
	private ArrayList<PlaysBean> mPlaysNameList;
	private int mListCoverWidth;
	private int mListCoverHeight;
	private int mFlag = 0;
	private static File FilePath = Environment.getExternalStorageDirectory();
	private static String CONTENT_LOCATION;
	//public final static String ALPHABATES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public PlaysListAdapter(HashMap<String, Object> playsList,	Context c, ArrayList<String> keyList,int flag) {
		this.mPlaysList=playsList;
		this.mContext=c;
		this.mKeyList=keyList;
		this.mFlag  = flag;


		// TODO Auto-generated constructor stub
	}

	@Override
	public Object getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {

		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,	boolean isLastChild, View convertView, ViewGroup parent) {
		mListCoverWidth  = (int)mContext.getResources().getDimension(R.dimen.list_cover_image_width);
		mListCoverHeight = (int)mContext.getResources().getDimension(R.dimen.list_cover_image_height);
		ImageView mPlayImage = null;
		TextView mPlayName = null ;
		TextView mPlayAuthorName = null  ;
		InputStream bitmap =null;
		ViewHolder holder  = null;
		if(convertView==null){
			//convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.e_list_layout, null);
			holder = new ViewHolder(); 
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.e_list_layout, null);
				
			holder.img1 = (ImageView) convertView.findViewById(R.id.e_list_layout_play_image);
			holder.txt1 =(TextView) convertView.findViewById(R.id.e_list_layout_play_name);
			holder.txt2 =(TextView) convertView.findViewById(R.id.e_list_layout_author_name);
			//holder.txt3 = (TextView) convertView.findViewById(R.id.e_list_layout_delete_play);
			//holder.txt3.setTag(groupPosition);
			convertView.setTag(holder);     

		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		mPlaysNameList=	(ArrayList<PlaysBean>) mPlaysList.get(mKeyList.get(groupPosition));
		CONTENT_LOCATION = "Android/data/"+mContext.getPackageName()+File.separator+"contents";
		try {
			System.out.println(mPlaysNameList.get(childPosition).getPlayID().toString());
			 final File mfreeChapterFolder = new File(FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator
						+mPlaysNameList.get(childPosition).getPlayID().toString());
		     if (mfreeChapterFolder.exists()||(mPlaysNameList.get(childPosition).getPlayID().toString().contentEquals("About")))  {
				
			bitmap = new FileInputStream(FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator
					//+mPlaysNameList.get(childPosition).getPlayID()+File.separator
					+mPlaysNameList.get(childPosition).getPlayImage().toString());
			//bitmap=mContext.getResources().getAssets().open(mPlaysNameList.get(childPosition).getPlayImage());
			Bitmap bit=BitmapFactory.decodeStream(bitmap);
			Bitmap scaled = Bitmap.createScaledBitmap(bit, mListCoverWidth, mListCoverHeight, true);

			holder.img1.setImageBitmap(scaled);
		     }
		     else{
		    	 AssetManager assetManager = this.mContext.getAssets();
		    	 bitmap = assetManager.open("download.png");
		    	 Bitmap bit=BitmapFactory.decodeStream(bitmap);
					Bitmap scaled = Bitmap.createScaledBitmap(bit, mListCoverWidth, mListCoverHeight, true);

					holder.img1.setImageBitmap(scaled);
		     }
			Log.v("bitmap",""+bitmap);
		
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		holder.txt1.setText(mPlaysNameList.get(childPosition).getPlayName().toString());
		if (!(mPlaysNameList.get(childPosition).getPlayAuthors()==null)){
		holder.txt2.setText(mPlaysNameList.get(childPosition).getPlayAuthors().toString());
		}else{
			holder.txt2.setText("");
		}
		
        
		return convertView;
	}

	class ViewHolder{
		ImageView img1 ;
		TextView txt1 ;
		TextView txt2 ;
		TextView txt3 ;

	}

	@Override
	public int getChildrenCount(int groupPosition) {
		mPlaysNameList=(ArrayList<PlaysBean>) mPlaysList.get(mKeyList.get(groupPosition));
		Log.v("mPlaysNameList count ::::::::::::::::::::",""+mPlaysNameList.size());
		return mPlaysNameList.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return mKeyList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if(convertView==null)
		{
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.e_alpha_header, null);


		}
		((TextView)convertView.findViewById(R.id.e_alpha_header_text)).setText("");
				//mKeyList.get(groupPosition).toString()+"");
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

}
