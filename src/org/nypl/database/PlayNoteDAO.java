package org.nypl.database;

import java.util.ArrayList;

import org.nypl.LibrettoContentProvider;
import org.nypl.dataholder.AnchorBean;
import org.nypl.dataholder.PlayNoteBean;
import org.nypl.dataholder.PlaysBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class PlayNoteDAO {

	public static final String TABLE_NAME = "PLAY_NOTE"; 
	private static final String COLUMN_NAME_UNIQUE_NOTE_ID = "_id"; 
	public static final String COLUMN_NAME_NOTE_ID = "NOTE_ID"; 
	private static final String COLUMN_NAME_NOTE_TEXT = "NOTE_TEXT"; 
	public static final String COLUMN_NAME_NOTE_PLAY_ID = "PLAY_ID";
	private static final String COLUMN_NAME_NOTE_VERSION_ID = "VERSION_ID";
	private static final String COLUMN_NAME_NOTE_VERSION_NAME = "VERSION_NAME";
	private static final String COLUMN_NAME_NOTE_PLAY_NAME = "PLAY_NAME";
	private static final String COLUMN_NAME_NOTE_PLAY_IMAGE = "IMAGE";
	private static final String COLUMN_NAME_NOTE_PLAY_AUTHOR_NAME = "AUTHORS";
	public static final String COLUMN_NAME_NOTE_PLAY_NOTE = "NOTES";

	private static int COLUMN_INDEX_UNIQUE_NOTE_ID = -1; 
	private static int COLUMN_INDEX_NOTE_ID = -1;
	private static int COLUMN_INDEX_NOTE_TEXT = -1;
	private static int COLUMN_INDEX_NOTE_PLAY_ID = -1;
	private static int COLUMN_INDEX_NOTE_VERSION_ID = -1;
	private static int COLUMN_INDEX_NOTE_VERSION_NAME = -1;
	private static int COLUMN_INDEX_NOTE_PLAY_AUHTOR_NAME = -1;
	private static int COLUMN_INDEX_NOTE_IMAGE = -1;
	private static int COLUMN_INDEX_NOTE_PLAY_NAME = -1;
	private static int COLUMN_INDEX_NOTE_PLAY_NOTE = -1;

	private static void setColumns(Cursor cursor){

		COLUMN_INDEX_UNIQUE_NOTE_ID = cursor.getColumnIndex(COLUMN_NAME_UNIQUE_NOTE_ID);
		COLUMN_INDEX_NOTE_ID = cursor.getColumnIndex(COLUMN_NAME_NOTE_ID);
		COLUMN_INDEX_NOTE_TEXT = cursor.getColumnIndex(COLUMN_NAME_NOTE_TEXT);
		COLUMN_INDEX_NOTE_PLAY_ID = cursor.getColumnIndex(COLUMN_NAME_NOTE_PLAY_ID);
		COLUMN_INDEX_NOTE_VERSION_ID = cursor.getColumnIndex(COLUMN_NAME_NOTE_VERSION_ID);
		COLUMN_INDEX_NOTE_VERSION_NAME = cursor.getColumnIndex(COLUMN_NAME_NOTE_VERSION_NAME);
		COLUMN_INDEX_NOTE_PLAY_AUHTOR_NAME = cursor.getColumnIndex(COLUMN_NAME_NOTE_PLAY_AUTHOR_NAME);
		COLUMN_INDEX_NOTE_IMAGE = cursor.getColumnIndex(COLUMN_NAME_NOTE_PLAY_IMAGE);
		COLUMN_INDEX_NOTE_PLAY_NAME = cursor.getColumnIndex(COLUMN_NAME_NOTE_PLAY_NAME);
		COLUMN_INDEX_NOTE_PLAY_NOTE = cursor.getColumnIndex(COLUMN_NAME_NOTE_PLAY_NOTE);



	}
	public static ArrayList<PlayNoteBean> getAllNotes(Context ctx,String search,String NoteId,String VersionId){
		ArrayList<PlayNoteBean> list = new ArrayList<PlayNoteBean>();
		Cursor cursor =ctx.getContentResolver().query(Uri.withAppendedPath(LibrettoContentProvider.CONTENT_URI,
				LibrettoContentProvider.PLAYNOTE_PATH), null, search, null, null);
		///.query(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.PLAY_PATH, null, null, null, null);
		if(cursor != null && cursor.getCount()>0){
			setColumns(cursor);
			for(int i=0;i<cursor.getCount();i++){
				cursor.moveToPosition(i);
				PlayNoteBean playnote = new PlayNoteBean();
				playnote.setPlayNoteID(cursor.getString(COLUMN_INDEX_UNIQUE_NOTE_ID));
				playnote.setNoteID(cursor.getString(COLUMN_INDEX_NOTE_ID));
				playnote.setNotes(cursor.getString(COLUMN_INDEX_NOTE_PLAY_NOTE));
				playnote.setNoteText(cursor.getString(COLUMN_INDEX_NOTE_TEXT));
				playnote.setPlayID(cursor.getString(COLUMN_INDEX_NOTE_PLAY_ID));
				playnote.setVersionID(cursor.getString(COLUMN_INDEX_NOTE_VERSION_ID));
				playnote.setVersionName(cursor.getString(COLUMN_INDEX_NOTE_VERSION_NAME));
				playnote.setAuthor(cursor.getString(COLUMN_INDEX_NOTE_PLAY_AUHTOR_NAME));
				playnote.setNotePlayImage(cursor.getString(COLUMN_INDEX_NOTE_IMAGE));
				playnote.setNotePlayName(cursor.getString(COLUMN_INDEX_NOTE_PLAY_NAME));
				list.add(playnote);
			}
		}
		cursor.close();

		return list;

	}

	public static ArrayList<PlayNoteBean> getNoteDetail(Context ctx,String NoteId){

		ArrayList<PlayNoteBean> list = new ArrayList<PlayNoteBean>();


		Cursor cursor =ctx.getContentResolver().query(Uri.parse(LibrettoContentProvider.CONTENT_URI+"/"+LibrettoContentProvider.PLAYNOTE_DETAIL_PATH), null,NoteId,null, null);
		///.query(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.PLAY_PATH, null, null, null, null);
		Log.v("in playdiao cursor",""+cursor.getCount());
		if(cursor != null && cursor.getCount()>0){

			setColumns(cursor);

			for(int i=0;i<cursor.getCount();i++){
				cursor.moveToPosition(i);

				PlayNoteBean playnote = new PlayNoteBean();
				playnote.setPlayNoteID(cursor.getString(COLUMN_INDEX_UNIQUE_NOTE_ID));
				playnote.setNoteID(cursor.getString(COLUMN_INDEX_NOTE_ID));
				playnote.setNotes(cursor.getString(COLUMN_INDEX_NOTE_PLAY_NOTE));
				playnote.setNoteText(cursor.getString(COLUMN_INDEX_NOTE_TEXT));
				playnote.setPlayID(cursor.getString(COLUMN_INDEX_NOTE_PLAY_ID));
				playnote.setVersionID(cursor.getString(COLUMN_INDEX_NOTE_VERSION_ID));
				playnote.setVersionName(cursor.getString(COLUMN_INDEX_NOTE_VERSION_NAME));
				playnote.setAuthor(cursor.getString(COLUMN_INDEX_NOTE_PLAY_AUHTOR_NAME));
				playnote.setNotePlayImage(cursor.getString(COLUMN_INDEX_NOTE_IMAGE));
				playnote.setNotePlayName(cursor.getString(COLUMN_INDEX_NOTE_PLAY_NAME));
				list.add(playnote);


				list.add(playnote);
			}

		}
		cursor.close();

		return list;
	}

	public static void saveNotes(Context ctx,String Noteid,String Notetext,String playid,String versionid,String VersionName,String NoteText){
		SqliteDBHelper dbHelper = new SqliteDBHelper(ctx);
		SQLiteDatabase db =dbHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put(COLUMN_NAME_NOTE_ID, Noteid);
		values.put(COLUMN_NAME_NOTE_TEXT, Notetext);
		values.put(COLUMN_NAME_NOTE_PLAY_ID, playid);
		values.put(COLUMN_NAME_NOTE_VERSION_ID, versionid);
		values.put(COLUMN_NAME_NOTE_VERSION_NAME, VersionName);
		values.put(COLUMN_NAME_NOTE_PLAY_NOTE, NoteText);
		db.insert(TABLE_NAME, null, values);
		Toast.makeText(ctx, "Note saved successfully.", Toast.LENGTH_LONG).show();
		db.close();
		dbHelper.close();
	}
}
