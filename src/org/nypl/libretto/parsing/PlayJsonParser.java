package org.nypl.libretto.parsing;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nypl.libretto.database.CsvToSqliteImport;
import org.nypl.libretto.dataholder.PlaysBean;



public class PlayJsonParser {
	/*
	 * First Json array Object
	 */
	public static final String PLAYS = "Plays";

	/*
	 * second Json array Object
	 */
	public static final String PLAY_ID = "playid";
	public static final String PLAY_NAME = "playname";
	public static final String PLAY_IMAGE_URL = "imageurl";
	public static final String PLAY_AUTHOR = "author";
	public static final String PLAY_URL = "playUrl";
	
	public static  ArrayList<PlaysBean> playsList ;
	private static String mPlayID;
	private static String mPlayName;
	private static String mImageUrl;
	private static String mAuthorName;

	private static String mPlayUrl;
	private static JSONArray PlaysData_Array;

	public static ArrayList<PlaysBean> parsePlayList(String is){
		playsList = new ArrayList<PlaysBean>();
		String startNode = "";
	
		try { 

			JSONObject jsonObj = new JSONObject(is);
		   
			PlaysData_Array = jsonObj.getJSONArray(PLAYS);
			for(int i = 0; i < PlaysData_Array.length(); i++){
				PlaysBean currentplaylist = new PlaysBean();
				JSONObject playObject = PlaysData_Array.getJSONObject(i);
				mPlayID = playObject.getString(PLAY_ID);
				currentplaylist.setPlayID(mPlayID);
				
				mPlayName = playObject.getString(PLAY_NAME);
				currentplaylist.setPlayName(mPlayName);
			
				mImageUrl = playObject.getString(PLAY_IMAGE_URL);
				currentplaylist.setPlayImage(mImageUrl);
				mAuthorName = playObject.getString(PLAY_AUTHOR);
				currentplaylist.setPlayAuthors(mAuthorName);
				mPlayUrl = playObject.getString(PLAY_URL);
				currentplaylist.setPlayUrl(mPlayUrl);
				currentplaylist.setScrollPosition(startNode);
				playsList.add(currentplaylist);
			}
		
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return playsList;
	}
	public static void addPlayToJson(String JSON_file_name,String playid, String play_name, String image,String caption, String authors, String authors_info){
		FileInputStream is=null;
		String JSONString;
		JSONObject jsonObj;
		JSONObject newPlay;
		try {
			newPlay = new JSONObject();
			newPlay.put("playid",playid);
			newPlay.put("playname", play_name);
			newPlay.put("imageurl", image);
			newPlay.put("author", authors);
			
			
			is = new FileInputStream(JSON_file_name);
			JSONString = CsvToSqliteImport.convertStreamToString(is);
			jsonObj = new JSONObject(JSONString);	
			PlaysData_Array=jsonObj.getJSONArray(PLAYS);
			PlaysData_Array.put(newPlay);
			jsonObj.remove(PLAYS);
			jsonObj.put(PLAYS, PlaysData_Array);
			JSONString = jsonObj.toString();
			FileOutputStream fooStream = new FileOutputStream(JSON_file_name, false); // true to append
            // false to overwrite.
			byte[] myBytes = JSONString.getBytes(); 
			fooStream.write(myBytes);
			fooStream.close();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
		
		
		
		
		
		
	}
	
}
