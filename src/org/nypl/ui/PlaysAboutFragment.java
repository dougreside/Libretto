package org.nypl.ui;


import org.nypl.R;
import org.nypl.adapter.ViewPagerAdapter;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;

public class PlaysAboutFragment extends BaseFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		
		if(ViewPagerAdapter.mp!=null)
			ViewPagerAdapter.mp.release();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(ViewPagerAdapter.mp!=null)
		ViewPagerAdapter.mp.release();
	
	}
	@Override
	public void onPause()
	{
		super.onPause();
		if(ViewPagerAdapter.mp!=null)
			ViewPagerAdapter.mp.release();
	}
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.s_plays_about_frag, null);
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
