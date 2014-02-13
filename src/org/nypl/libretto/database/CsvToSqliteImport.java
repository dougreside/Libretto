package org.nypl.libretto.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.nypl.libretto.dataholder.AudioBean;
import org.nypl.libretto.dataholder.ChaptersBean;
import org.nypl.libretto.dataholder.PlaysBean;
import org.nypl.libretto.dataholder.VersionBean;
import org.nypl.libretto.parsing.AudioFileParser;
import org.nypl.libretto.parsing.PlayJsonParser;
import org.nypl.libretto.parsing.VersionParser;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.widget.TextView;

public class CsvToSqliteImport {
	SqliteDBHelper mDbHelper;
	String notification = "Data imported!";
	TextView tView;


	private static String aplay_id;
	private static String play_version_id;
	private static String anchor_html_id;
	public static String CONTENT_LOCATION ;
	private static File FilePath = Environment.getExternalStorageDirectory();
	public static String filePath;
	private static InputStream is;
	private static ArrayList<AudioBean> playAudioData;
	private static ArrayList<PlaysBean> PlayJsonData;
	public static String stringFromStream = null;
	public SQLiteDatabase appDb;
	
	public static void readFromCsvForPlayTable(SQLiteDatabase db, Context context) throws IOException
	{
		
		CONTENT_LOCATION = "Android/data/"+context.getPackageName()+File.separator+"contents";
		filePath = FilePath.getAbsolutePath() + File.separator+ CONTENT_LOCATION+ File.separator ;
		 
		File mPlayJsonFile = new File(FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+"playjsonformat.json");
		try {

			is = new FileInputStream(mPlayJsonFile.toString());
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
		
		try {
			stringFromStream = convertStreamToString(is);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
		//System.out.println(stringFromStream);
		PlayJsonData = PlayJsonParser.parsePlayList(stringFromStream);
		for(int i=0;i<PlayJsonData.size();i++){
			String id = PlayJsonData.get(i).getPlayID();
		//	//System.out.println(id+" : "+PlayDAO.getPlayByID(context, id).size());
			if (db.isDbLockedByCurrentThread())
			{
				CsvReader.insertPlayTable(db,context,PlayJsonData.get(i).getPlayID(), PlayJsonData.get(i).getPlayName(), PlayJsonData.get(i).getPlayImage(),  PlayJsonData.get(i).getPlayAuthors(),PlayJsonData.get(i).getPlayUrl());
				
			}else
				if ((PlayDAO.getPlayByID(context, id)).size()==0){
				
			
			
		CsvReader.insertPlayTable(db,context,PlayJsonData.get(i).getPlayID(), PlayJsonData.get(i).getPlayName(), PlayJsonData.get(i).getPlayImage(),  PlayJsonData.get(i).getPlayAuthors(),PlayJsonData.get(i).getPlayUrl());
		
			}
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

	
		try {
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

		InputStreamReader inputStreamReader = new InputStreamReader(is);
		BufferedReader in = new BufferedReader(inputStreamReader);
		String reader = "";
		String[] RowData =null;
		int index=0;
		while ((reader = in.readLine()) != null){
			if(index == 0) {
				index++;
				continue; //skip first line
			}
			RowData = reader.split("\",");
			
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
		String VersionId;
		String chapterhtml;
		if (db==null){
		ArrayList<VersionBean> vList = VersionDAO.getVersionOf(context,playId);
	
		for(int j=0 ; j<vList.size(); j++){
			
		    //PlayId = vList.get(j).getVersionPlayID();
			VersionId = vList.get(j).getVersionUUID();
			ArrayList<ChaptersBean> cList = ChaptersDAO.getChaptersForVersion(context, VersionId);
			for (int m=0;m<cList.size();m++){
			chapterhtml = cList.get(m).getHTMLFile();
		

		try{
		String fp = FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+chapterhtml;

		
	       

		playAudioData = AudioFileParser.parseAudio(fp,VersionId);
		for(int i=0;i<playAudioData.size();i++){
			CsvReader.insertAudioTable(db,context, playId ,playAudioData.get(i).getClipID(), playAudioData.get(i).getClipFrom(), playAudioData.get(i).getClipTo(),playAudioData.get(i).getClipVersionId(),playAudioData.get(i).getAudioPath());
			
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		}
		}
		//
		
		}
else{
	filePath = FilePath.getAbsolutePath() + File.separator+ CONTENT_LOCATION+ File.separator ;
	Cursor cursor2 = db.rawQuery("select * from VERSION where PLAY_ID = \""+playId+"\"",null);
	for(int j=0 ; cursor2!=null && j<cursor2.getCount(); j++){
		cursor2.moveToPosition(j);
		 VersionId = cursor2.getString(cursor2.getColumnIndex("VERSION_ID"));
		 Cursor cursor3 = db.rawQuery("select * from CHAPTERS where CHAPTER_VERSION_ID = \""+VersionId+"\"",null);
			
		 
		 
		 chapterhtml = cursor3.getString(cursor2.getColumnIndex("HTML_FILE"));
	

	try{
	String fp = FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+chapterhtml;


       

	playAudioData = AudioFileParser.parseAudio(fp,VersionId);
	}
	catch (Exception e){
		e.printStackTrace();
	}
	for(int i=0;i<playAudioData.size();i++){
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
