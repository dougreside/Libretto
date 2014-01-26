package org.nypl.database;

import java.util.ArrayList;
import java.util.HashMap;

import org.nypl.LibrettoContentProvider;
import org.nypl.dataholder.PlaysBean;
import org.nypl.dataholder.VersionBean;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;


public class PlayDAO {

	public static final String TABLE_NAME = "PLAY"; 

	public static final String COLUMN_NAME_PLAY_ID = "_id"; 
	public static final String COLUMN_NAME_PLAY_NAME = "PLAY_NAME"; 
	public static final String COLUMN_NAME_PLAY_LONG_ID = "PLAY_LONG_ID"; 
	public static final String COLUMN_NAME_PLAY_IMAGE = "IMAGE"; 
	public static final String COLUMN_NAME_PLAY_URL = "PLAY_URL"; 
	public static final String COLUMN_NAME_PLAY_AUTHORS = "AUTHORS"; 

	public static final String COLUMN_NAME_PLAY_VERSION= "VERSION_NAME"; 

	public static final String COLUMN_NAME_PLAY_VERSION_ID= "_id"; 
	public static final String COLUMN_NAME_PLAY_ID1= "PLAYID";
	public static final String COLUMN_NAME_SCROLL_POSITION= "SCROLL_POSITION";
	private static int COLUMN_INDEX_PLAY_ID = -1; 
	private static int COLUMN_INDEX_PLAY_LONG_ID= -1; 
	private static int COLUMN_INDEX_PLAY_ID1 = -1; 
	private static int COLUMN_INDEX_PLAY_NAME = -1;
	private static int COLUMN_INDEX_PLAY_IMAGE = -1;
	private static int COLUMN_INDEX_PLAY_URL = -1;
	private static int COLUMN_INDEX_PLAY_AUTHORS = -1;

	private static int COLUMN_INDEX_PLAY_VERSION = -1;

	private static int COLUMN_INDEX_PLAY_VERSION_ID = -1;
	private static int COLUMN_INDEX_PLAY_SCROLL_POSITION = -1;
	
	

	private static ArrayList<String> keyList = null;
	private static  ArrayList<PlaysBean> playsList = null;
	private static String alphabet;
	private static void setColumns(Cursor cursor){
		
		COLUMN_INDEX_PLAY_ID = cursor.getColumnIndex(COLUMN_NAME_PLAY_ID);
		COLUMN_INDEX_PLAY_SCROLL_POSITION = cursor.getColumnIndex(COLUMN_NAME_SCROLL_POSITION);
		System.out.println("THE COLUMN # IS: "+COLUMN_INDEX_PLAY_SCROLL_POSITION);
		COLUMN_INDEX_PLAY_LONG_ID = cursor.getColumnIndex(COLUMN_NAME_PLAY_LONG_ID);
		COLUMN_INDEX_PLAY_ID1 = cursor.getColumnIndex(COLUMN_NAME_PLAY_ID1);
		COLUMN_INDEX_PLAY_NAME = cursor.getColumnIndex(COLUMN_NAME_PLAY_NAME);
		COLUMN_INDEX_PLAY_IMAGE = cursor.getColumnIndex(COLUMN_NAME_PLAY_IMAGE);
		COLUMN_INDEX_PLAY_URL = cursor.getColumnIndex(COLUMN_NAME_PLAY_URL);

		COLUMN_INDEX_PLAY_AUTHORS = cursor.getColumnIndex(COLUMN_NAME_PLAY_AUTHORS);
	
		COLUMN_INDEX_PLAY_VERSION = cursor.getColumnIndex(COLUMN_NAME_PLAY_VERSION);
		
		COLUMN_INDEX_PLAY_VERSION_ID = cursor.getColumnIndex(COLUMN_NAME_PLAY_VERSION_ID);
		COLUMN_INDEX_PLAY_SCROLL_POSITION = cursor.getColumnIndex(COLUMN_NAME_SCROLL_POSITION);
	}

	public static ArrayList<PlaysBean> getFeaturedPlays(Context ctx){
		ArrayList<PlaysBean> list = new ArrayList<PlaysBean>();
	Cursor cursor =ctx.getContentResolver().query(Uri.withAppendedPath(LibrettoContentProvider.CONTENT_URI,
			LibrettoContentProvider.PLAY_PATH), null, null, null, null);
	///.query(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.PLAY_PATH, null, null, null, null);
		if(cursor != null && cursor.getCount()>0){
			setColumns(cursor);
			for(int i=0;i<cursor.getCount();i++){
				cursor.moveToPosition(i);
				PlaysBean plays = new PlaysBean();
				plays.setPlayID(cursor.getString(COLUMN_INDEX_PLAY_LONG_ID));
				plays.setPlayName(cursor.getString(COLUMN_INDEX_PLAY_NAME));
				plays.setPlayImage(cursor.getString(COLUMN_INDEX_PLAY_IMAGE));
				plays.setPlayUrl(cursor.getString(COLUMN_INDEX_PLAY_URL));
				plays.setPlayAuthors(cursor.getString(COLUMN_INDEX_PLAY_AUTHORS));
				plays.setScrollPosition(cursor.getString(COLUMN_INDEX_PLAY_SCROLL_POSITION));
				list.add(plays);
			}
		}
		cursor.close();
		
		return list;
		
	}
	

	
	

