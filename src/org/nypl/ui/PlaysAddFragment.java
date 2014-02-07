package org.nypl.ui;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.nypl.PlaysAddActivity;
import org.nypl.R;
import org.nypl.adapter.ViewPagerAdapter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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

public class PlaysAddFragment extends BaseFragment implements TextWatcher,OnEditorActionListener{
	
    private String CONTENT_LOCATION;
	private EditText mSearchField;
	private static File FilePath = Environment.getExternalStorageDirectory();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		mSearchField = (EditText) view.findViewById(R.id.s_plays_add_url);		
		mSearchField.addTextChangedListener(this);
		mSearchField.setOnEditorActionListener(this);

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
	
		}else
		{
			
		}




	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		String uri;
		if (actionId == EditorInfo.IME_ACTION_GO ||
				actionId == EditorInfo.IME_ACTION_DONE ||
				event.getAction() == KeyEvent.ACTION_DOWN &&
				event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			InputMethodManager inputMgr = (InputMethodManager)sActivityInstance.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMgr.toggleSoftInput(0, 0);
			///	isAuntheticate();
			 uri = mSearchField.getText().toString();
			
				String protocol = uri.substring(0,uri.indexOf("/"));
	       		 if ((protocol.length()>1)&&(!(protocol.startsWith("http")))){
	       		 uri = "http://"+uri;
	       		 }
				 DownloadFile downloadFile = new DownloadFile();
			       //downloadFile.execute("https://s3.amazonaws.com/lpa-musical.nypl.org/dh2013/HTMLContent.zip");
			       downloadFile.execute(uri);
		   	 
			return true;
		}
		return false;

	}
	private class DownloadFile extends AsyncTask<String,Integer,String> {
		@Override
		protected void onPostExecute(String result) {
			//new ExtractingTask("HTMLObject.zip", FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+"/").execute();
			 //CONTENT_LOCATION = "Android/data/"+getView().getContext().getPackageName()+File.separator+"contents";
		    
			
			 PlaysAddActivity.processZipFile(result,getView().getContext());

			
     
			super.onPostExecute(result);
	    	
			
		}
	    @Override
	    protected String doInBackground(String... params) {
	    	CONTENT_LOCATION = "Android/data/"+getView().getContext().getPackageName()+File.separator+"contents";
		     
	    	String outstring = FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+params[0].substring(params[0].lastIndexOf("/")+1);
	           
	        try {
	        	URL url = new URL(params[0]);
	            
		           
	            URLConnection connection = url.openConnection();
	            connection.connect();
	            // this will be useful so that you can show a typical 0-100% progress bar
	            int fileLength = connection.getContentLength();

	            // download the file
	            InputStream input = new BufferedInputStream(url.openStream());
	            OutputStream output = new FileOutputStream(outstring);

	            byte data[] = new byte[1024];
	            long total = 0;
	            int count;
	            while ((count = input.read(data)) != -1) {
	                total += count;
	                //publishing the progress....
	                publishProgress((int) (total * 100 / fileLength));
	                output.write(data, 0, count);
	            }

	            output.flush();
	            output.close();
	            input.close();
	         	    	   	
	        } catch (Exception e) {
	        e.printStackTrace();
	        }
	        return outstring;
	    }

	}
	/*
	private void DownloadFile(String uri) {
	          
	            Context thisContext = getView().getContext();
	            CONTENT_LOCATION = "Android/data/"+thisContext.getPackageName()+File.separator+"contents";
		     	
	           
	       	 try {
	       		 String protocol = uri.substring(0,uri.indexOf("/"));
	       		 if ((protocol.length()>1)&&(!(protocol.startsWith("http")))){
	       		 uri = "http://"+uri;
	       		 }
	 	        
	            URL url = new URL(uri);
	            URLConnection connection = url.openConnection();
	            connection.connect();
	            // this will be useful so that you can show a typical 0-100% progress bar
	            int fileLength = connection.getContentLength();

	            // download the file
	            InputStream input = new BufferedInputStream(url.openStream());
	            OutputStream output = new FileOutputStream(FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+uri.substring(uri.lastIndexOf("/")+1));


	            output.flush();
	            output.close();
	            input.close();
	            PlaysAddActivity.processZipFile(FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+uri.substring(uri.lastIndexOf("/")+1));
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }}
	        */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.s_plays_add_frag, null);
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
