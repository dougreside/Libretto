package org.nypl.database;


import org.nypl.LibrettoContentProvider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


public class CsvReader {

	public static void insertPlayTable(SQLiteDatabase db, Context context ,String playid, String play_name, String image, String authors, String url) {

			ContentValues values = new ContentValues();
			values.put(DatabaseTable.TABLE_PLAY_LONG_ID, playid);
			values.put(DatabaseTable.TABLE_PLAY_NAME, play_name);
			values.put(DatabaseTable.TABLE_PLAY_IMAGE, image);
			values.put(DatabaseTable.TABLE_PLAY_AUTHORS, authors);
			values.put(DatabaseTable.TABLE_PLAY_URL, url);
			db.insert(PlayDAO.TABLE_NAME , null, values);
			
	}
	public static void insertChapterTable(SQLiteDatabase db,Context context ,String html_file,String versionid,String chapter_name,String chapter_mapping,int playorder){
		if(versionid!=null )
		{	
			ContentValues values = new ContentValues();
			values.put(DatabaseTable.TABLE_CHAPTER_MAPPING_ID, chapter_mapping);
			values.put(DatabaseTable.TABLE_CHAPTER_NAME, chapter_name);
			values.put(DatabaseTable.TABLE_CHAPTER_PLAYORDER, playorder);
			values.put(DatabaseTable.TABLE_CHAPTER_HTML_FILE, html_file);
			values.put(DatabaseTable.TABLE_CHAPTER_VERSION_ID, versionid);
			db.insert(ChaptersDAO.TABLE_NAME , null, values);
		}	
	}
	public static void insertSheetMusicTable(SQLiteDatabase db,Context context ,String HTML,String SheetMusicID,String SheetMusicName,int PlayOrder){
			
			ContentValues values = new ContentValues();
			values.put(DatabaseTable.TABLE_SHEETMUSIC_ID, SheetMusicID);
			values.put(DatabaseTable.TABLE_SHEETMUSIC_NAME, SheetMusicName);
			values.put(DatabaseTable.TABLE_SHEETMUSIC_PLAYORDER, PlayOrder);
			values.put(DatabaseTable.TABLE_CHAPTER_HTML_FILE, HTML);
			

			
	}
	public static void insertVersionTable(SQLiteDatabase db, Context context ,String versionid, String play_id, String version_name) {

	
		if(play_id!=null )
		{
			ContentValues values = new ContentValues();
			values.put(DatabaseTable.TABLE_VERSION_ID, versionid);
			values.put(DatabaseTable.TABLE_VERSION_PLAY_ID, play_id);
			values.put(DatabaseTable.TABLE_VERSION_NAME, version_name);
			db.insert(VersionDAO.TABLE_NAME , null, values);
		}
		
	}
	public static void insertAnchorTable(SQLiteDatabase db, Context context ,String aplay_id, String play_version_id, String anchor_html_id) {

			ContentValues values = new ContentValues();
			values.put(DatabaseTable.TABLE_ANCHOR_PLAY_ID, aplay_id);
			values.put(DatabaseTable.TABLE_ANCHOR_VERSION_PLAY_ID, play_version_id);
			values.put(DatabaseTable.TABLE_ANCHOR_HTML_ID, anchor_html_id);
			db.insert(AnchorDOA.TABLE_NAME , null, values);

	}
	public static void insertMediaTable(SQLiteDatabase db, Context context,
		String mediaid,String media_name) {
		
		ContentValues values = new ContentValues();
		values.put(DatabaseTable.TABLE_MEDIA_ID, mediaid);
		values.put(DatabaseTable.TABLE_MEDIA_NAME, media_name);
		db.insert(MediaDOA.TABLE_NAME , null, values);
		
	}
	
	
	public static void insertAudioTable(SQLiteDatabase db, Context context,String Playid,String clipid,String clipfrom, String clipto, String clipversionid,String audioPath) {
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
		context.getContentResolver().insert(Uri.parse(LibrettoContentProvider.CONTENT_URI+"/"+LibrettoContentProvider.AUDIO_PATH), values);
	
	}
	
	
}
