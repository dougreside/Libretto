package org.nypl.database;


import org.nypl.MoverContentProvider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class CsvReader {
	private static String TAG = "CSVReader";
	private static String AUDIO_TABLE_NAME="AUDIO";
	public static void insertPlayTable(SQLiteDatabase db, Context context ,String playid, String play_name, String image, String authors, String url) {

	/*	SqliteDBHelper mDBHelper = new SqliteDBHelper(context);
		synchronized (mDBHelper) {
		// TODO Auto-generated method stub
		
	SQLiteDatabase db = mDBHelper.getReadableDatabase();
	*/
	
			ContentValues values = new ContentValues();
			values.put(DatabaseTable.TABLE_PLAY_LONG_ID, playid);
			values.put(DatabaseTable.TABLE_PLAY_NAME, play_name);
			values.put(DatabaseTable.TABLE_PLAY_IMAGE, image);
			values.put(DatabaseTable.TABLE_PLAY_AUTHORS, authors);
			values.put(DatabaseTable.TABLE_PLAY_URL, url);
			//values.put("initial", initial);
			//db.insert(TABLE_NAME, null, values);
			

			long result = db.insert(PlayDAO.TABLE_NAME , null, values);
			Log.v("result",""+result);
			//Object o = context.getContentResolver();
			///Uri uri = context.getContentResolver().insert(Uri.parse(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.PLAY_PATH), values);
			
			//Log.i(TAG, "INSERT::::::::"+"::"+uri);
			///Log.i(TAG, "INSERT::::::::"+"::"+result);
			
	/*	db.close();
		mDBHelper.close();
	}*/
	}
	public static void insertVersionTable(SQLiteDatabase db, Context context ,String versionid, String play_id, String html_file,String version_name) {
		

	
		//Cursor cursor2 = db.rawQuery("select PLAY_ID from VERSION where PLAY_ID='"+play_id+"'",null);
		//cursor2.moveToFirst();
		/*System.out.println("play_id:::::::::::::::111111111111::::::::::::::"+play_id);
		System.out.println("versionid:::::::::::::::111111111111::::::::::::::"+versionid);
		System.out.println("html_file:::::::::::::::111111111111::::::::::::::"+html_file);
		System.out.println("version_name:::::::::::::::111111111111::::::::::::::"+version_name);
		System.out.println("audioname:::::::::::::::111111111111::::::::::::::"+audioname);*/
		if(play_id!=null )
		{	
			System.out.println("play_id:::::::::::::::2222::::::::::::::"+play_id);
			System.out.println("versionid:::::::::::::::2222222::::::::::::::"+versionid);
			System.out.println("html_file:::::::::::::::222::::::::::::::"+html_file);
		

			//	System.out.println("audioname:::::::::::::::22222::::::::::::::"+audioname);
			ContentValues values = new ContentValues();
			values.put(DatabaseTable.TABLE_VERSION_ID, versionid);
			values.put(DatabaseTable.TABLE_VERSION_PLAY_ID, play_id);
			values.put(DatabaseTable.TABLE_VERSION_HTML_FILE, html_file);
			values.put(DatabaseTable.TABLE_VERSION_NAME, version_name);

			//values.put("initial", initial);
			//db.insert(TABLE_NAME, null, values);
			

			long result = db.insert(VersionDAO.TABLE_NAME , null, values);
			Log.v("result","INSERTED: "+result);
		}
		
			//Uri uri = context.getContentResolver().insert(Uri.parse(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.VERSION_PATH), values);
			//Log.i(TAG, "INSERT::::::::"+"::"+uri);
			///Log.i(TAG, "INSERT::::::::"+"::"+result);
			
	/*	db.close();
		mDBHelper.close();
	}*/
	}
	public static void insertAnchorTable(SQLiteDatabase db, Context context ,String aplay_id, String play_version_id, String anchor_html_id) {

	/*	SqliteDBHelper mDBHelper = new SqliteDBHelper(context);
		synchronized (mDBHelper) {
		// TODO Auto-generated method stub
		
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
	*/
	
			ContentValues values = new ContentValues();
			values.put(DatabaseTable.TABLE_ANCHOR_PLAY_ID, aplay_id);
			values.put(DatabaseTable.TABLE_ANCHOR_VERSION_PLAY_ID, play_version_id);
			values.put(DatabaseTable.TABLE_ANCHOR_HTML_ID, anchor_html_id);
		

			long result = db.insert(AnchorDOA.TABLE_NAME , null, values);
			Log.v("result",""+result);
			//Uri uri = context.getContentResolver().insert(Uri.parse(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.ANCHOR_PATH), values);
			//Log.i(TAG, "INSERT::::::::"+"::"+uri);
			////Log.i(TAG, "INSERT::::::::"+"::"+result);
	/*		
		db.close();
		mDBHelper.close();
	}*/
	}
	public static void insertMediaTable(SQLiteDatabase db, Context context,
			String mediaid,String media_name) {

		ContentValues values = new ContentValues();
		values.put(DatabaseTable.TABLE_MEDIA_ID, mediaid);
		values.put(DatabaseTable.TABLE_MEDIA_NAME, media_name);
		
		long result = db.insert(MediaDOA.TABLE_NAME , null, values);
		Log.v("result",""+result);
		
	}
	
	
	public static void insertAudioTable(SQLiteDatabase db, Context context,String Playid,String clipid,String clipfrom, String clipto, String clipversionid,String audioPath) {
		System.out.println("CLIP TO :" +clipto);
		ContentValues values = new ContentValues();
		if (clipfrom.trim().length()<1){
			clipfrom="0";

		}
		if (clipto.trim().length()<1){
			clipto="0";

		}
		values.put(DatabaseTable.TABLE_AUDIO_CLIP_ID, clipid);
		values.put(DatabaseTable.TABLE_AUDIO_PLAY_ID, Playid);
		values.put(DatabaseTable.TABLE_AUDIO_CLIP_FROM, clipfrom);
		values.put(DatabaseTable.TABLE_AUDIO_CLIP_TO, clipto);
		values.put(DatabaseTable.TABLE_AUDIO_VERSION_ID, clipversionid);
		values.put(DatabaseTable.TABLE_AUDIO_PATH, audioPath);
		System.out.println(values);
	
		System.out.println(AudioDAO.TABLE_NAME);
		context.getContentResolver().insert(Uri.parse(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.AUDIO_PATH), values);
		//long result = db.insert(AudioDAO.TABLE_NAME, null, values);
		//Log.v("result",""+result);
		
	}
	
	
}
