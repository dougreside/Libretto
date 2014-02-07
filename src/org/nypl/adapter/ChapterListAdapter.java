package org.nypl.adapter;

import java.util.ArrayList;

import org.nypl.R;
import org.nypl.dataholder.ChaptersBean;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChapterListAdapter  extends BaseAdapter{

	private ArrayList<ChaptersBean> ChapterList;

	public ChapterListAdapter(ArrayList<ChaptersBean> ChapterList) {
		super();
		this.ChapterList = ChapterList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return ChapterList.size();
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
		 if(convertView==null)
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.e_popup_list, null);
				ChaptersBean chapter = ChapterList.get(position);
				((TextView)convertView.findViewById(R.id.version_name)).setText(chapter.getChapterName());
				return convertView;
	}

}
