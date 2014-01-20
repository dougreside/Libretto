package org.nypl.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public class BaseFragment extends Fragment {
	protected 	FragmentActivity	sActivityInstance	=	null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sActivityInstance = getActivity();
	}
	
}