	public static HashMap<String, Object> getAllBookmark(Context ctx){



		keyList = new ArrayList<String>();
		HashMap<String, Object> mBookmarkListData=new HashMap<String, Object>();
		for(int j=65;j<=90;j++)
		{
			char c = (char)j;
			alphabet=Character.toString(c);
			Cursor cursor =ctx.getContentResolver().query(Uri.parse(LibrettoContentProvider.CONTENT_URI+"/"+LibrettoContentProvider.PLAY_BOOKMARK_PATH), null, COLUMN_NAME_PLAY_NAME +" LIKE "+"'"+alphabet + "%"+"'"  ,null , null);
			///.query(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.PLAY_PATH, null, null, null, null);
			Log.v("in playdiao cursor",""+cursor.getCount());
			if(cursor != null && cursor.getCount()>0){
				playsList = new ArrayList<PlaysBean>();
				setColumns(cursor);
				keyList.add(alphabet);
				for(int i=0;i<cursor.getCount();i++){
					cursor.moveToPosition(i);
					PlaysBean plays = new PlaysBean();
					plays.setPlayID(cursor.getString(COLUMN_INDEX_PLAY_ID));
					plays.setPlayID(cursor.getString(COLUMN_INDEX_PLAY_ID1));
					plays.setPlayName(cursor.getString(COLUMN_INDEX_PLAY_NAME));
					plays.setPlayImage(cursor.getString(COLUMN_INDEX_PLAY_IMAGE));
					plays.setPlayUrl(cursor.getString(COLUMN_INDEX_PLAY_URL));
					
					plays.setPlayAuthors(cursor.getString(COLUMN_INDEX_PLAY_AUTHORS));
					
					plays.setPlayVersion(cursor.getString(COLUMN_INDEX_PLAY_VERSION));
					plays.setPlayVersionID(cursor.getString(COLUMN_INDEX_PLAY_VERSION_ID));
					plays.setScrollPosition(cursor.getString(COLUMN_INDEX_PLAY_SCROLL_POSITION));
					
					playsList.add(plays);
				}
				mBookmarkListData.put(alphabet, playsList);	
			}
			cursor.close();
		}
		return mBookmarkListData;
	}

	
	
	

	public static  ArrayList<PlaysBean>  getAllVersionBookmark(Context ctx){

		ArrayList<PlaysBean> list = new ArrayList<PlaysBean>();


	
			Cursor cursor =ctx.getContentResolver().query(Uri.parse(LibrettoContentProvider.CONTENT_URI+"/"+LibrettoContentProvider.PLAY_ALLBOOKMARK_PATH), null, null  ,null , null);
			
			if(cursor != null && cursor.getCount()>0){
			
				setColumns(cursor);
				
				for(int i=0;i<cursor.getCount();i++){
					cursor.moveToPosition(i);
					PlaysBean plays = new PlaysBean();
					plays.setPlayID(cursor.getString(COLUMN_INDEX_PLAY_ID));
					plays.setPlayID(cursor.getString(COLUMN_INDEX_PLAY_ID1));
					plays.setPlayName(cursor.getString(COLUMN_INDEX_PLAY_NAME));
					plays.setPlayImage(cursor.getString(COLUMN_INDEX_PLAY_IMAGE));
					plays.setPlayUrl(cursor.getString(COLUMN_INDEX_PLAY_URL));
					
					plays.setPlayAuthors(cursor.getString(COLUMN_INDEX_PLAY_AUTHORS));
				
					plays.setPlayVersion(cursor.getString(COLUMN_INDEX_PLAY_VERSION));
					plays.setPlayVersionID(cursor.getString(COLUMN_INDEX_PLAY_VERSION_ID));
					plays.setScrollPosition(cursor.getString(COLUMN_INDEX_PLAY_SCROLL_POSITION));
					
					list.add(plays);
				}
				
			}
			cursor.close();
		//}
		return list;
	}

	
	
	
	
