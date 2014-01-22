package org.nypl.database;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.nypl.dataholder.AudioBean;
import org.nypl.dataholder.ChaptersBean;
import org.nypl.dataholder.PlaysBean;
import org.nypl.dataholder.VersionBean;
import org.nypl.parsing.AudioFileParser;
import org.nypl.parsing.PlayJsonParser;
import org.nypl.parsing.VersionParser;
import org.nypl.utils.ZipExtracter;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

public class CsvToSqliteImport {
	SqliteDBHelper mDbHelper;
	String notification = "Data imported!";
	TextView tView;

	private static String playid;
	private static String play_name;
	private static String image;

	private static String authors;


	private static String play_id;
	private static String html_file;
	private static String version_name;
	private static String bookmark;
	private static String note;
	private static String versionid;
	private static String anchorid;
	private static String aplay_id;
	private static String play_version;
	private static String play_version_id;
	private static String anchor_html_id;
	private static String mediaid;
	private static String media_name;
	private File xml_file_path;
	public static String CONTENT_LOCATION ;
	private static File FilePath = Environment.getExternalStorageDirectory();
	public static String filePath;
	private static InputStream is;
	private static ArrayList<VersionBean> playVersionData;
	private static ArrayList<AudioBean> playAudioData;
	private static ArrayList<PlaysBean> PlayJsonData;
	public static String stringFromStream = null;
	private static ProgressDialog pd;
	private static ArrayList<VersionBean> versionDetailList;

