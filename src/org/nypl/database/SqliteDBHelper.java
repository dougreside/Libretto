package org.nypl.database;


import java.io.IOException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteDBHelper extends SQLiteOpenHelper{

	private static String DB_NAME = "Mover.db";
	private static int DB_VERSION = 1;
	private Context context;
	public SqliteDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context=context;

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		System.out.println("CREATING");
		DatabaseTable.onCreate(db);
	
			try {
			System.out.println("LET THERE BE!");	
			CsvToSqliteImport.readFromCsvForPlayTable(db,context);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