	public static ArrayList<PlaysBean> getBookmarkDetail(Context ctx,String VersionId){

		ArrayList<PlaysBean> list = new ArrayList<PlaysBean>();
	
			Cursor cursor =ctx.getContentResolver().query(Uri.parse(LibrettoContentProvider.CONTENT_URI+"/"+LibrettoContentProvider.SET_BOOKMARK_PATH), null, VersionId  ,null , null);
			///.query(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.PLAY_PATH, null, null, null, null);
			Log.v("in playdiao cursor",""+cursor.getCount());
			if(cursor != null && cursor.getCount()>0){
				
				setColumns(cursor);
				
				for(int i=0;i<cursor.getCount();i++){
					cursor.moveToPosition(i);
					PlaysBean plays = new PlaysBean();
					plays.setPlayID(cursor.getString(COLUMN_INDEX_PLAY_ID));
					plays.setPlayName(cursor.getString(COLUMN_INDEX_PLAY_NAME));
					plays.setPlayImage(cursor.getString(COLUMN_INDEX_PLAY_IMAGE));
					plays.setPlayUrl(cursor.getString(COLUMN_INDEX_PLAY_URL));
					
					plays.setPlayAuthors(cursor.getString(COLUMN_INDEX_PLAY_AUTHORS));
					plays.setPlayVersion(cursor.getString(COLUMN_INDEX_PLAY_VERSION));
					plays.setPlayVersionID(cursor.getString(COLUMN_INDEX_PLAY_VERSION_ID));
					
					list.add(plays);
				}
				
			}
			cursor.close();
			return list;
		}
	

	
	
	
	
	
	
	
	
	
	public static HashMap<String, Object> getAllPlays(Context ctx){
		
		

		keyList = new ArrayList<String>();
		HashMap<String, Object> mPlaysListData=new HashMap<String, Object>();
		for(int j=65;j<=90;j++)
		{
			char c = (char)j;
			alphabet=Character.toString(c);
	Cursor cursor =ctx.getContentResolver().query(Uri.parse(LibrettoContentProvider.CONTENT_URI+"/"+LibrettoContentProvider.PLAY_PATH), null, COLUMN_NAME_PLAY_NAME +" LIKE ?" , new String[]{alphabet + "%"}, null);
	///.query(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.PLAY_PATH, null, null, null, null);
	Log.v("in playdiao cursor",""+cursor.getCount());
		if(cursor != null && cursor.getCount()>0){
			playsList = new ArrayList<PlaysBean>();
			setColumns(cursor);
			keyList.add(alphabet);
			for(int i=0;i<cursor.getCount();i++){
				cursor.moveToPosition(i);
				
				PlaysBean plays = new PlaysBean();
				plays.setPlayID(cursor.getString(COLUMN_INDEX_PLAY_LONG_ID));
				//plays.setPlayID(cursor.getString(COLUMN_INDEX_PLAY_ID));
				plays.setPlayName(cursor.getString(COLUMN_INDEX_PLAY_NAME));
				plays.setPlayImage(cursor.getString(COLUMN_INDEX_PLAY_IMAGE));
				plays.setPlayAuthors(cursor.getString(COLUMN_INDEX_PLAY_AUTHORS));
				plays.setScrollPosition(cursor.getString(COLUMN_INDEX_PLAY_SCROLL_POSITION));
				plays.setPlayUrl(cursor.getString(COLUMN_INDEX_PLAY_URL));
				
				playsList.add(plays);
			}
			mPlaysListData.put(alphabet, playsList);	
		}
		cursor.close();
		}
		return mPlaysListData;
	}

	
	
	
	
	
	public static ArrayList<String> getKeyList()
	{
		Log.v("keyList:::::::::::::::",""+keyList);
		return keyList;

	}
	
	
	
