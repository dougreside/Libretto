package org.nypl.database;

import java.util.ArrayList;

import org.nypl.LibrettoContentProvider;
import org.nypl.dataholder.AnchorBean;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class AnchorDOA {
	public static final String TABLE_NAME = "ANCHOR"; 
	private static final String COLUMN_NAME_ANCHOR_ID = "_id"; 
	private static final String COLUMN_NAME_ANCHOR_PLAY_ID = "PLAY_ID"; 
	private static final String COLUMN_NAME_ANCHOR_PLAY_VERSION_ID = "PLAY_VERSION_ID"; 
	private static final String COLUMN_NAME_ANCHOR_HTML_ID = "ANCHOR_HTML_ID"; 
	
	private static int COLUMN_INDEX_ANCHOR_ID = -1; 
	private static int COLUMN_INDEX_ANCHOR_PLAY_ID = -1;
	private static int COLUMN_INDEX_ANCHOR_PLAY_VERSION_ID = -1;
	private static int COLUMN_INDEX_ANCHOR_HTML_ID = -1;
	private static void setColumns(Cursor cursor){

		COLUMN_INDEX_ANCHOR_ID = cursor.getColumnIndex(COLUMN_NAME_ANCHOR_ID);
		COLUMN_INDEX_ANCHOR_PLAY_ID = cursor.getColumnIndex(COLUMN_NAME_ANCHOR_PLAY_ID);
		COLUMN_INDEX_ANCHOR_PLAY_VERSION_ID = cursor.getColumnIndex(COLUMN_NAME_ANCHOR_PLAY_VERSION_ID);
		COLUMN_INDEX_ANCHOR_HTML_ID = cursor.getColumnIndex(COLUMN_NAME_ANCHOR_HTML_ID);
	}
	public static ArrayList<AnchorBean> getVersions(Context ctx,String PlayId,String htmlId){
		ArrayList<AnchorBean> list = new ArrayList<AnchorBean>();
	Cursor cursor =ctx.getContentResolver().query(Uri.withAppendedPath(LibrettoContentProvider.CONTENT_URI,
			LibrettoContentProvider.ANCHOR_PATH), null, null, null, null);
	///.query(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.PLAY_PATH, null, null, null, null);
		if(cursor != null && cursor.getCount()>0){
			setColumns(cursor);
			for(int i=0;i<cursor.getCount();i++){
				cursor.moveToPosition(i);
				AnchorBean anchor = new AnchorBean();
				anchor.setAnchorID(cursor.getColumnName(COLUMN_INDEX_ANCHOR_ID));
				anchor.setAnchorPlayId(cursor.getColumnName(COLUMN_INDEX_ANCHOR_PLAY_ID));
				anchor.setAnchorPlay_Version_Id(cursor.getColumnName(COLUMN_INDEX_ANCHOR_PLAY_VERSION_ID));
				anchor.setAnchorHTML_Id(cursor.getColumnName(COLUMN_INDEX_ANCHOR_HTML_ID));
				list.add(anchor);
			}
		}
		cursor.close();
		
		return list;
		
	}
}
