package org.nypl.ui;

import org.nypl.R;
import org.nypl.adapter.ViewPagerAdapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlaysFragment extends BaseFragment{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		 
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.s_plays_frag, null);
	}
	@Override
	public void onResume() {
		super.onResume();
		if(ViewPagerAdapter.mp!=null)
			ViewPagerAdapter.mp.release();
	
	}
}
