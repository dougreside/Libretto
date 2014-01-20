package org.nypl.ui;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.nypl.MoverContentProvider;
import org.nypl.PlaysDetailActivity;
import org.nypl.R;
import org.nypl.adapter.AnnotateListAdapter;
import org.nypl.adapter.ViewPagerAdapter;
import org.nypl.database.DatabaseTable;
import org.nypl.database.PlayNoteDAO;
import org.nypl.database.VersionDAO;
import org.nypl.dataholder.PlayNoteBean;
import org.nypl.dataholder.VersionBean;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class PlaysAnnotateFragment extends ActionModeBaseFragment implements TextWatcher,OnEditorActionListener, OnItemLongClickListener{
	
	private ListView mPlayList;
	private ArrayList<PlayNoteBean> mPlaysAnnotateList;
	private EditText mSearchField;
	private ListView mSearchPlayList;
	private TextView mNoText;
	private AnnotateListAdapter mPlayListAdapter;
	private boolean mIsActionModeActive;
	private ProgressDialog pd;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		mPlayList = (ListView) view.findViewById(R.id.s_play_annotate_listview);
		mSearchPlayList = (ListView) view.findViewById(R.id.s_play_annotate_search_listview);
		mSearchField = (EditText) view.findViewById(R.id.s_plays_annotation_search);		
		mNoText=(TextView) view.findViewById(R.id.s_play_no_text);
		mSearchField.addTextChangedListener(this);
		mSearchField.setOnEditorActionListener(this);
	/////	mPlayList.setOnItemLongClickListener(this);
		//sActivityInstance.findViewById(R.id.progress1).setVisibility(View.VISIBLE);
		mPlayList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mPlayList.setItemsCanFocus(false);
		pd = ProgressDialog.show(sActivityInstance, "Loading", "please wait...");
		if(ViewPagerAdapter.mp!=null)
			ViewPagerAdapter.mp.release();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(ViewPagerAdapter.mp!=null)
		ViewPagerAdapter.mp.release();
		switchMode(false);
	}
	@Override
	public void onPause()
	{
		super.onPause();
		if(ViewPagerAdapter.mp!=null)
			ViewPagerAdapter.mp.release();
	}
	/**
	 * It changes the mode of the activity whether it is in search mode or not.
	 * It make the appropriate ListView to be visible and set the data of that.
	 * @param isSearchMode 'true' to switch to search mode 'false' to no search mode
	 */
	private void switchMode(boolean isSearchMode){
		if(isSearchMode)
		{
			mSearchPlayList.setVisibility(View.VISIBLE);
			mPlayList.setVisibility(View.GONE);
			mNoText.setVisibility(View.GONE);
			getSearchList();

		}
		else
		{
			mSearchPlayList.setVisibility(View.GONE);
			mPlayList.setVisibility(View.VISIBLE);
			mNoText.setVisibility(View.GONE);
			getPlaysAnnotate();
		}

	}


	public void getPlaysAnnotate() {
		new Thread(){

			public void run() {

				///mPlaysAnnotateList = VersionDAO.getPlayVersionHavingNotes(sActivityInstance,null,null);
				mPlaysAnnotateList = PlayNoteDAO.getAllNotes(sActivityInstance,null,null,null);
				sActivityInstance.runOnUiThread(new Runnable(){
					

					@Override
					public void run() { 
						if(mPlaysAnnotateList.size()>0){
						mPlayList.setDrawingCacheBackgroundColor(android.R.color.transparent);
						mPlayListAdapter = new AnnotateListAdapter(sActivityInstance,mPlaysAnnotateList);
						mPlayList.setAdapter(mPlayListAdapter);
						mPlayList.setDivider(getResources().getDrawable(R.drawable.row_divider)); 
						//sActivityInstance.findViewById(R.id.progress1).setVisibility(View.GONE);
						pd.dismiss();
						}else{
							mNoText.setVisibility(View.VISIBLE);
							mPlayList.setVisibility(View.GONE);
						//	sActivityInstance.findViewById(R.id.progress1).setVisibility(View.GONE);
							pd.dismiss();
							mNoText.setText("Notes not available.");
							
						}

					}
				});

			};
		}.start();
		mPlayList.setOnItemClickListener(new OnItemClickListener() {

			private String mVersion = null;
			

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (mIsActionModeActive) {
					final SparseBooleanArray checkedItems = mPlayList.getCheckedItemPositions();
					final int totalItem = checkedItems.size();
					int checkedItemsCount	=	0;
					for (int i = 0; i < totalItem; i++) {
						// This tells us the item position we are looking at
						// --
						final boolean isChecked = checkedItems.valueAt(i);
						if (isChecked) {
							checkedItemsCount++;
						}
					}
					setActionModeText(checkedItemsCount);
					return;
				}else{
					Intent i = new Intent(sActivityInstance,PlaysDetailActivity.class);
					i.putExtra("playsId",mPlaysAnnotateList.get(arg2).getPlayID().toString());
					i.putExtra("playsName",mPlaysAnnotateList.get(arg2).getNotePlayName().toString());
					i.putExtra("mNote",mPlaysAnnotateList.get(arg2).getNotes().toString());
					i.putExtra("mNoteID",mPlaysAnnotateList.get(arg2).getNoteID().toString());
					i.putExtra("position",arg2);
					i.putExtra("mVersion",mPlaysAnnotateList.get(arg2).getVersionID().toString());
					startActivity(i);

				}
				
			}
		});
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

		if(s.length()>0)
		{
			switchMode(true);
		}else
		{
			switchMode(false);
		}




	}


	public void getSearchList(){

		final String currentString = mSearchField.getText().toString().trim();
		 if(!isWhitespace(currentString)){
		mSearchPlayList.setVisibility(View.VISIBLE);
		mPlayList.setVisibility(View.GONE);

		new Thread(){

			public void run() {

				mPlaysAnnotateList = PlayNoteDAO.getAllNotes(sActivityInstance,currentString,null,null);
				sActivityInstance.runOnUiThread(new Runnable(){
					@Override
					public void run() {  
						if(mPlaysAnnotateList.size()>0){
						mSearchPlayList.setDrawingCacheBackgroundColor(android.R.color.transparent);
						mSearchPlayList.setAdapter(new AnnotateListAdapter(sActivityInstance,mPlaysAnnotateList));
						mSearchPlayList.setDivider(getResources().getDrawable(R.drawable.row_divider)); 
					//	sActivityInstance.findViewById(R.id.progress1).setVisibility(View.GONE);
						pd.dismiss();
					}else{
						mNoText.setVisibility(View.VISIBLE);
						mSearchPlayList.setVisibility(View.GONE);
						//sActivityInstance.findViewById(R.id.progress1).setVisibility(View.GONE);
						pd.dismiss();
						mNoText.setText("Notes not available.");
						
					}
					}
				});

			};
		}.start();
		mSearchPlayList.setOnItemClickListener(new OnItemClickListener() {

			private String mVersion = null;

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i = new Intent(sActivityInstance,PlaysDetailActivity.class);
				i.putExtra("playsId",mPlaysAnnotateList.get(arg2).getPlayID().toString());
				i.putExtra("playsName",mPlaysAnnotateList.get(arg2).getNotePlayName().toString());
				i.putExtra("mNote",mPlaysAnnotateList.get(arg2).getNotes().toString());
				i.putExtra("mNoteID",mPlaysAnnotateList.get(arg2).getNoteID().toString());
				i.putExtra("mSearchNote",currentString);
				i.putExtra("position",arg2);
				i.putExtra("mVersion",mPlaysAnnotateList.get(arg2).getVersionID().toString());
				startActivity(i);
			}
		});
		 }else{
			 Toast.makeText(sActivityInstance, "Please enter text.", Toast.LENGTH_LONG).show();
		 }

	}
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_SEARCH ||
				actionId == EditorInfo.IME_ACTION_DONE ||
				event.getAction() == KeyEvent.ACTION_DOWN &&
				event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			InputMethodManager inputMgr = (InputMethodManager)sActivityInstance.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMgr.toggleSoftInput(0, 0);
			///	isAuntheticate();
			switchMode(true);
			return true;
		}
		return false;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.s_plays_annotate_frag, null);
	}

	@Override
	protected void deleteItems() {
		boolean isCheckedItemFound	=	false;
		final SparseBooleanArray checkedItems = mPlayList.getCheckedItemPositions();
		int checkedItemsCount = checkedItems.size();
		Set<Integer> deletePos	=	new TreeSet<Integer>(comparator);
		for (int i = 0; i < checkedItemsCount; i++) {
			// This tells us the item position we are looking at
			// --
			final int position = checkedItems.keyAt(i);
			// This tells us the item status at the above position
			// --
			final boolean isChecked = checkedItems.valueAt(i);
			if (isChecked && mPlaysAnnotateList != null && mPlaysAnnotateList.size() > 0) {
				isCheckedItemFound	=	true;
				deletePos.add(position);
			}
		}
		
		if (isCheckedItemFound) {
			for (Integer position : deletePos) {
				
				ContentValues cv =new ContentValues();
				cv.put(VersionDAO.COLUMN_NAME_NOTE, "");
				int rowUpdated=sActivityInstance.getContentResolver().update(Uri.parse(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.VERSION_PATH), cv, VersionDAO.COLUMN_NAME_VERSION_ID+ "=" + mPlaysAnnotateList.get(position.intValue()).getVersionID(), null);
			    Log.v("UPDATE::::::::","::"+rowUpdated);
			    Toast.makeText(sActivityInstance, "Successfully deleted.", Toast.LENGTH_LONG).show();
				mPlaysAnnotateList.remove(position.intValue());
			//	isCheckedItemFound	=	false;
				//checkedItemsCount=0;
				setActionModeText(0);
				checkedItems.clear();
				
			}

		}
		if(mPlaysAnnotateList.size()==0){
			mNoText.setVisibility(View.VISIBLE);
			mNoText.setText("Notes not available.");
		}
	}
	// comparator for sorting items So that they can be deleted according  Descending position.
	private Comparator<Integer> comparator = new Comparator<Integer>() {
		
		@Override
		public int compare(Integer lhs, Integer rhs) {
			return rhs.compareTo(lhs);
		}
	};


	@Override
	protected void destroyActionMode() {
		mPlayList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);	
		mIsActionModeActive = false;
		mPlayList.clearChoices();
		mIsActionModeActive = false;
		int count = mPlayListAdapter.getCount();
		for (int i = 0; i < count; i++) {
			mPlayList.setItemChecked(i, false);	
		}
	}

	
	
	@Override
	protected void refreshItemsAfterDeletion() {
		mPlayListAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		
		if(mIsActionModeActive)
			return false;
			
		
		mIsActionModeActive = true;		
		mPlayList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mPlayList.setItemChecked(arg2, true);
		startActionMode();
		setActionModeText(1);
		
		return true;
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
