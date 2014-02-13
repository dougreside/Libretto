package org.nypl.libretto.database;

import java.util.ArrayList;

import org.nypl.libretto.LibrettoContentProvider;
import org.nypl.libretto.dataholder.ChaptersBean;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class ChaptersDAO {
	public static final String TABLE_NAME = "CHAPTERS"; 
	public static final String COLUMN_NAME_ID = "_id"; 
	public static final String COLUMN_NAME_VERSION_ID = "CHAPTER_VERSION_ID"; 
	public static final String COLUMN_NAME_HTML_FILE = "HTML_FILE"; 
	public static final String COLUMN_NAME_CHAPTER_NAME = "CHAPTER_NAME"; 
	public static final String COLUMN_NAME_CHAPTER_MAPPING_ID = "CHAPTER_MAPPING_ID";
	public static final String COLUMN_NAME_CHAPTER_PLAYORDER = "CHAPTER_PLAYORDER"; 

	

	
	private static int COLUMN_INDEX_CHAPTER_ID = -1; 
	private static int COLUMN_INDEX_VERSION_ID = -1;
	private static int COLUMN_INDEX_CHAPTER_MAPPING_ID = -1;
	private static int COLUMN_INDEX_CHAPTER_PLAYORDER = -1;
	private static int COLUMN_INDEX_HTML_FILE = -1;
	private static int COLUMN_INDEX_CHAPTER_NAME = -1;
	


	private static void setColumns(Cursor cursor){

		COLUMN_INDEX_VERSION_ID = cursor.getColumnIndex(COLUMN_NAME_VERSION_ID);
		COLUMN_INDEX_CHAPTER_ID = cursor.getColumnIndex(COLUMN_NAME_ID);
		COLUMN_INDEX_CHAPTER_NAME = cursor.getColumnIndex(COLUMN_NAME_CHAPTER_NAME);
		
		COLUMN_INDEX_CHAPTER_MAPPING_ID = cursor.getColumnIndex(COLUMN_NAME_CHAPTER_MAPPING_ID);
		COLUMN_INDEX_CHAPTER_PLAYORDER = cursor.getColumnIndex(COLUMN_NAME_CHAPTER_PLAYORDER);
		COLUMN_INDEX_HTML_FILE = cursor.getColumnIndex(COLUMN_NAME_HTML_FILE);
	
	}
	public static ArrayList<ChaptersBean> getChaptersForVersion(Context ctx,String VersionId){
		ArrayList<ChaptersBean> list = new ArrayList<ChaptersBean>();
		Cursor cursor =ctx.getContentResolver().query(Uri.withAppendedPath(LibrettoContentProvider.CONTENT_URI,
				LibrettoContentProvider.CHAPTER_PATH), null,  COLUMN_NAME_VERSION_ID +" =\""+VersionId+"\"", null, "CHAPTER_PLAYORDER");
		///.query(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.PLAY_PATH, null, null, null, null);
		if(cursor != null && cursor.getCount()>0){
			setColumns(cursor);
			for(int i=0;i<cursor.getCount();i++){
				cursor.moveToPosition(i);
				ChaptersBean chapter = new ChaptersBean();
				chapter.setVersionID(cursor.getString(COLUMN_INDEX_VERSION_ID));
				chapter.setChapterID(cursor.getString(COLUMN_INDEX_CHAPTER_ID));
				chapter.setChapterName(cursor.getString(COLUMN_INDEX_CHAPTER_NAME));
				chapter.setHTMLFile(cursor.getString(COLUMN_INDEX_HTML_FILE));
				chapter.setChapterMappingID(cursor.getString(COLUMN_INDEX_CHAPTER_MAPPING_ID));
				chapter.setChapterPlayOrder(cursor.getString(COLUMN_INDEX_CHAPTER_PLAYORDER));
				list.add(chapter);
			}
		}
		cursor.close();

		return list;

	}






	
}
