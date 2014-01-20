package org.nypl.parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Vector;



import org.nypl.dataholder.AudioBean;

import android.content.res.AssetManager;
import android.util.Log;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AudioFileParserSAX{
	public static String convertStreamToString(InputStream is) throws Exception {
	    try{
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();
	    String line = null;

	    while ((line = reader.readLine()) != null) {
	        sb.append(line);
	    }

	    is.close();

	    return sb.toString();}
	    catch (Exception e){
	    	return "";
	    }
	}

	private static void parseAudio(String htmlfile,String version_id){

		String clip_id;
		 String clip_from;
		String clip_to;
	
		String audio_clip_version_id;
		AudioBean currentItem;
		RssHandler handler = new RssHandler();
		ArrayList<AudioBean> audioItemList = new ArrayList<AudioBean>();
		File mAudioFile = new File(htmlfile);
		if(mAudioFile.exists()){
		System.out.println("imageFile data ::::::::::::::::::::::1111::::::::::" +mAudioFile.exists() );  


		try{
			FileInputStream is = new FileInputStream(mAudioFile);
           
			String iss = convertStreamToString(is);
					

		    SAXParserFactory factory = SAXParserFactory.newInstance();

			SAXParser parser = factory.newSAXParser();
		
			parser.parse(is, handler);
			is.close();
			//return handler.getXmlItems();
		}
		catch (Exception e){
			
		}
        
	
	}else{

	}
		}
	 public class StartTag{
		 String name;
		 int start;
		 int length;
	 }
	 public class TextParts{
		 String text;
		 int offset;
	 }
	 public static class RssHandler extends DefaultHandler{
		private ArrayList<AudioBean> audioItemList;
		private AudioBean currentItem;
		private StringBuilder builder;
		private String clip_id;
		private String clip_from;
		private String clip_to;
		private String version_html_name;
		private String audio_clip_version_id;
		private Vector<StartTag> Tags;
		private Vector<TextParts> Texts;

		public ArrayList<AudioBean> getXmlItems(){
			return this.audioItemList;
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
		if (this.currentItem != null){
				 if (localName.equalsIgnoreCase(AUDIO_CLIP)){
					currentItem.setClipID(clip_id);
					currentItem.setClipFrom(clip_from);
					currentItem.setClipTo(clip_to);
					currentItem.setClipVesrsionId(audio_clip_version_id);
					
				}
				 audioItemList.add(currentItem);
			}
			builder.setLength(0);    
		}

		@Override
		public void startDocument() throws SAXException {
			super.startDocument();
			audioItemList = new ArrayList<AudioBean>();
			builder = new StringBuilder();
		}

		@Override
		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException {
			super.startElement(uri, localName, name, attributes);
			
			if (localName.equalsIgnoreCase(AUDIO)){
				this.currentItem = new AudioBean();
				
			} 
			else if(localName.equalsIgnoreCase(AUDIO_CLIP)){
				this.currentItem = new AudioBean();
				clip_id = attributes.getValue("id");
				
				clip_from = attributes.getValue("from");
			
				clip_to = attributes.getValue("to");
				
				audio_clip_version_id = attributes.getValue("versionid");
				
			}


		}
	
		private static final  String AUDIO = "audio";
		private static final  String AUDIO_CLIP = "source";
		private static final  String AUDIO_CLIP_ID = "id";
		private static final  String AUDIO_CLIP_FROM = "starttime";
		private static final  String AUDIO_CLIP_TO = "endtime";
		private static final  String AUDIO_TYPE  = "type";
		private static final  String TEXT  = "text";
		
	
		

	
	}
}
	
	
	/*Document doc = Jsoup.parse(iss);
					Element dHead = doc.getElementsByTag("head").first();
					dHead.empty();
					dHead.append(" <meta http-equiv='Content-Type' content='text/html; charset=utf-8'/><script src='file:///android_asset/jquery.js' type='text/javascript'></script>");
					dHead.append("<script src='file:///android_asset/audio.js'></script>" );
					dHead.append("<script type='text/javascript' src='file:///android_asset/anchors.js'></script>");
					dHead.append("<link type='text/css' href='file:///android_asset/pagestyle.css' rel='stylesheet'/>");
					
					System.out.println(dHead);
					Elements auds = doc.select("audio");
					Log.v("audio","starting");
					if (auds.size()>0){
					ListIterator<Element> eli = auds.listIterator();
					
					//Element el = auds.first();
					Element el;
					while (eli.hasNext()){
						el = (Element)eli.next();
						clip_id = el.attr("id").toString();
						clip_from = el.attr("starttime").toString();
						clip_to = el.attr("endtime").toString();
						currentItem = new AudioBean();
						currentItem.setClipID(clip_id);
						currentItem.setClipFrom(clip_from);
						currentItem.setClipTo(clip_to);
						currentItem.setClipVesrsionId(version_id);
						mp3src = el.select("source[type=audio/mp3]");
						System.out.println("MP3 size: "+mp3src.size());
						if (!(mp3src.isEmpty())){
						String aPath = mp3src.first().attr("src").toString();
						aPath = htmlfile.substring(0,htmlfile.lastIndexOf("/"))+File.separator+aPath;
						System.out.println(aPath);
						currentItem.setAudioPath(aPath);
						audioItemList.add(currentItem);
						
						
						Attributes aAttributes = new Attributes();
						aAttributes.put("href", "nypl_audio-"+clip_id);

						Element aElement = new Element(Tag.valueOf("a"), "", aAttributes);
					
						el.replaceWith(aElement);
						Element iElement = aElement.appendElement("img");
					    
					    
						iElement.attr("src","file:///android_asset/play.jpg");
						iElement.attr("style","max-width: 30px");
						iElement.attr("alt","image");
						}
						Log.v("audio",el.attr("starttime").toString());
						Log.v("audio",el.attr("endtime").toString());
						
						System.out.println(el.html());
					}
					}
					else{
						
					}



FileOutputStream fooStream = new FileOutputStream(mAudioFile, false); // true to append
                                                                 // false to overwrite.
byte[] myBytes = doc.html().getBytes(); 
fooStream.write(myBytes);
fooStream.close();
					

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
		
			
System.out.println("Sending back....");


	}
		catch (Exception e){
			e.printStackTrace();
		}
	}
		return audioItemList;
	}}*/
	

