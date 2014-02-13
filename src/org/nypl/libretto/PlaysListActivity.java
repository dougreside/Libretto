package org.nypl.libretto;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.nypl.libretto.adapter.PlaysListAdapter;
import org.nypl.libretto.adapter.SearchPlayAdapter;
import org.nypl.libretto.adapter.ViewPagerAdapter;
import org.nypl.libretto.database.CsvToSqliteImport;
import org.nypl.libretto.database.PlayDAO;
import org.nypl.libretto.dataholder.PlaysBean;
import org.nypl.libretto.parsing.VersionParser;
import org.nypl.libretto.utils.CustomTypefaceSpan;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
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
		Spannable s = new SpannableString(Html.fromHtml(titleLogo+titleText));
		s.setSpan (new CustomTypefaceSpan("", icomoon ), 0, 1,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan (new CustomTypefaceSpan("", lato ), 2, titleText.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	
		titleBar.setText(s);
		
		

		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		
		
		pd = ProgressDialog.show(this, "Loading", "please wait...");
		if(ViewPagerAdapter.mp!=null)
		ViewPagerAdapter.mp.release();
	
	}





	@Override
	protected void onResume()
	{
		super.onResume();
		mPlayList = (ExpandableListView) findViewById(R.id.s_play_expendable_list);
		mNoText=(TextView) findViewById(R.id.s_play_no_text);
	
		
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
	
	@Override
	protected void onPause()
	{

		super.onPause();
		if(ViewPagerAdapter.mp!=null)
			ViewPagerAdapter.mp.release();
		//Save state here
	}


	public void deletePlayClicked(View v){
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
              //   Toast.makeText(PlaysListActivity.this, "LongClick", Toast.LENGTH_LONG).show();
                 return false;
            } 
       }); 
		mPlayList.setOnChildClickListener(new OnChildClickListener() {


			private String mVersion = null;

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				mPlaysNameList=	(ArrayList<PlaysBean>)mPlaysList.get(mkeyList.get(groupPosition));
			      //--- Getting a new play
				 ctx = findViewById(R.id.s_main_title).getContext();
				String uuid = mPlaysNameList.get(childPosition).getPlayID().toString();
				if (uuid.equals("About")){
					Intent i = new Intent(PlaysListActivity.this,AboutActivity.class);
					startActivity(i);
					return false;
				}
				else{
				String epuburl = mPlaysNameList.get(childPosition).getPlayUrl().toString();
				File epubDir = new File(FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+uuid);
				
			   	if ((haveNetworkConnection())&&(!epubDir.exists()))	{
			   		
			   		pd = new ProgressDialog(ctx);
			   		pd.setMessage("Downloading libretto");
			   		pd.setIndeterminate(true);
			   		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			   		pd.setCancelable(false);
			   			//pd = ProgressDialog.show(ctx, "Downloading libretto", "please wait...");
						
			   			final DownloadFile df = new DownloadFile();
			   			df.execute(epuburl,FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+uuid+".epub",uuid,groupPosition+"",childPosition+"",mPlaysNameList.get(childPosition).getPlayName().toString(),mVersion);
			   		
			   		
			        return false;
			   	}
			   	else {
			   		
				//---------
		   	   	if (epubDir.exists()){
				Intent i = new Intent(PlaysListActivity.this,PlaysDetailActivity.class);
				i.putExtra("playsId",mPlaysNameList.get(childPosition).getPlayID().toString());
				i.putExtra("playsName",mPlaysNameList.get(childPosition).getPlayName().toString());
				i.putExtra("mNote","Home" );
				i.putExtra("position",childPosition);
				i.putExtra("mVersion",mVersion );
				i.putExtra("mChapter","0");
				startActivity(i);
				
				
		   	   	}
		   	   	else{
		   	        Toast.makeText(PlaysListActivity.this, "No Network Available.  Please try again later.", Toast.LENGTH_LONG).show();
	                
		   	   	}
		   	   	
		   	 return false;
				}
			   	
			}}
		});

	}

	public static boolean deleteDirectory(File directory) {
	    if(directory.exists()){
	        File[] files = directory.listFiles();
	        if(null!=files){
	            for(int i=0; i<files.length; i++) {
	                if(files[i].isDirectory()) {
	                    deleteDirectory(files[i]);
	                }
	                else {
	                    files[i].delete();
	                }
	            }
	        }
	    }
	    return(directory.delete());
	}
	
	private class UnzipEPUB extends AsyncTask<String,Integer,String> {
		String playId;
		String childPos;
		String playName;
		String mVersion;
		WakeLock mWakeLock;
		@Override
		 protected void onPreExecute() {
		        super.onPreExecute();
		        // take CPU lock to prevent CPU from going off if the user 
		        // presses the power button during download
		        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
		        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
		             getClass().getName());
		        mWakeLock.acquire();
		        pd.show();
		    }
			 @Override
			    protected void onProgressUpdate(Integer... progress) {
			        super.onProgressUpdate(progress);
			        // if we get here, length is known, now set indeterminate to false
			
			     
			        pd.setProgress(progress[0]);
			    }
			 @Override
				protected void onCancelled(String outstring) {
					Toast.makeText(ctx, "Installation failed, please try again.", Toast.LENGTH_LONG).show();
					File f = new File(outstring);
					deleteDirectory(f);
					
			 }	
			  
		        public void onDismiss(DialogInterface dialog) {
					this.cancel(true);
		        }
		@Override
		protected void onPostExecute(String result) {

			pd.dismiss();
			mWakeLock.release();
		
			if (result==null){
			Intent i = new Intent(PlaysListActivity.this,PlaysDetailActivity.class);
		
	
			
			
			i.putExtra("position",childPos);
			i.putExtra("playsName",playName);
			i.putExtra("playsId",playId);
			i.putExtra("mNote","Home" );
			i.putExtra("mVersion",mVersion);
			i.putExtra("mChapter","0");
			startActivity(i);}
			else{
				Toast.makeText(ctx, "Installation failed, please try again.", Toast.LENGTH_LONG).show();
				File f = new File(result);
				boolean deleted = f.delete();
			}
			super.onPostExecute(result);
	    	
			
		}
	    @Override
	    protected String doInBackground(String... params) {
	    	childPos=params[0];
	    	playId=params[1];
			playName=params[2];
			mVersion = params[3];

			int done = 0;
	
			  
			  
	    	File mFirstVersionFile = new File(FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+playId+".epub");
	    	//System.out.println(mFirstVersionFile);
	    	String[] i={"","","","",""};
	  
		
		       String mFilename=playId+".epub";
		       String mFilepath=FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+mFilename;
		      //System.out.println(mFilepath);
		      
		       ctx = findViewById(R.id.s_main_title).getContext();
		 		
		      // pd = ProgressDialog.show(ctx, "Installing libretto", "please wait...");
			
		        try {
		        
		            InputStream fInputStream = null;
					try {

					   	 mFirstVersionFile.mkdirs();
					   	 fInputStream = new FileInputStream(mFilepath); 
					   	 ZipFile zip = new ZipFile(mFilepath);
					   	 pd.setIndeterminate(false);
					   	 pd.setMax(zip.size());
					     
					   	 String output = FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+playId+File.separator;
						//System.out.println(output);
					
					  // 	 ZipExtracter.extract(inputStream, output, pd);
					   	 
							ZipInputStream inputStream = new ZipInputStream(fInputStream);
							
						
							
							
							//AssetFileDescriptor mydisc = getAssets().openFd(input);
				//Size of the zip package
							int size = 0;
							int increment = 0;
						//	dialog.setMax((int) size);

							for (ZipEntry entry = inputStream.getNextEntry(); entry != null; entry = inputStream.getNextEntry())
							{
								String innerFileName = output +  entry.getName();
								File innerFile = new File(innerFileName);
							
								if (innerFile.exists()){
									innerFile.delete();
								}
								if (entry.isDirectory()){
//									if(DEBUG)Log.i(TAG, "CREATING FOLDER : " + innerFile.toString());
//									innerFile.mkdirs();
								}else
								{
								
									
									File file = new File(innerFile.getParent());
									
									if(!file.exists()){
										file.mkdirs();
									
									}
									FileOutputStream outputStream = new FileOutputStream(innerFileName.replace("HTMLContent",""));
									final int BUFFER = 4096;

									BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream,
											BUFFER);

									int count = 0;
									byte[] data = new byte[BUFFER];
									while ((count = inputStream.read(data, 0, BUFFER)) != -1)
									{
										bufferedOutputStream.write(data, 0, count);
										increment = (int)(increment + count);
									}
									// --- NEW ---
									
									done++;
									publishProgress(done);
									// ----
									bufferedOutputStream.flush();
									bufferedOutputStream.close();
									outputStream.close();
								}

								inputStream.closeEntry();
							}
							inputStream.close();
							
					   	 
					   	 
					   	 
					   	 
					   	 
					   	 
					   	 
					   	 
						//System.out.println("back");
						inputStream.close();
						
						
						 File mVersionFile = new File(output+File.separator+"nav.xhtml");

					     	
					 		try {
					 			InputStream is = new FileInputStream(mVersionFile);
					 			String playid = VersionParser.parsePlayVersion(is,null,ctx,CONTENT_LOCATION);
							
					 			
					 			CsvToSqliteImport.readFromCsvForAudioTable(playid,null,ctx);
					 			
					 			//	int rowUpdated=getContentResolver().insert(Uri.parse(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.PLAY_PATH), cv, AudioDAO.COLUMN_NAME_AUDIO_VERSION_ID + "=\"" + Version +"\" and "+ AudioDAO.COLUMN_NAME_AUDIO_PLAY_ID + "=\"" + mPlaysId+"\"" , null);
								
					 			//VersionBean playVersionData =VersionParser.parsePlayVersion(is,thisContext.,thisContext,CONTENT_LOCATION);
					 			
					 			
					 			
					 		  
						
								
					 			
					 			
					 			
					 			//HomeActivity.refreshCarousel();
					 		
					 			
					 		} catch (FileNotFoundException e) {
					 			// TODO Auto-generated catch block

					 			e.printStackTrace();
					 		}
							}
							catch (Exception e){
								e.printStackTrace();
								return mFilepath;
							}
						
						
						//publishProgress(-2);
					} 
					catch (Exception e) 
					{
						return mFilepath;
						//publishProgress(-4);
					}
		        
		        return null;
	    }
	  
	}
   
	
	
	
	
	
	
	
	
	
	
	
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
				i.putExtra("mChapter","0");
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
		ContentResolver cr = v.getContext().getContentResolver();
		cr.delete(Uri.parse(LibrettoContentProvider.CONTENT_URI+"/"+LibrettoContentProvider.PLAY_PATH), "PLAY_LONG_ID=\""+delID+"\"", null);
		cr.delete(Uri.parse(LibrettoContentProvider.CONTENT_URI+"/"+LibrettoContentProvider.VERSION_PATH),"PLAY_ID=\""+delID+"\"",null);
		cr.delete(Uri.parse(LibrettoContentProvider.CONTENT_URI+"/"+LibrettoContentProvider.AUDIO_PATH),"PLAY_ID=\""+delID+"\"",null);
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
		WakeLock mWakeLock;
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        // take CPU lock to prevent CPU from going off if the user 
	        // presses the power button during download
	        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
	        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
	             getClass().getName());
	        mWakeLock.acquire();
	        pd.show();
	    }
		 @Override
		    protected void onProgressUpdate(Integer... progress) {
		        super.onProgressUpdate(progress);
		        // if we get here, length is known, now set indeterminate to false
		        pd.setIndeterminate(false);
		        pd.setMax(100);
		        pd.setProgress(progress[0]);
		        
		    }
		 @Override
			protected void onCancelled(String outstring) {
				Toast.makeText(ctx, "Installation failed, please try again.", Toast.LENGTH_LONG).show();
				File f = new File(outstring);
				boolean deleted = f.delete();
		 }
		  public void onDismiss(DialogInterface dialog) {
				this.cancel(true);
	        }
		@Override
		protected void onPostExecute(String outstring) {
			pd.dismiss();
			
			mWakeLock.release();
			//pd = ProgressDialog.show(ctx, "Installing libretto", "please wait...");
			if (outstring == null){
			final UnzipEPUB unzipper = new UnzipEPUB(); 
			pd = new ProgressDialog(ctx);
	   		pd.setMessage("Installing libretto");
	   		pd.setIndeterminate(true);
	   		
	   		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	   		pd.setCancelable(false);
   		    unzipper.execute(childPos,playId,playName,playVersion);
			}
	
			else{
				Toast.makeText(ctx, "Installation failed, please try again.", Toast.LENGTH_LONG).show();
				File f = new File(outstring);
				boolean deleted = f.delete();
			}
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
	            
	            return null;
			     
			 
			
	            
	         	    	   	
	        } catch (Exception e) {
	        e.printStackTrace();
	        return outstring;
	        }
	   
	    }
	    public void downloadContent(String urlString,String outstring,String groupPosition, String childPosition){
	    	try{
	    	URL url = new URL(urlString);
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
	    	
	    }
	    	catch (Exception e){
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