	public static void readFromCsvForPlayTable(SQLiteDatabase db, Context context) throws IOException
	{
		System.out.println("XXXXXXXXXXXXXXXXXX readFromCsvForPlayTable");
		
		CONTENT_LOCATION = "Android/data/"+context.getPackageName()+File.separator+"contents";
		filePath = FilePath.getAbsolutePath() + File.separator+ CONTENT_LOCATION+ File.separator ;
	//	File mPlayJsonFile = new File(FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+"playjsonformat.txt");
		File mPlayJsonFile = new File(FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+"playjsonformat.json");

		System.out.println("imageFile data ::::::::::::::::::::::1111::::::::::" +mPlayJsonFile.exists() );  


		try {

			is = new FileInputStream(mPlayJsonFile.toString());
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
		
		try {
			stringFromStream = convertStreamToString(is);
			System.out.println("FIRST PASS AT JSON: "+stringFromStream);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
		
		PlayJsonData = PlayJsonParser.parsePlayList(stringFromStream);
		
		System.out.println("PlayJsonData::::::::::::::::::::::"+PlayJsonData.get(0).getPlayName());
		for(int i=0;i<PlayJsonData.size();i++){
			
		CsvReader.insertPlayTable(db,context,PlayJsonData.get(i).getPlayID(), PlayJsonData.get(i).getPlayName(), PlayJsonData.get(i).getPlayImage(),  PlayJsonData.get(i).getPlayAuthors(),PlayJsonData.get(i).getPlayUrl());
		/*String mUrl = PlayJsonData.get(i).getPlayUrl();
		
		File mFirstVersionFile = new File(FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+PlayJsonData.get(i).getPlayID());
	   	   System.out.println("FIRST FILE: "+FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+PlayJsonData.get(i).getPlayID());
   	   	if(!mFirstVersionFile.exists())
   	   	{
   	
   	  
  
	      
	
	       String mFilename=PlayJsonData.get(i).getPlayID()+".epub";
	       String mFilepath=FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+mFilename;
	       String playId=PlayJsonData.get(i).getPlayID();
	       System.out.println("UNZIPPING "+mFilepath);
	   
	        try {
	        
	            InputStream inputStream = null;
				try {
					
				
				       
					
				
				   	 mFirstVersionFile.mkdirs();
				   	   	
				  
				 
				    inputStream = new FileInputStream(mFilepath);   
					ZipExtracter.extract(inputStream, FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+PlayJsonData.get(i).getPlayID()+File.separator);
					
					inputStream.close();
					try{
						System.out.println("Version 1");
						String playid = readFromCsvForVersionTable(PlayJsonData.get(i).getPlayID(),db,context);
			   	   		System.out.println("Audio");
						readFromCsvForAudioTable(playid,db,context);
						}
						catch (Exception e){
							System.out.println("reading from version or audio fail");
						}
					//publishProgress(-2);
				} 
				catch (Exception e) 
				{
					//publishProgress(-4);
				}
	         		    	   	
	        } catch (Exception e) {
	        }
	       
	       
   	   
   	   	
		
		}
   	   	else{
   	 	System.out.println("Version 2");
		String playid = readFromCsvForVersionTable(PlayJsonData.get(i).getPlayID(),db,context);
	   		System.out.println("Audio");
		readFromCsvForAudioTable(playid,db,context);
   	 
   	   	}*/

		}

	}

	public static String convertStreamToString(InputStream is) throws Exception {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();
	    String line = null;

	    while ((line = reader.readLine()) != null) {
	        sb.append(line);
	    }

	    is.close();

	    return sb.toString();
	}
	public static String readFromCsvForVersionTable(String playId, SQLiteDatabase db, Context context) throws IOException
	{



		CONTENT_LOCATION = "Android/data/"+context.getPackageName()+File.separator+"contents"+File.separator+playId;
		filePath = FilePath.getAbsolutePath() + File.separator+ CONTENT_LOCATION+ File.separator ;
		//File mVersionFile = new File(FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+"version.xml");
		File mVersionFile = new File(FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+"toc.ncx");
		System.out.println(FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+"toc.ncx");
		System.out.println("imageFile data ::::::::::::::::::::::1111::::::::::" +mVersionFile.exists() );  

	
		try {
			Log.v("CSVImport","GOTCHA");
			is = new FileInputStream(mVersionFile);
			
			return VersionParser.parsePlayVersion(is,db,context,CONTENT_LOCATION);
			
		
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
			return "";
		}
	
		
		
	}



	
	public static void readFromCsvForAnchorTable(SQLiteDatabase db, Context context) throws IOException
	{
		InputStream is=context.getAssets().open("anchor.csv");  
		Log.v("is",""+is);

		InputStreamReader inputStreamReader = new InputStreamReader(is);
		BufferedReader in = new BufferedReader(inputStreamReader);
		Log.v("in path",""+in);
		String reader = "";
		String[] RowData =null;
		int index=0;
		while ((reader = in.readLine()) != null){
			if(index == 0) {
				index++;
				continue; //skip first line
			}
			RowData = reader.split("\",");


			Log.v("RowData",""+RowData.length);
			anchorid = RowData[0].replace("\"", "");
			aplay_id = RowData[1].replace("\"", "");
			play_version_id = RowData[2].replace("\"", "");
			anchor_html_id = RowData[3].replace("\"", "");


			CsvReader.insertAnchorTable(db,context, aplay_id, play_version_id, anchor_html_id);


		}
		in.close();

	}

	
	public static void readFromCsvForAudioTable(String playId, SQLiteDatabase db, Context context) throws IOException
	{

		CONTENT_LOCATION = "Android/data/"+context.getPackageName()+File.separator+"contents"+File.separator+playId;
		filePath = FilePath.getAbsolutePath() + File.separator+ CONTENT_LOCATION+ File.separator ;
		String PlayId;
		String VersionId;
		String chapterhtml;

		Log.v("Audio","Start read");
		if (db==null){
		ArrayList<VersionBean> vList = VersionDAO.getVersionOf(context,playId);
	
		for(int j=0 ; j<vList.size(); j++){
			
		    //PlayId = vList.get(j).getVersionPlayID();
			VersionId = vList.get(j).getVersionUUID();
			ArrayList<ChaptersBean> cList = ChaptersDAO.getChaptersForVersion(context, VersionId);
			for (int m=0;m<cList.size();m++){
			chapterhtml = cList.get(m).getHTMLFile();

			System.out.println(chapterhtml);
		

		try{
		
		Log.v("Audio","Audio Parsing sent");
		String fp = FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+chapterhtml;

		
	       

		playAudioData = AudioFileParser.parseAudio(fp,VersionId);
		Log.v("Audio","Audio Parsing retrieved");
		for(int i=0;i<playAudioData.size();i++){
			Log.v("Audio",playAudioData.get(i).getClipID()+" "+playAudioData.get(i).getClipFrom()+" "+playAudioData.get(i).getClipTo()+" "+playAudioData.get(i).getClipVersionId());	
			CsvReader.insertAudioTable(db,context, playId ,playAudioData.get(i).getClipID(), playAudioData.get(i).getClipFrom(), playAudioData.get(i).getClipTo(),playAudioData.get(i).getClipVersionId(),playAudioData.get(i).getAudioPath());
			
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		}
		}
		//System.out.println("playaudioData.get(i).getVersionPlayID()::::::::::::::::::::::"+playAudioData.get(0).getClipID());
		
		}
else{
	filePath = FilePath.getAbsolutePath() + File.separator+ CONTENT_LOCATION+ File.separator ;
	Log.v("Audio","Start read");
	Cursor cursor2 = db.rawQuery("select * from VERSION where PLAY_ID = \""+playId+"\"",null);
	Log.v("Audio",cursor2.getCount()+"");
	for(int j=0 ; cursor2!=null && j<cursor2.getCount(); j++){
		cursor2.moveToPosition(j);
		Log.v("Audio",j+"");
		 VersionId = cursor2.getString(cursor2.getColumnIndex("VERSION_ID"));
		 Cursor cursor3 = db.rawQuery("select * from CHAPTERS where CHAPTER_VERSION_ID = \""+VersionId+"\"",null);
			
		 
		 
		 chapterhtml = cursor3.getString(cursor2.getColumnIndex("HTML_FILE"));
			
		System.out.println(chapterhtml);
	

	try{
	
	Log.v("Audio","Audio Parsing sent");
	String fp = FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+chapterhtml;


       

	playAudioData = AudioFileParser.parseAudio(fp,VersionId);
	Log.v("Audio","Audio Parsing retrieved");
	}
	catch (Exception e){
		e.printStackTrace();
	}
	for(int i=0;i<playAudioData.size();i++){
		Log.v("Audio",playAudioData.get(i).getClipID()+" "+playAudioData.get(i).getClipFrom()+" "+playAudioData.get(i).getClipTo()+" "+playAudioData.get(i).getClipVersionId());	
		CsvReader.insertAudioTable(db,context, playId ,playAudioData.get(i).getClipID(), playAudioData.get(i).getClipFrom(), playAudioData.get(i).getClipTo(),playAudioData.get(i).getClipVersionId(),playAudioData.get(i).getAudioPath());
		
		}
   
		}
}
	///	db.close();
	}
	

	
	 static boolean haveNetworkConnection(Context mcontext) {
	        boolean haveConnectedWifi = false;
	        boolean haveConnectedMobile = false;

	        ConnectivityManager cm = (ConnectivityManager) mcontext.getSystemService(Context.CONNECTIVITY_SERVICE);
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
