package org.nypl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import org.nypl.adapter.PlaysListAdapter;
import org.nypl.adapter.SearchPlayAdapter;
import org.nypl.adapter.ViewPagerAdapter;
import org.nypl.database.CsvToSqliteImport;
import org.nypl.database.PlayDAO;
import org.nypl.dataholder.PlaysBean;
import org.nypl.parsing.AudioFileParserSAX;
import org.nypl.parsing.VersionParser;
import org.nypl.utils.CustomTypefaceSpan;
import org.nypl.utils.ZipExtracter;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class PlaysListActivity extends FragmentActivity   {
	private ExpandableListView mPlayList;
	private HashMap<String, Object>  mPlaysList;
	private ArrayList<String> mkeyList;
	private ArrayList<PlaysBean> mPlaysNameList;
	private ListView mSearchPlayList;
	private EditText mSearchText;
	private ArrayList<PlaysBean> mSearchplaysList;
	//private String mSearchWord = null;
	private TextView mNoText;
	private ProgressDialog pd;
	public static String CONTENT_LOCATION;
	private File FilePath = Environment.getExternalStorageDirectory();
	private Context ctx;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CONTENT_LOCATION= "Android/data/"+this.getPackageName()+File.separator+"contents";
		setContentView(R.layout.plays_activity);
		BitmapFactory.Options options = new BitmapFactory.Options();
		Typeface icomoon = Typeface.createFromAsset(getAssets(),
		        "fonts/icomoon.ttf");
		Typeface lato = Typeface.createFromAsset(getAssets(),
		        "fonts/Lato-Reg.ttf");
		TextView titleBar = (TextView) findViewById(R.id.s_main_title);
		String titleLogo ="&#xe012;";
		String titleText = " Libretto";
		//titleBar.setText(titleText);
		Spannable s = new SpannableString(Html.fromHtml(titleLogo+titleText));
		System.out.println("LOGO LENGTH "+titleLogo.length());
		s.setSpan (new CustomTypefaceSpan("", icomoon ), 0, 1,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan (new CustomTypefaceSpan("", lato ), 2, titleText.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	
		titleBar.setText(s);
		
		
		//.setTypeface(icomoon);
	//	title.setTypeface(lato);
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		//((ImageView)findViewById(R.id.s_background_img)).setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.img_background, options)));

		///findViewById(R.id.progress).setVisibility(View.VISIBLE);
		
		pd = ProgressDialog.show(this, "Loading", "please wait...");
		if(ViewPagerAdapter.mp!=null)
			//ViewPagerAdapter.mp.reset();
		ViewPagerAdapter.mp.release();
	
	}





	@Override
	protected void onResume()
	{
		super.onResume();
		mPlayList = (ExpandableListView) findViewById(R.id.s_play_expendable_list);
		//mSearchPlayList=(ListView)findViewById(R.id.s_play_search_list);
		//mSearchText = (EditText) findViewById(R.id.s_play_search_text);
		mNoText=(TextView) findViewById(R.id.s_play_no_text);
		//mSearchText.setOnEditorActionListener(this);
		//Bundle extras = getIntent().getExtras();
		/*mSearchWord = extras.getString("searchtext");
		if(mSearchWord!=null){
			mSearchText.setText(mSearchWord);
			mSearchText.setSelection(mSearchWord.length());
			switchMode(true);
		}else{
			switchMode(false);
		}*/
		//switchMode(false);
		
		mPlayList.setVisibility(View.VISIBLE);

		getPlays();
		new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(ViewPagerAdapter.mp!=null){
				ViewPagerAdapter.mp.release();
			ViewPagerAdapter.mHandler.removeCallbacks(ViewPagerAdapter.mUpdateTimeTask);
			}
			};
		}.sendEmptyMessageDelayed(1, 1000);
		//mSearchText.addTextChangedListener(this);



	}


	/**
	 * It changes the mode of the activity whether it is in search mode or not.
	 * It make the appropriate ListView to be visible and set the data of that.
	 * @param isSearchMode 'true' to switch to search mode 'false' to no search mode
	 */
	/*private void switchMode(boolean isSearchMode){

		if(isSearchMode)
		{
			mSearchPlayList.setVisibility(View.VISIBLE);
			mPlayList.setVisibility(View.GONE);

			getSearchList();

		}
		else
		{
			mSearchPlayList.setVisibility(View.GONE);
			mPlayList.setVisibility(View.VISIBLE);

			getPlays();

		}

	}

*/
	@Override
	protected void onPause()
	{

		super.onPause();
		if(ViewPagerAdapter.mp!=null)
			ViewPagerAdapter.mp.release();
		//Save state here
	}


	public void deletePlayClicked(View v){
		System.out.println("Delete clicked ");
		
		System.out.println(v.getTag());
		deletePlay((Integer)v.getTag(),v);
		
		
	}

	public void getPlays(){

		new Thread(){

			public void run() {
				mPlaysList = PlayDAO.getAllPlays(PlaysListActivity.this);
				mkeyList=PlayDAO.getKeyList() ;
				runOnUiThread(new Runnable(){
					@Override
					public void run() {  
						mPlayList.setGroupIndicator(null);
						mPlayList.setDrawingCacheBackgroundColor(17170445);

						mPlayList.setAdapter(new PlaysListAdapter(mPlaysList,PlaysListActivity.this,mkeyList,0));
						mPlayList.setChildDivider(getResources().getDrawable(R.drawable.row_divider)); 
						///findViewById(R.id.progress).setVisibility(View.GONE);
						pd.dismiss();
						for(int j=0;j<mkeyList.size();j++)
						{

							mPlayList.expandGroup(j);
						}
						mNoText.setVisibility(View.GONE);


					}

				});



			};

		}.start();	

		 mPlayList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){ 
             @Override 
             public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) 
            { 
                 Toast.makeText(PlaysListActivity.this, "LongClick", Toast.LENGTH_LONG).show();
                 return false;
            } 
       }); 
		mPlayList.setOnChildClickListener(new OnChildClickListener() {


			private String mVersion = null;
			private String mNote = null;

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				mPlaysNameList=	(ArrayList<PlaysBean>)mPlaysList.get(mkeyList.get(groupPosition));
			      //--- Getting a new play
				 ctx = findViewById(R.id.s_main_title).getContext();
		 			
				System.out.println("The one clicked is "+childPosition);
				String uuid = mPlaysNameList.get(childPosition).getPlayID().toString();
				if (uuid.equals("About")){
					Intent i = new Intent(PlaysListActivity.this,AboutActivity.class);
					startActivity(i);
					return false;
				}
				else{
				String epuburl = mPlaysNameList.get(childPosition).getPlayUrl().toString();
				File epubDir = new File(FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+uuid);
				File epubFile = new File(FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+uuid+".epub");
				
			   	if ((haveNetworkConnection())&&(!epubDir.exists()))	{
			   		if (!epubFile.exists()){
			   			pd = ProgressDialog.show(ctx, "Downloading libretto", "please wait...");
						
			   			final DownloadFile df = new DownloadFile();
			   			df.execute(epuburl,FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+uuid+".epub",uuid,groupPosition+"",childPosition+"",mPlaysNameList.get(childPosition).getPlayName().toString(),mVersion);
			   		}
			   		else{
			   			pd = ProgressDialog.show(ctx, "Installing libretto", "please wait...");		
			   			System.out.println("To the unzipper "+CONTENT_LOCATION);
			   			final UnzipEPUB unzipper = new UnzipEPUB(); 
			   		    unzipper.execute(childPosition+"",uuid,mPlaysNameList.get(childPosition).getPlayName().toString(),mVersion);
			   		}
			        return false;
			   	}
			   	else {
			   		
				//---------
		   	   	
				Intent i = new Intent(PlaysListActivity.this,PlaysDetailActivity.class);


				Log.v("PlayListActivity","CHILD POSITION: "+childPosition);
				i.putExtra("playsId",mPlaysNameList.get(childPosition).getPlayID().toString());
				i.putExtra("playsName",mPlaysNameList.get(childPosition).getPlayName().toString());
				i.putExtra("mNote","Home" );
				i.putExtra("position",childPosition);
				i.putExtra("mVersion",mVersion );
				System.out.println(i.getExtras().toString());
				startActivity(i);
				
				return false;
				}
			   	
			}}
		});

	}

	
	
	private class UnzipEPUB extends AsyncTask<String,Integer,String> {
		String playId;
		String childPos;
		String playName;
		String mVersion;
		@Override
		protected void onPostExecute(String intentVals) {

			pd.dismiss();	
			Intent i = new Intent(PlaysListActivity.this,PlaysDetailActivity.class);
		
	
			
			
			i.putExtra("position",childPos);
			i.putExtra("playsName",playName);
			i.putExtra("playsId",playId);
			i.putExtra("mNote","Home" );
			i.putExtra("mVersion",mVersion);
			
			startActivity(i);
			System.out.println("Download Complete");
			System.out.println(i.getExtras().toString());
			super.onPostExecute(intentVals);
	    	
			
		}
	    @Override
	    protected String doInBackground(String... params) {
	    	childPos=params[0];
	    	playId=params[1];
			playName=params[2];
			mVersion = params[3];

	  
	
			  
			  
	    	File mFirstVersionFile = new File(FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+playId+".epub");
	    	
	    	String[] i={"","","","",""};
	  
		
		       String mFilename=playId+".epub";
		       String mFilepath=FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+mFilename;
		      
		       System.out.println("UNZIPPING "+mFilepath);
		       ctx = findViewById(R.id.s_main_title).getContext();
		 		
		      // pd = ProgressDialog.show(ctx, "Installing libretto", "please wait...");
			
		        try {
		        
		            InputStream inputStream = null;
					try {

					   	 mFirstVersionFile.mkdirs();
					   	 inputStream = new FileInputStream(mFilepath);   
					   	 String output = FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+playId+File.separator;
						ZipExtracter.extract(inputStream, output);
						
						inputStream.close();
						
						
						 File mVersionFile = new File(output+File.separator+"toc.ncx");

					     	
					 		try {
					 			Log.v("CSVImport","GOTCHA");
					 			InputStream is = new FileInputStream(mVersionFile);
					 			String playid = VersionParser.parsePlayVersion(is,null,ctx,CONTENT_LOCATION);
							
					 			
					 			CsvToSqliteImport.readFromCsvForAudioTable(playid,null,ctx);
					 			
					 			//	int rowUpdated=getContentResolver().insert(Uri.parse(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.PLAY_PATH), cv, AudioDAO.COLUMN_NAME_AUDIO_VERSION_ID + "=\"" + Version +"\" and "+ AudioDAO.COLUMN_NAME_AUDIO_PLAY_ID + "=\"" + mPlaysId+"\"" , null);
								
					 			//VersionBean playVersionData =VersionParser.parsePlayVersion(is,thisContext.,thisContext,CONTENT_LOCATION);
					 			Log.v("CSVImport","GOTTEN");
					 			
					 			
					 			
					 		  
						
								
								System.out.print("Happy");
					 			
					 			
					 			
					 			//HomeActivity.refreshCarousel();
					 		
					 			
					 		} catch (FileNotFoundException e) {
					 			// TODO Auto-generated catch block

					 			e.printStackTrace();
					 		}
							}
							catch (Exception e){
								e.printStackTrace();
							}
						
						
						//publishProgress(-2);
					} 
					catch (Exception e) 
					{
						//publishProgress(-4);
					}
		        
		        return "";
	    }
	  
	}
   
	
	
	
	
	
	
	
	
	
	
	

	/*public void unzipEPUB(String epubUrl, String playID,String groupPos,String childPos){
		File mFirstVersionFile = new File(FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+playID+".epub");
	
		   int childPosition = Integer.parseInt(childPos);
		   int groupPosition = Integer.parseInt(groupPos);
		   
   	  
  
	
	       String mFilename=playID+".epub";
	       String mFilepath=FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+mFilename;
	      
	       System.out.println("UNZIPPING "+mFilepath);
	       ctx = findViewById(R.id.s_main_title).getContext();
	 		
	       pd = ProgressDialog.show(ctx, "Installing libretto", "please wait...");
		
	        try {
	        
	            InputStream inputStream = null;
				try {

				   	 mFirstVersionFile.mkdirs();
				   	 inputStream = new FileInputStream(mFilepath);   
				   	 String output = FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+playID+File.separator;
					ZipExtracter.extract(inputStream, output);
					
					inputStream.close();
					
					
					 File mVersionFile = new File(output+File.separator+"toc.ncx");

				     	
				 		try {
				 			Log.v("CSVImport","GOTCHA");
				 			InputStream is = new FileInputStream(mVersionFile);
				 			String playid = VersionParser.parsePlayVersion(is,null,ctx,CONTENT_LOCATION);
							CsvToSqliteImport.readFromCsvForAudioTable(playid,null,ctx);
				 		//	int rowUpdated=getContentResolver().insert(Uri.parse(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.PLAY_PATH), cv, AudioDAO.COLUMN_NAME_AUDIO_VERSION_ID + "=\"" + Version +"\" and "+ AudioDAO.COLUMN_NAME_AUDIO_PLAY_ID + "=\"" + mPlaysId+"\"" , null);
							
				 			//VersionBean playVersionData =VersionParser.parsePlayVersion(is,thisContext.,thisContext,CONTENT_LOCATION);
				 			Log.v("CSVImport","GOTTEN");
				 			
				 			
				 			
				 		  	pd.dismiss();
							Intent i = new Intent(PlaysListActivity.this,PlaysDetailActivity.class);
							
							mPlaysNameList=	(ArrayList<PlaysBean>)mPlaysList.get(mkeyList.get(groupPosition));
							Log.v("PlayListActivity","CHILD POSITION: "+childPosition);
							i.putExtra("playsId",mPlaysNameList.get(childPosition).getPlayID().toString());
							i.putExtra("playsName",mPlaysNameList.get(childPosition).getPlayName().toString());
							i.putExtra("mNote","Home" );
							i.putExtra("position",childPosition);
							//i.putExtra("mVersion",mVersion );
							startActivity(i);
						
				 			
				 			
				 			
				 			//HomeActivity.refreshCarousel();
				 		
				 			
				 		} catch (FileNotFoundException e) {
				 			// TODO Auto-generated catch block

				 			e.printStackTrace();
				 		}
						}
						catch (Exception e){
							System.out.println(e.getStackTrace());
						}
					
					
					//publishProgress(-2);
				} 
				catch (Exception e) 
				{
					//publishProgress(-4);
				}
	         		    	   	
	      
	       
	       
   	   
   	   	
		
		

	}*/
	public void getSearchList(){

		final String currentString = mSearchText.getText().toString().trim();
		 if(!isWhitespace(currentString)){
		mSearchPlayList.setVisibility(View.VISIBLE);
		mPlayList.setVisibility(View.GONE);

		new Thread(){



			public void run() {

				mSearchplaysList = PlayDAO.getAllSearchPlays(PlaysListActivity.this,currentString);
				runOnUiThread(new Runnable(){
					@Override
					public void run() {  
						System.out.println("mSearchplaysList:::::::::::::::::::::::::::::"+mSearchplaysList.size());
						if(mSearchplaysList.size()>0){
							mSearchPlayList.setDrawingCacheBackgroundColor(17170445);
							mSearchPlayList.setAdapter(new SearchPlayAdapter(PlaysListActivity.this,mSearchplaysList,0));
							mSearchPlayList.setDivider(getResources().getDrawable(R.drawable.row_divider)); 
						//	PlaysListActivity.this.findViewById(R.id.progress).setVisibility(View.GONE);
							pd.dismiss();
							mNoText.setVisibility(View.GONE);
						}else{
							mNoText.setVisibility(View.VISIBLE);
							mSearchPlayList.setVisibility(View.GONE);
							pd.dismiss();
							mNoText.setText("No Play found.");

						}
					}
				});

			};
		}.start();
		mSearchPlayList.setOnItemClickListener(new OnItemClickListener() {

			private String mVersion = null;
			private String mNote = null;

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i = new Intent(PlaysListActivity.this,PlaysDetailActivity.class);

				PlaysBean mPlaysNameList=	mSearchplaysList.get(arg2);

				i.putExtra("playsId",mPlaysNameList.getPlayID().toString());
				i.putExtra("playsName",mPlaysNameList.getPlayName().toString());
				i.putExtra("mNote","Home" );
				i.putExtra("position",0);
				i.putExtra("mVersion",mVersion );
				startActivity(i);

			}
		});

		PlaysListActivity.this.findViewById(R.id.progress).setVisibility(View.GONE);
		 }else{
			 Toast.makeText(this, "Please enter text.", Toast.LENGTH_LONG).show();
		 }
	}


	private boolean deletePlay(int index,View v){
		mPlaysNameList=	(ArrayList<PlaysBean>)mPlaysList.get(mkeyList.get(index));
		String delID = mPlaysNameList.get(index).getPlayID().toString();
		String contentFolder = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"Android/data/"+v.getContext().getPackageName()+File.separator+"contents"+File.separator+delID;
		System.out.println(contentFolder);
		ContentResolver cr = v.getContext().getContentResolver();
		Log.v("PlayListActivity","Delete CHILD POSITION: "+index);
		cr.delete(Uri.parse(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.PLAY_PATH), "PLAY_LONG_ID=\""+delID+"\"", null);
		cr.delete(Uri.parse(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.VERSION_PATH),"PLAY_ID=\""+delID+"\"",null);
		cr.delete(Uri.parse(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.AUDIO_PATH),"PLAY_ID=\""+delID+"\"",null);
		recursiveDelete(new File(contentFolder));
		getPlays();
	
		return true;
	}
	private void recursiveDelete(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                recursiveDelete(child);

        fileOrDirectory.delete();
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

	private class DownloadFile extends AsyncTask<String,Integer,String> {
		String playId;
		String childPos;
		String groupPos;
		String playName;
		String playVersion;
		@Override
		protected void onPostExecute(String outstring) {
			pd.dismiss();	
			System.out.println("Download Complete");
			pd = ProgressDialog.show(ctx, "Installing libretto", "please wait...");
			
			final UnzipEPUB unzipper = new UnzipEPUB(); 
   	
   		    unzipper.execute(childPos,playId,playName,playVersion);
   		    super.onPostExecute(outstring);
	    	
			
		}
	    @Override
	    protected String doInBackground(String... params) {
	    	
	    	//CONTENT_LOCATION = "Android/data/"+this.getPackageName()+File.separator+"contents";
		     	String urlString = params[0];
	    	     String outstring =   params[1];
	    	     playId = params[2];
	    	      groupPos = params[3];
	    	      childPos = params[4];
	    	    
	    	      playName = params[5];
	    	      playVersion = params[6];
	        try {
	        	downloadContent(urlString,outstring,groupPos,childPos);
	            
	            return outstring;
			     
			 
			
	            
	         	    	   	
	        } catch (Exception e) {
	        e.printStackTrace();
	        }
	        return outstring;
	    }
	    public void downloadContent(String urlString,String outstring,String groupPosition, String childPosition){
	    	try{
	    	URL url = new URL(urlString);
	    	 URLConnection connection = url.openConnection();
	            connection.connect();
	            // this will be useful so that you can show a typical 0-100% progress bar
	            int fileLength = connection.getContentLength();
	            System.out.println("Downloading to: "+outstring);
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
	    	
	    }
	    	catch (Exception e){
	    		System.out.println(e);
	    	}
	}
   
	String readFile(String fileName) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    }
	    	finally {
	    
	        br.close();
	    }
	}


}
	public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }	
	
	
}


/*

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

@Override
public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	if (actionId == EditorInfo.IME_ACTION_SEARCH ||
			actionId == EditorInfo.IME_ACTION_DONE ||
			event.getAction() == KeyEvent.ACTION_DOWN &&
			event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
		InputMethodManager inputMgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMgr.toggleSoftInput(0, 0);
		///	isAuntheticate();
		switchMode(true);
		return true;
	}
	return false;

}*/