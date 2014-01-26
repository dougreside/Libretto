package org.nypl.parsing;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.nypl.LibrettoContentProvider;
import org.nypl.database.ChaptersDAO;
import org.nypl.database.CsvReader;
import org.nypl.database.PlayDAO;
import org.nypl.database.SheetMusicDAO;
import org.nypl.database.VersionDAO;
import org.nypl.dataholder.ChaptersBean;
import org.nypl.dataholder.PlaysBean;
import org.nypl.dataholder.VersionBean;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class VersionParser {

	public static SQLiteDatabase db1;
	public static Context context1;
	public static String CONTENT_LOCATION;
	public static String ProjectFolder;

	public static String parsePlayVersion(InputStream is, SQLiteDatabase db,
			Context context, String content_location) {
		db1 = db;
		context1 = context;
		CONTENT_LOCATION = content_location;

		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			RssHandler handler = new RssHandler();
			parser.parse(is, handler);
			is.close();
			return handler.getXmlItems();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static class RssHandler extends DefaultHandler {
		private ArrayList<VersionBean> versionItemList;
		private ArrayList<PlaysBean> playsItemList;
		private ArrayList<ChaptersBean> chaptersItemList;
		private VersionBean currentVersion;
		private ChaptersBean currentChapter;
		private PlaysBean currentPlay = new PlaysBean();
		private StringBuilder builder;
		private String item_id;
		private String version_id;
		private String version_name;
		private String version_html_name;
		private String chapterID;
		private String chapterName;
		private String chapterHTML;
		private String versionName;
	//	private String version_audio_name;
		private String playidfinal;
		private Boolean IsMusic = false;

		private String songId;
		private String songPageHTML;
		private String songTitle;
		
		
		private int versionDepth = 0;
		
		private int playOrder = 0;
		public String getXmlItems() {
			return this.playidfinal;
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			super.characters(ch, start, length);

			builder.append(ch, start, length);

		}

		@Override
		public void endElement(String uri, String localName, String name)
				throws SAXException {
			super.endElement(uri, localName, name);
			System.out.println("end: " + localName);
			// if (this.currentItem != null){
			System.out.println("Do do do " + localName);
			if ((localName.equalsIgnoreCase(LI))
					&& (this.currentVersion != null)) {
				//String versionname = builder.toString().trim();
				System.out.println("*******" + version_id);
				System.out.println("*******" + versionName);
				System.out.println("*******" + version_html_name);
				System.out.println("*******" + chapterHTML);
				if (!(IsMusic)){
				// Same for all chapters
			
				currentVersion.setVersionPlayID(playidfinal);
				currentVersion.setVersionUUID(version_id);
				currentVersion.setVersionName(versionName);
				
				// first chapter
				
			
			
				currentChapter.setVersionID(version_id);
				currentChapter.setChapterMappingID(chapterID);
				currentChapter.setChapterName(chapterName);
				currentChapter.setChapterPlayOrder(playOrder+"");	
				currentChapter.setHTMLFile(chapterHTML);
				
				versionItemList.add(currentVersion);
				chaptersItemList.add(currentChapter);
				System.out.println("Inserting version");
				if (versionDepth<2){
				if (db1 != null) {
					currentChapter.setHTMLFile(version_html_name);
					System.out.println("db1 not null");
					CsvReader.insertVersionTable(db1, context1, version_id,
							playidfinal, versionName);
				} else {
					System.out.println("DB1 null but playidfinal is "
							+ playidfinal);
					ContentValues cv = new ContentValues();
					cv.put(VersionDAO.COLUMN_NAME_VERSION_UUID, version_id);
					cv.put(VersionDAO.COLUMN_NAME_VERSION_PLAY_ID, playidfinal);
					cv.put(VersionDAO.COLUMN_NAME_VERSION_NAME, versionName);
				//	cv.put(VersionDAO.COLUMN_NAME_HTML_FILE, chapterHTML);
				//	cv.put(VersionDAO.COLUMN_NAME_CHAPTER_ID, chapterID);
				//	cv.put(VersionDAO.COLUMN_NAME_CHAPTER_NAME, chapterName);
				//	cv.put(VersionDAO.COLUMN_NAME_CHAPTER_PLAYORDER, playOrder+"");
					context1.getContentResolver().insert(
							Uri.parse(LibrettoContentProvider.CONTENT_URI + "/"
									+ LibrettoContentProvider.VERSION_PATH), cv);
					

				}
				
				playOrder = 0;
				}
				else{	
					if (db1 != null) {
						System.out.println("db1 not null");
						CsvReader.insertChapterTable(db1, context1, version_id,
								 chapterHTML, chapterName, chapterID, playOrder);
					} else {
						System.out.println("DB1 null but playidfinal is "
								+ playidfinal);
						ContentValues cv = new ContentValues();
						cv.put(ChaptersDAO.COLUMN_NAME_VERSION_ID, version_id);
						cv.put(ChaptersDAO.COLUMN_NAME_HTML_FILE, chapterHTML);
						cv.put(ChaptersDAO.COLUMN_NAME_CHAPTER_NAME, chapterName);
						cv.put(ChaptersDAO.COLUMN_NAME_CHAPTER_MAPPING_ID, chapterID);
						cv.put(ChaptersDAO.COLUMN_NAME_CHAPTER_PLAYORDER, playOrder);
						
					
						context1.getContentResolver().insert(
								Uri.parse(LibrettoContentProvider.CONTENT_URI + "/"
										+ LibrettoContentProvider.CHAPTER_PATH), cv);
					playOrder++;
					}	
				}
				}
				else{
					if (db1 != null) {
						System.out.println("db1 not null");
						CsvReader.insertSheetMusicTable(db1, context1, chapterHTML,version_id,versionName,playOrder);
					} else {
					// CREATE SHEET MUSIC BEAN AND INSERT IT INTO SHEETMUSIC TABLE
					ContentValues cv = new ContentValues();
					cv.put(SheetMusicDAO.COLUMN_NAME_SHEETMUSIC_ID, version_id);
					cv.put(SheetMusicDAO.COLUMN_NAME_SHEETMUSIC_HTML, chapterHTML);
					cv.put(SheetMusicDAO.COLUMN_NAME_SHEETMUSIC_NAME, versionName);
					cv.put(SheetMusicDAO.COLUMN_NAME_SHEETMUSIC_PLAYORDER, playOrder+"");
					context1.getContentResolver().insert(
							Uri.parse(LibrettoContentProvider.CONTENT_URI + "/"
									+ LibrettoContentProvider.SHEETMUSIC_PATH), cv);
					}
					IsMusic=false;
				}
				versionDepth--;
				
			}

			else if (localName.equalsIgnoreCase(TITLE)) {
				
					currentPlay.setPlayName(builder.toString().trim());
					
			} 
			else if (localName.equalsIgnoreCase(SPAN)){
					versionName = builder.toString().trim();
					
			}
			else if (localName.equalsIgnoreCase(A)){
			
				chapterName = builder.toString().trim();
			}
					
			
			// }
			// }
			// builder.setLength(0);
		}

		@Override
		public void startDocument() throws SAXException {
			super.startDocument();
			versionItemList = new ArrayList<VersionBean>();
			chaptersItemList = new ArrayList<ChaptersBean>();
			builder = new StringBuilder();
		}

		@Override
		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException {
			super.startElement(uri, localName, name, attributes);
			Cursor cursor2;
			ProjectFolder = CONTENT_LOCATION.substring(0,
					CONTENT_LOCATION.lastIndexOf("/"));
			File PlayFolder = new File(CONTENT_LOCATION);

			if (localName.equalsIgnoreCase(NAV)) {

				if (db1 != null) {
					cursor2 = db1.rawQuery(
							"select * from PLAY where PLAY_LONG_ID=\""
									+ playidfinal + "\"", null);
				} else {
					cursor2 = context1.getContentResolver().query(
							Uri.parse(LibrettoContentProvider.CONTENT_URI + "/"
									+ LibrettoContentProvider.PLAY_PATH),
							null,
							PlayDAO.COLUMN_NAME_PLAY_LONG_ID + "=\""
									+ playidfinal + "\"", null, null);
				}

				if (cursor2.getCount() == 0) {

					if (db1 != null) {
						CsvReader.insertPlayTable(db1, context1, playidfinal,
								currentPlay.getPlayName(), ProjectFolder + File.separator+"cover.jpeg",
								currentPlay.getPlayAuthors(),
								currentPlay.getPlayUrl());
					} else {
						ContentValues cv = new ContentValues();
						cv.put(PlayDAO.COLUMN_NAME_PLAY_LONG_ID, playidfinal);
						cv.put(PlayDAO.COLUMN_NAME_PLAY_NAME,
								currentPlay.getPlayName());
						cv.put(PlayDAO.COLUMN_NAME_PLAY_IMAGE, ProjectFolder + File.separator+"cover.jpeg");
						cv.put(PlayDAO.COLUMN_NAME_PLAY_AUTHORS,
								currentPlay.getPlayAuthors());
						context1.getContentResolver()
								.insert(Uri
										.parse(LibrettoContentProvider.CONTENT_URI
												+ "/"
												+ LibrettoContentProvider.PLAY_PATH),
										cv);

					}
					System.out.println("Inserted");
					PlayJsonParser.addPlayToJson(ProjectFolder + File.separator
							+ "playjsonformat.json", playidfinal,
							currentPlay.getPlayName(), ProjectFolder + File.separator+"cover.jpeg", "",
							currentPlay.getPlayAuthors(), "");
				}
				System.out.println("IS PLAY FOLDER NAMED RIGHT?");
				if (PlayFolder.exists()) {
					System.out.println(CONTENT_LOCATION);
					System.out.println(ProjectFolder + File.separator
							+ playidfinal);
					PlayFolder.renameTo(new File(ProjectFolder + File.separator
							+ playidfinal));
				}
				this.currentVersion = new VersionBean();
				this.currentChapter = new ChaptersBean();

			} else if (localName.equalsIgnoreCase("meta")) {
				if (attributes.getValue("name")!=null){
				if (attributes.getValue("name").equalsIgnoreCase("dtb:uid")) {
					playidfinal = attributes.getValue("content");
				}
				}
			}  else if (localName.equalsIgnoreCase(LI)) {
				
				if (versionDepth<1){
					if (attributes.getValue("epub:type").equalsIgnoreCase("SheetMusic")){
						IsMusic=true;
					}
					else{
						IsMusic=false;
					}
					playOrder=1;
					if (attributes.getValue(ID)!=null){
						version_id = attributes.getValue(ID);
					}
					
				}
				else{
					if (attributes.getValue(ID)!=null){
						chapterID = attributes.getValue(ID);
						chapterID = chapterID.substring(0,chapterID.indexOf("-"));
					}
				
				}
				versionDepth++;
			} else if (localName.equalsIgnoreCase(A)) {
					builder.delete(0, builder.length());
					chapterHTML = attributes.getValue("href");
				
			} else if (localName.equalsIgnoreCase(SPAN)) {
				builder.delete(0, builder.length());
			} else if (localName.equalsIgnoreCase("TITLE")) {
				builder.delete(0, builder.length());
			}
		
		}

		private static final String NAV = "nav";
		private static final String SPAN = "span";
		private static final String LI = "li";
		private static final String OL = "ol";
		private static final String ID = "id";
		private static final String A = "a";
		private static final String TITLE = "title";

	}

}
