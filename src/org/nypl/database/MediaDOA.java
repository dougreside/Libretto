package org.nypl.database;

import java.util.ArrayList;

import org.nypl.MoverContentProvider;
import org.nypl.dataholder.AnchorBean;
import org.nypl.dataholder.MediaBean;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class MediaDOA {
	public static final String TABLE_NAME = "MEDIA"; 
	private static final String COLUMN_MEDIA_ID = "_id"; 
	private static final String COLUMN_MEDIA_NAME = "MEDIA_NAME"; 
	
	private static int COLUMN_INDEX_MEDIA_ID = -1; 
	private static int COLUMN_INDEX_MEDIA_NAME = -1;
	private static void setColumns(Cursor cursor){

		COLUMN_INDEX_MEDIA_ID = cursor.getColumnIndex(COLUMN_MEDIA_ID);
		COLUMN_INDEX_MEDIA_NAME = cursor.getColumnIndex(COLUMN_MEDIA_NAME);
		
	}
	public static ArrayList<MediaBean> getVersions(Context ctx,String PlayId,String htmlId){
		ArrayList<MediaBean> list = new ArrayList<MediaBean>();
	Cursor cursor =ctx.getContentResolver().query(Uri.withAppendedPath(MoverContentProvider.CONTENT_URI,
			MoverContentProvider.MEDIA_PATH), null, null, null, null);
	///.query(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.PLAY_PATH, null, null, null, null);
		if(cursor != null && cursor.getCount()>0){
			setColumns(cursor);
			for(int i=0;i<cursor.getCount();i++){
				cursor.moveToPosition(i);
				MediaBean media = new MediaBean();
				media.setMediaID(cursor.getColumnName(COLUMN_INDEX_MEDIA_ID));
				media.setMediaName(cursor.getColumnName(COLUMN_INDEX_MEDIA_NAME));
				
				list.add(media);
			}
		}
		cursor.close();
		
		return list;
		
	}
}
