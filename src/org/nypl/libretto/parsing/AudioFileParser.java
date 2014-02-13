package org.nypl.libretto.parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.ListIterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.nypl.libretto.dataholder.AudioBean;




public class AudioFileParser{

	public static String convertStreamToString(InputStream is) throws Exception {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();
	    String line = null;

	    while ((line = reader.readLine()) != null) {
	        sb.append(line);
	    }

	    is.close();

	    return sb.toString();
	}
	public static ArrayList<AudioBean>  parseAudio(String htmlfile,String version_id){

		String clip_id;
		 String clip_from;
		String clip_to;
	

		AudioBean currentItem;
		
		ArrayList<AudioBean> audioItemList = new ArrayList<AudioBean>();
		File mAudioFile = new File(htmlfile);
		if(mAudioFile.exists()){

		try {

			FileInputStream is = new FileInputStream(mAudioFile);
           try{
			String iss = convertStreamToString(is);
					Document doc = Jsoup.parse(iss);
					Element dHead = doc.getElementsByTag("head").first();
					dHead.empty();
				
					//Attributes sAtts = new Attributes();
					//sAtts.put("type","text/javascript");
					
					//Element scripts = new Element(Tag.valueOf("script"), "", sAtts);

					dHead.append(" <meta http-equiv='Content-Type' content='text/html; charset=utf-8'/><script src='file:///android_asset/jquery.js' type='text/javascript'></script>");
					dHead.append(
							//"	<script src='file:///android_asset/rangy-core.js'></script>"+
				//	"<script src='file:///android_asset/android.selection.js'></script>"+
	//	"<script src='file:///android_asset/search.js'></script>"+
		"<script src='file:///android_asset/audio.js'></script>" );
	//	"<script type='text/javascript' src='file:///android_asset/searchwebview.js'></script>");
					dHead.append("<script type='text/javascript' src='file:///android_asset/anchors.js'></script>");
					dHead.append("<link type='text/css' href='file:///android_asset/pagestyle.css' rel='stylesheet'/>");
					Elements auds = doc.select("audio");
					Elements sheetmusics = doc.select("div[class=sheetmusic]");
					

					if (auds.size()>0){
					ListIterator<Element> eli = auds.listIterator();
					
					//Element el = auds.first();
					Element el;
					while (eli.hasNext()){
						el = (Element)eli.next();
						clip_id = el.attr("id").toString();
						clip_from = el.attr("starttime").toString();
						clip_to = el.attr("endtime").toString();
						if (clip_from.trim().length()<1){
							clip_from="0";
						}
						if (clip_to.trim().length()<1){
							clip_to="0";
						}
						currentItem = new AudioBean();
						currentItem.setClipID(clip_id);
						currentItem.setClipFrom(clip_from);
						currentItem.setClipTo(clip_to);
						currentItem.setClipVesrsionId(version_id);
						
						
						String aPath = el.attr("src").toString();
						aPath = htmlfile.substring(0,htmlfile.lastIndexOf("/"))+File.separator+aPath;
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
						//}
					}
					}
					else{
						
					}

		
					if (sheetmusics.size()>0){
					ListIterator<Element> eli = sheetmusics.listIterator();
					
					//Element el = auds.first();
					Element el;
					while (eli.hasNext()){
						el = (Element)eli.next();
						el.prepend("<span class='musicicon'>&#xf001;</span> ");

					}
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


	}
		catch (Exception e){
			e.printStackTrace();
		}
	}
		return audioItemList;
	}}

		/*SAXParserFactory factory = SAXParserFactory.newInstance();
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

	public  static class RssHandler extends DefaultHandler{
		private ArrayList<AudioBean> audioItemList;
		private AudioBean currentItem;
		private StringBuilder builder;
		private String clip_id;
		private String clip_from;
		private String clip_to;
		private String version_html_name;
		private String audio_clip_version_id;
		

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

		private static final  String AUDIO = "audioclipslist";
		private static final  String AUDIO_CLIP = "clip";
		private static final  String AUDIO_CLIP_ID = "id";
		private static final  String AUDIO_CLIP_FROM = "from";
		private static final  String AUDIO_CLIP_TO = "to";
		private static final  String AUDIO_CLIP_VERSION_ID  = "versionid";
		*/
	
	

