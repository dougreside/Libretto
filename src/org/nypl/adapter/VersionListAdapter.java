package org.nypl.adapter;

import java.util.ArrayList;

import org.nypl.R;
import org.nypl.dataholder.VersionBean;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class VersionListAdapter  extends BaseAdapter{

	private ArrayList<VersionBean> versionDetailList;

	public VersionListAdapter(ArrayList<VersionBean> versionDetailList) {
		super();
		this.versionDetailList = versionDetailList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return versionDetailList.size();
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
				VersionBean version =versionDetailList.get(position);
				((TextView)convertView.findViewById(R.id.version_name)).setText(version.getVersionName());
				return convertView;
	}

}