	public static ArrayList<PlaysBean> getAllSearchPlays(Context ctx,String search){

		ArrayList<PlaysBean> list = new ArrayList<PlaysBean>();
		
			alphabet=search.toString();
	Cursor cursor =ctx.getContentResolver().query(Uri.parse(LibrettoContentProvider.CONTENT_URI+"/"+LibrettoContentProvider.PLAY_PATH), null, COLUMN_NAME_PLAY_NAME +" LIKE ?" , new String[]{"%"+alphabet + "%"}, null);
	///.query(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.PLAY_PATH, null, null, null, null);
	Log.v("in playdiao cursor",""+cursor.getCount());
		if(cursor != null && cursor.getCount()>0){
			
			setColumns(cursor);
			
			for(int i=0;i<cursor.getCount();i++){
				cursor.moveToPosition(i);
				
				PlaysBean plays = new PlaysBean();
				//plays.setPlayID(cursor.getString(COLUMN_INDEX_PLAY_ID));
				plays.setPlayID(cursor.getString(COLUMN_INDEX_PLAY_LONG_ID));
				plays.setPlayName(cursor.getString(COLUMN_INDEX_PLAY_NAME));
				plays.setPlayImage(cursor.getString(COLUMN_INDEX_PLAY_IMAGE));
				plays.setPlayAuthors(cursor.getString(COLUMN_INDEX_PLAY_AUTHORS));
				plays.setScrollPosition(cursor.getString(COLUMN_INDEX_PLAY_SCROLL_POSITION));
				
				plays.setPlayUrl(cursor.getString(COLUMN_INDEX_PLAY_URL));
				
				list.add(plays);
			}
			
		}
		cursor.close();
		
		return list;
	}
	
	
	public static ArrayList<PlaysBean> getPlayName(Context ctx,String PlayId){

		ArrayList<PlaysBean> list = new ArrayList<PlaysBean>();
		
			
	Cursor cursor =ctx.getContentResolver().query(Uri.parse(LibrettoContentProvider.CONTENT_URI+"/"+LibrettoContentProvider.PLAY_PATH), null, COLUMN_NAME_PLAY_ID +"=\""+PlayId+"\"",null, null);
	///.query(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.PLAY_PATH, null, null, null, null);
	Log.v("in playdiao cursor",""+cursor.getCount());
		if(cursor != null && cursor.getCount()>0){
			
			setColumns(cursor);
			
			for(int i=0;i<cursor.getCount();i++){
				cursor.moveToPosition(i);
				
				PlaysBean plays = new PlaysBean();
				//plays.setPlayID(cursor.getString(COLUMN_INDEX_PLAY_ID));
				plays.setPlayID(cursor.getString(COLUMN_INDEX_PLAY_LONG_ID));
				plays.setPlayName(cursor.getString(COLUMN_INDEX_PLAY_NAME));
				plays.setPlayImage(cursor.getString(COLUMN_INDEX_PLAY_IMAGE));
				plays.setPlayAuthors(cursor.getString(COLUMN_INDEX_PLAY_AUTHORS));
				plays.setScrollPosition(cursor.getString(COLUMN_INDEX_PLAY_SCROLL_POSITION));
				plays.setPlayUrl(cursor.getString(COLUMN_INDEX_PLAY_URL));
				
				
				list.add(plays);
			}
			
		}
		cursor.close();
		
		return list;
	}

	
	
	
	

	
	
	public static ArrayList<PlaysBean> getPlayByID(Context ctx,String PlayId){

		ArrayList<PlaysBean> list = new ArrayList<PlaysBean>();
		
		System.out.println("CHECKING FOR PLAY BY ID "+PlayId);	
		System.out.println(LibrettoContentProvider.CONTENT_URI+"/"+LibrettoContentProvider.PLAY_PATH);
		Uri uri = Uri.withAppendedPath(LibrettoContentProvider.CONTENT_URI,
				LibrettoContentProvider.PLAY_PATH);
		ContentResolver cr = ctx.getContentResolver();
		Cursor cursor =cr.query(uri, null, COLUMN_NAME_PLAY_LONG_ID +"=\""+PlayId+"\"",null, null);
	///.query(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.PLAY_PATH, null, null, null, null);
  System.out.println("Howdy");
	System.out.println("cursor: "+cursor.getCount());
   
		if(cursor != null && cursor.getCount()>0){
			
			setColumns(cursor);
			
			for(int i=0;i<cursor.getCount();i++){
				cursor.moveToPosition(i);
				
				PlaysBean plays = new PlaysBean();
				//plays.setPlayID(cursor.getString(COLUMN_INDEX_PLAY_ID));
				plays.setPlayID(cursor.getString(COLUMN_INDEX_PLAY_LONG_ID));
				plays.setPlayName(cursor.getString(COLUMN_INDEX_PLAY_NAME));
				plays.setPlayImage(cursor.getString(COLUMN_INDEX_PLAY_IMAGE));
				plays.setPlayAuthors(cursor.getString(COLUMN_INDEX_PLAY_AUTHORS));
				plays.setScrollPosition(cursor.getString(COLUMN_INDEX_PLAY_SCROLL_POSITION));
				plays.setPlayUrl(cursor.getString(COLUMN_INDEX_PLAY_URL));
				
				
				list.add(plays);
			}
			
		}
		cursor.close();
		
		return list;
	}

	public static int updateScrollPosition(Context ctx, String playid, String sp){
		ContentResolver cr = ctx.getContentResolver();
		ContentValues cv =new ContentValues();
		cv.put(PlayDAO.COLUMN_NAME_SCROLL_POSITION, sp);
		int rowUpdated = cr.update(Uri.parse(LibrettoContentProvider.CONTENT_URI+"/"+LibrettoContentProvider.PLAY_PATH), cv, COLUMN_NAME_PLAY_LONG_ID + "=\"" + playid+"\"" , null);
		System.out.println("UP UP UP IPDATE: "+rowUpdated);
	
	    	 
		return rowUpdated;
		
	}
	
	
	
	
	
}
