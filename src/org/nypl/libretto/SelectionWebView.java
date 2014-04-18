package org.nypl.libretto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Region;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.webkit.WebView;


@SuppressLint("SetJavaScriptEnabled")
public class SelectionWebView extends WebView  {
	@SuppressWarnings("unused")
	private String mNoteId;

	public File xml_file_path;
	protected	Context	ctx;

	private File FilePath = Environment.getExternalStorageDirectory();
	public static String CONTENT_LOCATION ;
	

	



	/** The previously selected region. */
	protected Region lastSelectedRegion = null;

	/** The selected range. */
	protected String selectedRange = "";

	/** The selected text. */
	protected String selectedText = "";
	
	/** JavaScript interface for catching text selection. */
	protected TextSelectionJavascriptInterface textSelectionJSInterface = null;

	/** Selection mode flag. */
	protected boolean inSelectionMode = false;

	/** Flag to stop from showing context menu twice. */
	protected boolean contextMenuVisible = false;

	/** The current content width. */
	protected int contentWidth = 0;






	public SelectionWebView(Context context) {
		super(context);
		SelectionWebView.this.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
		this.ctx = context;
		this.setup(context);
	}

	public SelectionWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		SelectionWebView.this.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
		this.ctx = context;
		this.setup(context);
	}


	public SelectionWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		SelectionWebView.this.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
		this.ctx = context;
		this.setup(context);
	}

	




	//*****************************************************
	//*
	//*		Setup
	//*
	//*****************************************************

	/**
	 * Setups up the web view.
	 * @param context
	 */

	protected void setup(Context context){



		// Webview setup
		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		//this.getSettings().setPluginsEnabled(true);



		// Set to the empty region
		Region region = new Region();
		region.setEmpty();
		this.lastSelectedRegion = region;
	}



	//*****************************************************
	//*
	//*		Selection Layer Handling
	//*
	//*****************************************************

	
	public static String URLID;
	public static String HTMLFileLocation = null;

	public void SetAudio(String Id,String HTMLName){
		
		if(Id.contains("external"))
		{
		Uri partialUri = Uri.parse("content://media"+Id);
		 Cursor cursor = ctx.getContentResolver().query(partialUri, new String[] { android.provider.MediaStore.Audio.AudioColumns.DATA }, null, null, null);//("content://media/external/audio/media/4743.mp3", new String[] { android.provider.MediaStore.Audio.AudioColumns.DATA }, null, null, null);
         cursor.moveToFirst();   
         URLID = cursor.getString(0);
         cursor.close();
		}else{
			URLID = Id;
		}
			
		HTMLFileLocation=HTMLName;
		SelectionWebView.this.getSettings().setJavaScriptEnabled(true);
		SelectionWebView.this.loadUrl("javascript:highlightsTextAudio();");
		
		//SelectionWebView.this.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
		SelectionWebView.this.loadUrl("javascript:window.HTMLOUT.showHTML2('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
		
		///SelectionWebView.this.loadUrl();
	}
	
	public class MyJavaScriptInterface   
	{  
		
		String html6;
		
		
		public void getIdAudio(String html5)  
		{ 
			
			     html6=html5;
				 mNoteId = html5.replace("audioImage:", "");
		}
		
		
		
		public void showHTML2(String html)  
		{  
			
			
			
			if(HTMLFileLocation!=null){
			
				
				 html=html.replace(android.os.Environment.getExternalStorageDirectory()+File.separator +PlaysDetailActivity.CONTENT_LOCATION+File.separator +"nypl_audio-", URLID);////.replace(html6, mNoteId);
				 html=html.replace("nypl_audio-", URLID);
			}else{
				HTMLFileLocation=PlaysDetailActivity.HTMLFileName;
			}
			File cacheDir;
			///cacheDir=new File(android.os.Environment.getExternalStorageDirectory(), "contents");
			cacheDir=new File(android.os.Environment.getExternalStorageDirectory(), PlaysDetailActivity.CONTENT_LOCATION+File.separator);
			///"Android/data/"+this.getPackageName()
			xml_file_path = new File(cacheDir,HTMLFileLocation);
			FileOutputStream fos;
			byte[] data = new String(html).getBytes();
			try {
				fos = new FileOutputStream(xml_file_path);
				fos.write(data);
				fos.flush();
				fos.close();
				//String filePath = "file://"+path+xml_file_path;	
				//SelectionWebView.this.loadUrl(filePath);
				
			} catch (FileNotFoundException e) {
				// handle exception
			} catch (IOException e) {
				// handle exception
			}
			//String filePath = "file://"+path+"/HTMLContent/"+HTMLFileLocation
			String filePath = "file:///"+FilePath.getAbsolutePath() + File.separator+ PlaysDetailActivity.CONTENT_LOCATION + File.separator +HTMLFileLocation;
			SelectionWebView.this.loadUrl(filePath);
		
			
		}
		public void showHTML(String html)  
		{  
		
				HTMLFileLocation=PlaysDetailActivity.HTMLFileName;
			
			File cacheDir;
			cacheDir=new File(android.os.Environment.getExternalStorageDirectory(), PlaysDetailActivity.CONTENT_LOCATION+File.separator);
			xml_file_path = new File(cacheDir,HTMLFileLocation);
			FileOutputStream fos;
			byte[] data = new String(html).getBytes();
			try {
				fos = new FileOutputStream(xml_file_path);
				fos.write(data);
				fos.flush();
				fos.close();
				//String filePath = "file://"+path+xml_file_path;	
				//SelectionWebView.this.loadUrl(filePath);
				
			} catch (FileNotFoundException e) {
				// handle exception
			} catch (IOException e) {
				// handle exception
			}
			

			
		}  
		
		
		
		
		
		
	
		
	} 


	




	//*****************************************************
	//*
	//*		OnDismiss Listener
	//*
	//*****************************************************

	/**
	 * Clears the selection when the context menu is dismissed.
	 */
	public void onDismiss(){
		//clearSelection();
		this.contextMenuVisible = false;
	}

	

}
