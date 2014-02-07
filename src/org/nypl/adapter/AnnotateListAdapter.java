package org.nypl.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.nypl.R;
import org.nypl.dataholder.PlayNoteBean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AnnotateListAdapter extends BaseAdapter{

	private Context mContext;
	private ArrayList<PlayNoteBean> mAnnotateList;
	private int mListCoverWidth;
	private int mListCoverHeight;
	private InputStream bitmap =null;
	ImageView  mCoverImage;
	public AnnotateListAdapter(Context ctx,ArrayList<PlayNoteBean> mPlaysList) {
		// TODO Auto-generated constructor stub
		this.mContext=ctx;
		this.mAnnotateList=mPlaysList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mAnnotateList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		mListCoverWidth  = (int)mContext.getResources().getDimension(R.dimen.list_cover_image_width);
		mListCoverHeight = (int)mContext.getResources().getDimension(R.dimen.list_cover_image_height);
		//if (convertView == null) 
			convertView = LayoutInflater.from(mContext).inflate(R.layout.e_annotate_list_layout, null);
			final PlayNoteBean annotatelist = mAnnotateList.get(position);
		
			mCoverImage =(ImageView) convertView.findViewById(R.id.e_list_layout_play_image);
			TextView  mPlayName=(TextView) convertView.findViewById(R.id.e_list_layout_play_name);
			TextView  mAutherName=(TextView) convertView.findViewById(R.id.e_list_layout_author_name);
			TextView  mVersionName=(TextView) convertView.findViewById(R.id.e_list_layout_version_name);
			TextView  mAnnoList=(TextView) convertView.findViewById(R.id.annotate_text);
			
			try {
				bitmap=mContext.getResources().getAssets().open(annotatelist.getNotePlayImage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			Bitmap bit=BitmapFactory.decodeStream(bitmap);
			Bitmap scaled = Bitmap.createScaledBitmap(bit, mListCoverWidth, mListCoverHeight, true);
			mCoverImage.setImageBitmap(scaled);
			//setCoverImage(bitmap,mCoverImage);
			
			mPlayName.setText(annotatelist.getNotePlayName());
			mAutherName.setText(annotatelist.getAuthor());
			mVersionName.setVisibility(View.VISIBLE);
			mVersionName.setText(annotatelist.getVersionName());
			mAnnoList.setText(annotatelist.getNotes().toString().replace("\n", " "));
			
			return convertView;
		
		
	}
	
	
	

	public void setCoverImage(final InputStream bitmap,final ImageView mCoverImage){
		new Thread(){
			public void run() {
		ImageView im= new ImageView(mContext);
		im=mCoverImage;
		Bitmap bit=BitmapFactory.decodeStream(bitmap);
		Bitmap scaled = Bitmap.createScaledBitmap(bit, mListCoverWidth, mListCoverHeight, true);
		im.setImageBitmap(scaled);

			};
		}.start();
	}

}
