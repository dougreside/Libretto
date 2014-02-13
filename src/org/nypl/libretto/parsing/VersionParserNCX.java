package org.nypl.libretto.parsing;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.nypl.libretto.LibrettoContentProvider;
import org.nypl.libretto.database.ChaptersDAO;
import org.nypl.libretto.database.CsvReader;
import org.nypl.libretto.database.PlayDAO;
import org.nypl.libretto.database.VersionDAO;
import org.nypl.libretto.dataholder.ChaptersBean;
import org.nypl.libretto.dataholder.PlaysBean;
import org.nypl.libretto.dataholder.VersionBean;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class VersionParserNCX {

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

		private ArrayList<ChaptersBean> chaptersItemList;
		private VersionBean currentVersion;
		private ChaptersBean currentChapter;
		private PlaysBean currentPlay = new PlaysBean();
		private StringBuilder builder;

		private String version_id;

		private String version_html_name;
		private String chapterID;
		private String chapterName;
		private String chapterHTML;
		private String versionName;
	//	private String version_audio_name;
		private String playidfinal;
		private Boolean titleActive = false;
		private Boolean authorActive = false;
		private Boolean navlabelActive = false;
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
			// if (this.currentItem != null){
			if ((localName.equalsIgnoreCase(NAVPOINT))
					&& (this.currentVersion != null)) {
				//String versionname = builder.toString().trim();
				// Same for all chapters
				
				currentVersion.setVersionPlayID(playidfinal);
				currentVersion.setVersionUUID(version_id);
				currentVersion.setVersionName(versionName);
				
				// first chapter
				
			
			
				currentChapter.setVersionID(version_id);
				currentChapter.setChapterMappingID(chapterID);
				currentChapter.setChapterName(chapterName);
				currentChapter.setChapterPlayOrder(playOrder+"");	
				
				
				versionItemList.add(currentVersion);
				chaptersItemList.add(currentChapter);
				if (versionDepth<2){
				if (db1 != null) {
					currentChapter.setHTMLFile(version_html_name);
					CsvReader.insertVersionTable(db1, context1, version_id,
							playidfinal, versionName);
				} else {
						
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
				chapterName = "Front Matter";
				chapterHTML=version_html_name;
				playOrder = 0;
				}
					
					if (db1 != null) {
						CsvReader.insertChapterTable(db1, context1, version_id,
								 chapterHTML, chapterName, chapterID, playOrder);
					} else {
							
						ContentValues cv = new ContentValues();
						cv.put(ChaptersDAO.COLUMN_NAME_VERSION_ID, version_id);
						cv.put(ChaptersDAO.COLUMN_NAME_HTML_FILE, chapterHTML);
						cv.put(ChaptersDAO.COLUMN_NAME_CHAPTER_NAME, chapterName);
						cv.put(ChaptersDAO.COLUMN_NAME_CHAPTER_MAPPING_ID, chapterID);
						cv.put(ChaptersDAO.COLUMN_NAME_CHAPTER_PLAYORDER, playOrder);
						
					
						context1.getContentResolver().insert(
								Uri.parse(LibrettoContentProvider.CONTENT_URI + "/"
										+ LibrettoContentProvider.CHAPTER_PATH), cv);

					}	
				
				versionDepth--;
			}

			else if (localName.equalsIgnoreCase("text")) {
				if (titleActive) {
					currentPlay.setPlayName(builder.toString().trim());
					titleActive = false;
				} else if (authorActive) {
					currentPlay.setPlayAuthors(builder.toString().trim());
					authorActive = false;
				}
				else if (navlabelActive){
					if (versionDepth>1){
					chapterName = builder.toString().trim();
					}
					else{
					versionName = builder.toString().trim();
					chapterName = versionName;
					}
					navlabelActive = false;
				}
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

			if (localName.equalsIgnoreCase(NAVMAP)) {

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
								currentPlay.getPlayName(), "cover.jpeg",
								currentPlay.getPlayAuthors(),
								currentPlay.getPlayUrl());
					} else {
						ContentValues cv = new ContentValues();
						cv.put(PlayDAO.COLUMN_NAME_PLAY_LONG_ID, playidfinal);
						cv.put(PlayDAO.COLUMN_NAME_PLAY_NAME,
								currentPlay.getPlayName());
						cv.put(PlayDAO.COLUMN_NAME_PLAY_IMAGE, "cover.jpeg");
						cv.put(PlayDAO.COLUMN_NAME_PLAY_AUTHORS,
								currentPlay.getPlayAuthors());
						context1.getContentResolver()
								.insert(Uri
										.parse(LibrettoContentProvider.CONTENT_URI
												+ "/"
												+ LibrettoContentProvider.PLAY_PATH),
										cv);

					}
					PlayJsonParser.addPlayToJson(ProjectFolder + File.separator
							+ "playjsonformat.json", playidfinal,
							currentPlay.getPlayName(), "cover.jpeg", "",
							currentPlay.getPlayAuthors(), "");
				}
				if (PlayFolder.exists()) {
					
					PlayFolder.renameTo(new File(ProjectFolder + File.separator
							+ playidfinal));
				}
				this.currentVersion = new VersionBean();
				this.currentChapter = new ChaptersBean();

			} else if (localName.equalsIgnoreCase("meta")) {
				if (attributes.getValue("name").equalsIgnoreCase("dtb:uid")) {
					playidfinal = attributes.getValue("content");
				}
			}  else if (localName.equalsIgnoreCase(NAVPOINT)) {
				
				if (versionDepth<1){
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
					playOrder++;
				}
				versionDepth++;
			} else if (localName.equalsIgnoreCase(CONTENT)) {
				if (versionDepth<2){
				version_html_name = attributes.getValue("src");
				}
				else{
					chapterHTML = attributes.getValue("src");
				}
			} else if (localName.equalsIgnoreCase(TEXT)) {
				builder.delete(0, builder.length());
			} else if (localName.equalsIgnoreCase("docTitle")) {
				titleActive = true;
			}
			else if (localName.equalsIgnoreCase("navlabel")) {
				navlabelActive = true;
			}
		}

		private static final String NAVMAP = "navMap";
		private static final String NAVPOINT = "navPoint";
		private static final String TEXT = "text";
		private static final String CONTENT = "content";
		private static final String ID = "id";

	}

}
