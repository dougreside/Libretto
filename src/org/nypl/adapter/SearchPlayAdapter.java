package org.nypl.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.nypl.R;
import org.nypl.dataholder.PlaysBean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchPlayAdapter extends BaseAdapter {

	private ArrayList<PlaysBean> mSearchplaysList;
	private Context mContext;
	private int mListCoverWidth;
	private int mListCoverHeight;
	private int mFlag = 0;

	public SearchPlayAdapter(Context ctx,ArrayList<PlaysBean> mSearchplaysList,int flag) {
		this.mSearchplaysList=mSearchplaysList;
		this.mContext=ctx;
		this.mFlag = flag;
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mSearchplaysList.size();
	}

	@Override
	public Object getItem(int position) {
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
			holder.txt3 =(TextView) convertView.findViewById(R.id.e_list_layout_version_name);
			convertView.setTag(holder);     

		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		PlaysBean playlist=	 mSearchplaysList.get(position);

		try {

			bitmap=mContext.getResources().getAssets().open(playlist.getPlayImage());
			Bitmap bit=BitmapFactory.decodeStream(bitmap);
			Bitmap scaled = Bitmap.createScaledBitmap(bit, mListCoverWidth, mListCoverHeight, true);

			holder.img1.setImageBitmap(scaled);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		holder.txt1.setText(playlist.getPlayName().toString());
		holder.txt2.setText(playlist.getPlayAuthors().toString());
		 if(mFlag==1){
			    holder.txt3.setVisibility(convertView.VISIBLE);
	        	holder.txt3.setText(playlist.getPlayVersion().toString());
	        }
		return convertView;
	}
	class ViewHolder{
		ImageView img1 ;
		TextView txt1 ;
		TextView txt2 ;
		TextView txt3 ;
	}
}
