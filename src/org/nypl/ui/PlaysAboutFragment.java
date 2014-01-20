package org.nypl.ui;


import org.nypl.R;
import org.nypl.adapter.ViewPagerAdapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class PlaysAboutFragment extends BaseFragment {
	



	private ProgressDialog pd;
	private int trans = android.R.color.transparent;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		
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
