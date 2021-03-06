package org.nypl.libretto;

import org.nypl.libretto.database.AnchorDOA;
import org.nypl.libretto.database.AudioDAO;
import org.nypl.libretto.database.ChaptersDAO;
import org.nypl.libretto.database.PlayDAO;
import org.nypl.libretto.database.SheetMusicDAO;
import org.nypl.libretto.database.SqliteDBHelper;
import org.nypl.libretto.database.VersionDAO;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class LibrettoContentProvider extends ContentProvider{
	
	
	private static final int PLAYS = 10;
	private static final int VERSIONS = 12;
	private static final int ANCHORS = 14;
	private static final int VERSION_PLAY = 16;	
	private static final int AUDIO = 34;
	private static final int CHAPTERS = 36;
	private static final int SHEETMUSIC = 38;
	
	
	
	private SqliteDBHelper mDbHelper;
	private static final String AUTHORITY = "com.nypl.database";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	
	public static final String PLAY_PATH = "plays";
	public static final String VERSION_PATH = "versions";
	public static final String CHAPTER_PATH = "chapters";
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
	public static final String SHEETMUSIC_PATH = "sheetmusic";
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, PLAY_PATH	, PLAYS);
		sURIMatcher.addURI(AUTHORITY, VERSION_PATH, VERSIONS);
		sURIMatcher.addURI(AUTHORITY, ANCHOR_PATH, ANCHORS);
		sURIMatcher.addURI(AUTHORITY, VERSION_PLAY_PATH, VERSION_PLAY);
		sURIMatcher.addURI(AUTHORITY, AUDIO_PATH, AUDIO);
		sURIMatcher.addURI(AUTHORITY, CHAPTER_PATH, CHAPTERS);
		sURIMatcher.addURI(AUTHORITY, SHEETMUSIC_PATH, SHEETMUSIC);
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
			cursor = db.query(AudioDAO.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case PLAYS:
			cursor = db.query(PlayDAO.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case VERSIONS:
			cursor = db.query(VersionDAO.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		
		case ANCHORS:
			cursor = db.query(AnchorDOA.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		/*case VERSION_PLAY_NOTE:
			cursor = db.rawQuery("select  V.PLAY_ID,P.PLAY_NAME,V._id,V.HTML_FILE,V.VERSION_NAME from VERSION V inner join PLAY P on P._id=V.PLAY_ID   where  V._id ="+ " "+"'"+selection+"'", null);
			break;*/
		case VERSION_PLAY:
			if(selection!=null){
			cursor = db.rawQuery("select  V.PLAY_ID,P.PLAY_NAME,P.AUTHORS,P.IMAGE,V._id,V.HTML_FILE,V.VERSION_NAME from VERSION V inner join PLAY P on P._id=V.PLAY_ID   where V.NOTE !='' and P.PLAY_NAME like"+ " "+"'"+"%"+selection + "%"+"'", null);
			}
			else{
			cursor = db.rawQuery("select  V.PLAY_ID,P.PLAY_NAME,P.AUTHORS,P.IMAGE,V._id,V.HTML_FILE,V.VERSION_NAME from VERSION V inner join PLAY P on P._id=V.PLAY_ID   where V.NOTE !=''", null);
			}
			break;
		
		case CHAPTERS:
			cursor = db.query(ChaptersDAO.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			break;	
		case SHEETMUSIC:
			cursor = db.query(SheetMusicDAO.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
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
		case CHAPTERS:
			rowsDeleted = sqlDB.delete(ChaptersDAO.TABLE_NAME,  selection,null);
			break;
		case AUDIO:
			rowsDeleted = sqlDB.delete(AudioDAO.TABLE_NAME,selection,null);
		break;
		case SHEETMUSIC:
			
			rowsDeleted = sqlDB.delete(SheetMusicDAO.TABLE_NAME,selection,null);
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
		case CHAPTERS:
			id = db.insert(ChaptersDAO.TABLE_NAME, null, values);
			break;
		case SHEETMUSIC:
			id = db.insert(SheetMusicDAO.TABLE_NAME, null, values);
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
			rowsUpdated = db.update(VersionDAO.TABLE_NAME,values,selection,null);
			break;
		case AUDIO:
			rowsUpdated = db.update(AudioDAO.TABLE_NAME,values,selection,null);
			break;
		case PLAYS:
			rowsUpdated = db.update(PlayDAO.TABLE_NAME,values,selection,null);
		case CHAPTERS:
			rowsUpdated = db.update(ChaptersDAO.TABLE_NAME,values,selection,null);
		break;	
		case SHEETMUSIC:
			rowsUpdated = db.update(SheetMusicDAO.TABLE_NAME,values,selection,null);
		break;	
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}
	

}
