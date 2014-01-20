package org.nypl;

import org.nypl.database.AnchorDOA;
import org.nypl.database.AudioDAO;

import org.nypl.database.MediaDOA;
import org.nypl.database.PlayDAO;
import org.nypl.database.PlayNoteDAO;
import org.nypl.database.SqliteDBHelper;
import org.nypl.database.VersionDAO;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class MoverContentProvider extends ContentProvider{
	
	
	private static final int PLAYS = 10;
	private static final int VERSIONS = 12;
	private static final int ANCHORS = 14;
	private static final int VERSION_PLAY = 16;	
	private static final int PLAY_BOOKMARK= 18;	
	private static final int SET_BOOKMARK= 20;	
	private static final int VERSION_PLAY_NOTE= 22;	
	private static final int PLAY_ALLBOOKMARK= 24;	
	private static final int MEDIA= 26;	
	private static final int PLAYNOTE= 28;	
	private static final int PLAYNOTEDETAIL = 30;
	private static final int NOTES = 32;
	private static final int AUDIO = 34;

	
	
	
	private SqliteDBHelper mDbHelper;
	private static final String AUTHORITY = "com.nypl.database";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	
	public static final String PLAY_PATH = "plays";
	public static final String VERSION_PATH = "versions";
	public static final String ANCHOR_PATH = "anchors";
	public static final String VERSION_PLAY_PATH = "play_version";
	public static final String PLAY_BOOKMARK_PATH = "bookmark_version";
	public static final String SET_BOOKMARK_PATH = "set_bookmark";
	public static final String VERSION_PLAY_NOTE_PATH = "version_note";
	public static final String PLAY_ALLBOOKMARK_PATH = "bookmark";
	public static final String MEDIA_PATH = "media";
	public static final String PLAYNOTE_PATH = "notepath";
	public static final String PLAYNOTE_DETAIL_PATH = "notedetailpath";
	public static final String PLAYNOTE_NOTEID = "note";
	public static final String AUDIO_PATH = "audio";
	
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, PLAY_PATH	, PLAYS);
		sURIMatcher.addURI(AUTHORITY, VERSION_PATH, VERSIONS);
		sURIMatcher.addURI(AUTHORITY, ANCHOR_PATH, ANCHORS);
		sURIMatcher.addURI(AUTHORITY, VERSION_PLAY_PATH, VERSION_PLAY);
		sURIMatcher.addURI(AUTHORITY, PLAY_BOOKMARK_PATH, PLAY_BOOKMARK);
		sURIMatcher.addURI(AUTHORITY, SET_BOOKMARK_PATH, SET_BOOKMARK);
		sURIMatcher.addURI(AUTHORITY, VERSION_PLAY_NOTE_PATH, VERSION_PLAY_NOTE);
		sURIMatcher.addURI(AUTHORITY, PLAY_ALLBOOKMARK_PATH, PLAY_ALLBOOKMARK);
		sURIMatcher.addURI(AUTHORITY, MEDIA_PATH, MEDIA);
		sURIMatcher.addURI(AUTHORITY, PLAYNOTE_PATH, PLAYNOTE);
		sURIMatcher.addURI(AUTHORITY, PLAYNOTE_DETAIL_PATH, PLAYNOTEDETAIL);
		sURIMatcher.addURI(AUTHORITY, PLAYNOTE_NOTEID, NOTES);
		sURIMatcher.addURI(AUTHORITY, AUDIO_PATH, AUDIO);
		
	}	
	@Override
	public boolean onCreate() {

		mDbHelper= new SqliteDBHelper(getContext());
		return false;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		int uriType = sURIMatcher.match(uri);
		Cursor cursor = null;
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		switch (uriType) {
		case AUDIO:
			Log.v("selection","Audio "+selection);
			cursor = db.query(AudioDAO.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			Log.v("cursor",""+cursor);
			break;
		case PLAYS:
			Log.v("selection","Play "+selection);
			cursor = db.query(PlayDAO.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			Log.v("cursor",""+cursor);
			break;
		case VERSIONS:
			cursor = db.query(VersionDAO.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		
		case ANCHORS:
			cursor = db.query(AnchorDOA.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case VERSION_PLAY_NOTE:
			cursor = db.rawQuery("select  V.PLAY_ID,P.PLAY_NAME,V._id,V.HTML_FILE,V.VERSION_NAME from VERSION V inner join PLAY P on P._id=V.PLAY_ID   where  V._id ="+ " "+"'"+selection+"'", null);
			break;
		case VERSION_PLAY: 
			Log.v("selection",""+selection);
			if(selection!=null){
			cursor = db.rawQuery("select  V.PLAY_ID,P.PLAY_NAME,P.AUTHORS,P.IMAGE,V._id,V.HTML_FILE,V.VERSION_NAME from VERSION V inner join PLAY P on P._id=V.PLAY_ID   where V.NOTE !='' and P.PLAY_NAME like"+ " "+"'"+"%"+selection + "%"+"'", null);
			}
			else{
			cursor = db.rawQuery("select  V.PLAY_ID,P.PLAY_NAME,P.AUTHORS,P.IMAGE,V._id,V.HTML_FILE,V.VERSION_NAME from VERSION V inner join PLAY P on P._id=V.PLAY_ID   where V.NOTE !=''", null);
			}
			break;
		case MEDIA:
			cursor = db.query(MediaDOA.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case PLAYNOTE:
		//	cursor = db.query(PlayNoteDAO.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			
			Log.v("selection",""+selection);
			if(selection!=null){
			cursor = db.rawQuery("select  PN.PLAY_ID,P.PLAY_NAME,P.AUTHORS,P.IMAGE,PN._id,PN.NOTE_ID,PN.NOTE_TEXT,PN.VERSION_ID,PN.VERSION_NAME,PN.NOTES from PLAY_NOTE PN inner join PLAY P on P._id=PN.PLAY_ID   where  P.PLAY_NAME like"+ " "+"'"+"%"+selection + "%"+"'", null);
			}
			else{
			cursor = db.rawQuery("select  PN.PLAY_ID,P.PLAY_NAME,P.AUTHORS,P.IMAGE,PN._id,PN.NOTE_ID,PN.NOTE_TEXT,PN.VERSION_ID,PN.VERSION_NAME,PN.NOTES from PLAY_NOTE PN inner join PLAY P on P._id=PN.PLAY_ID   where PN.NOTES !=''", null);
			}
			
			break;
		case PLAYNOTEDETAIL:
			Log.v("selection",""+selection);
			cursor = db.rawQuery("select  PN.PLAY_ID,P.PLAY_NAME,P.AUTHORS,P.IMAGE,PN._id,PN.NOTE_ID,PN.NOTE_TEXT,PN.VERSION_ID,PN.VERSION_NAME,PN.NOTES from PLAY_NOTE PN inner join PLAY P on P._id=PN.PLAY_ID   where PN.NOTE_ID ="+ " "+"'"+selection+"'", null);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		if(cursor != null && cursor.getCount()>0){
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		}
		return cursor;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase sqlDB = mDbHelper.getWritableDatabase();
		int uriType = sURIMatcher.match(uri);
		int rowsDeleted = 0;
		switch (uriType) {
		case PLAYS:
			rowsDeleted = sqlDB.delete(PlayDAO.TABLE_NAME,  selection,null);
			//rowsDeleted = sqlDB.delete(VersionDAO.TABLE_NAME,  selection,null);
			
			break;
		case VERSIONS:
			rowsDeleted = sqlDB.delete(VersionDAO.TABLE_NAME,  selection,null);
			break;
		case NOTES:
			rowsDeleted = sqlDB.delete(PlayNoteDAO.TABLE_NAME,selection,null);
			break;
		case AUDIO:
			rowsDeleted = sqlDB.delete(AudioDAO.TABLE_NAME,selection,null);
		break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;		
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
	
		int uriType = sURIMatcher.match(uri);
		long id = 0;
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		switch (uriType) {
		case PLAYS:
			id = db.insert(PlayDAO.TABLE_NAME, null, values);
			break;
		case VERSIONS:
			id = db.insert(VersionDAO.TABLE_NAME, null, values);
			break;
		case ANCHORS:
			id = db.insert(AnchorDOA.TABLE_NAME, null, values);
			break;
		case AUDIO:
			id = db.insert(AudioDAO.TABLE_NAME, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		db.close();
		return Uri.parse(PLAY_PATH+"/"+id);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		int rowsUpdated = 0;
		
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		switch (uriType) {
		case VERSIONS:
			Log.v("db.update:::::::::::::::::::::::::::::::::",""+selection);
			rowsUpdated = db.update(VersionDAO.TABLE_NAME,values,selection,null);
			break;
		case NOTES:
			Log.v("db.update:::::::::::::::::::::::::::::::::",""+selection);
			rowsUpdated = db.update(PlayNoteDAO.TABLE_NAME,values,selection,null);
			break;
		case AUDIO:
			Log.v("db.update:::::::::::::::::::::::::::::::::",""+selection+values);
			rowsUpdated = db.update(AudioDAO.TABLE_NAME,values,selection,null);
			break;
		case PLAYS:
			Log.v("db.update:::::::::::::::::::::::::::::::::",""+selection);
			rowsUpdated = db.update(PlayDAO.TABLE_NAME,values,selection,null);
		break;	
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}
	

}
