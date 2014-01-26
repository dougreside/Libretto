package org.nypl.database;

import java.util.ArrayList;
import java.util.HashMap;

import org.nypl.LibrettoContentProvider;
import org.nypl.dataholder.PlaysBean;
import org.nypl.dataholder.VersionBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;


public class LibraryDAO {

	public static final String TABLE_NAME = "LIBRARY"; 

	private static final String COLUMN_NAME_LIBRARY_ID = "_id"; 
	private static final String COLUMN_NAME_LIBRARY_NAME = "LIBRARY_NAME"; 
	private static final String COLUMN_NAME_LIBRARY_URL = "LIBRARY_URL"; 
	

	private static int COLUMN_INDEX_LIBRARY_ID = -1; 
	private static int COLUMN_INDEX_LIBRARY_NAME= -1; 
	private static int COLUMN_INDEX_LIBRARY_URL = -1; 
	
	
	
	
	
	
}
