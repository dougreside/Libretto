package org.nypl.database;

import java.util.ArrayList;

import org.nypl.LibrettoContentProvider;
import org.nypl.dataholder.SheetMusicBean;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SheetMusicDAO {
	public static final String TABLE_NAME = "SHEETMUSIC"; 
	public static final String COLUMN_NAME_ID = "_id"; 

	public static final String COLUMN_NAME_SHEETMUSIC_ID = "SHEETMUSIC_ID";
	public static final String COLUMN_NAME_SHEETMUSIC_NAME = "NAME";
	public static final String COLUMN_NAME_SHEETMUSIC_HTML = "HTML";
	public static final String COLUMN_NAME_SHEETMUSIC_PLAYORDER = "PLAYORDER";
	

	public static int COLUMN_INDEX_ID = -1;
	public static int COLUMN_INDEX_SHEETMUSIC_ID = -1;
	public static int COLUMN_INDEX_SHEETMUSIC_NAME = -1;
	public static int COLUMN_INDEX_SHEETMUSIC_HTML = -1;
	public static int COLUMN_INDEX_SHEETMUSIC_PLAYORDER = -1;
	
	


	private static void setColumns(Cursor cursor){

		 	COLUMN_INDEX_ID = cursor.getColumnIndex(COLUMN_NAME_ID);
			COLUMN_INDEX_SHEETMUSIC_ID = cursor.getColumnIndex(COLUMN_NAME_SHEETMUSIC_ID);
			COLUMN_INDEX_SHEETMUSIC_NAME = cursor.getColumnIndex(COLUMN_NAME_SHEETMUSIC_NAME);
			COLUMN_INDEX_SHEETMUSIC_HTML = cursor.getColumnIndex(COLUMN_NAME_SHEETMUSIC_HTML);
			COLUMN_INDEX_SHEETMUSIC_PLAYORDER = cursor.getColumnIndex(COLUMN_NAME_SHEETMUSIC_PLAYORDER);
			
			
	}
	public static ArrayList<SheetMusicBean> getPagesForID(Context ctx,String SheetMusicId){
		ArrayList<SheetMusicBean> list = new ArrayList<SheetMusicBean>();
			Cursor cursor =ctx.getContentResolver().query(Uri.withAppendedPath(LibrettoContentProvider.CONTENT_URI,
				LibrettoContentProvider.SHEETMUSIC_PATH), null,  COLUMN_NAME_SHEETMUSIC_ID +" =\""+SheetMusicId+"\"", null, "PLAYORDER");
		///.query(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.PLAY_PATH, null, null, null, null);
		System.out.println("SHEETMUSIC CURSOR COUNT "+cursor.getCount());
		if(cursor != null && cursor.getCount()>0){
			setColumns(cursor);
			for(int i=0;i<cursor.getCount();i++){
				cursor.moveToPosition(i);
				SheetMusicBean sheetmusic = new SheetMusicBean();
				sheetmusic.setSheetMusicID(cursor.getString(COLUMN_INDEX_SHEETMUSIC_ID));
				sheetmusic.setSheetMusicName(cursor.getString(COLUMN_INDEX_SHEETMUSIC_NAME));
				sheetmusic.setSheetMusicHTML(cursor.getString(COLUMN_INDEX_SHEETMUSIC_HTML));
				sheetmusic.setPlayOrder(cursor.getString(COLUMN_INDEX_SHEETMUSIC_PLAYORDER));
				list.add(sheetmusic);
			}
		}
		cursor.close();

		return list;

	}






	
}
