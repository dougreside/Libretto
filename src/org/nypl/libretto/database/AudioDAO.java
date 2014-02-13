package org.nypl.libretto.database;

import java.util.ArrayList;

import org.nypl.libretto.LibrettoContentProvider;
import org.nypl.libretto.dataholder.AudioBean;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class AudioDAO {
	public static final String TABLE_NAME = "AUDIO"; 
	public static final String COLUMN_NAME_AUDIO_ID = "_id"; 
	public static final String COLUMN_NAME_AUDIO_CLIP_ID = "CLIP_ID"; 
	public static final String COLUMN_NAME_AUDIO_PLAY_ID = "PLAY_ID"; 
	public static final String COLUMN_NAME_AUDIO_VERSION_ID = "VERSION_ID"; 
	public static final String COLUMN_NAME_AUDIO_CLIP_FROM = "CLIP_FROM"; 
	public static final String COLUMN_NAME_AUDIO_CLIP_TO = "CLIP_TO"; 
	public static final String COLUMN_NAME_AUDIO_PATH = "AUDIO_PATH"; 
	
	private static int COLUMN_INDEX_AUDIO_CLIP_ID = -1;
	private static int COLUMN_INDEX_AUDIO_VERSION_ID = -1;
	private static int COLUMN_INDEX_AUDIO_CLIP_FROM = -1;
	private static int COLUMN_INDEX_AUDIO_CLIP_TO = -1;
	private static int COLUMN_INDEX_AUDIO_PATH = -1;
	private static void setColumns(Cursor cursor){

		COLUMN_INDEX_AUDIO_CLIP_ID = cursor.getColumnIndex(COLUMN_NAME_AUDIO_CLIP_ID);
		COLUMN_INDEX_AUDIO_VERSION_ID = cursor.getColumnIndex(COLUMN_NAME_AUDIO_VERSION_ID);
		COLUMN_INDEX_AUDIO_CLIP_FROM = cursor.getColumnIndex(COLUMN_NAME_AUDIO_CLIP_FROM);
		COLUMN_INDEX_AUDIO_CLIP_TO = cursor.getColumnIndex(COLUMN_NAME_AUDIO_CLIP_TO);
		COLUMN_INDEX_AUDIO_PATH = cursor.getColumnIndex(COLUMN_NAME_AUDIO_PATH);
		
	}
	public static ArrayList<AudioBean> getAudioData(Context ctx,String Audio_clipId, String mPlaysId){
		ArrayList<AudioBean> list = new ArrayList<AudioBean>();
		String selectionargs = COLUMN_NAME_AUDIO_CLIP_ID +" =\""+Audio_clipId+"\" "+ " and "+" "+ COLUMN_NAME_AUDIO_PLAY_ID +" = \""+mPlaysId+"\"";
		Cursor cursor =ctx.getContentResolver().query(Uri.withAppendedPath(LibrettoContentProvider.CONTENT_URI,
				LibrettoContentProvider.AUDIO_PATH), null,  selectionargs , null, null);
		///.query(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.PLAY_PATH, null, null, null, null);
		if(cursor != null && cursor.getCount()>0){
			setColumns(cursor);
			for(int i=0;i<cursor.getCount();i++){
				cursor.moveToPosition(i);
				AudioBean audio = new AudioBean();
				audio.setClipID(cursor.getString(COLUMN_INDEX_AUDIO_CLIP_ID));
				audio.setClipVesrsionId(cursor.getString(COLUMN_INDEX_AUDIO_VERSION_ID));
				audio.setClipFrom(cursor.getString(COLUMN_INDEX_AUDIO_CLIP_FROM));
				audio.setClipTo(cursor.getString(COLUMN_INDEX_AUDIO_CLIP_TO));
				audio.setAudioPath(cursor.getString(COLUMN_INDEX_AUDIO_PATH));
				list.add(audio);
			}
		}
		cursor.close();

		return list;

	}
}
