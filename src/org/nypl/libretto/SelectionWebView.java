package org.nypl.libretto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.nypl.libretto.R;
import org.nypl.libretto.adapter.ViewPagerAdapter;
import org.nypl.libretto.database.PlayNoteDAO;
import org.nypl.libretto.drag.DragController;
import org.nypl.libretto.drag.DragLayer;
import org.nypl.libretto.drag.DragListener;
import org.nypl.libretto.drag.DragSource;
import org.nypl.libretto.drag.MyAbsoluteLayout;
import org.nypl.libretto.popup.ActionItem;
import org.nypl.libretto.popup.QuickAction;
import org.nypl.libretto.popup.QuickAction.OnDismissListener;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.Region;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


public class SelectionWebView extends WebView implements TextSelectionJavascriptInterfaceListener, 
OnTouchListener, OnLongClickListener, OnDismissListener, DragListener {
	private ProgressDialog pd;
	private String mNoteId;
	private String textString;
	public File xml_file_path;
	protected	Context	ctx;
	private QuickAction mContextMenu;
	private DragLayer mSelectionDragLayer;
	
	private File FilePath = Environment.getExternalStorageDirectory();
	public static String CONTENT_LOCATION ;
	

	/** The drag controller for selection. */
	private DragController mDragController;

	/** The start selection handle. */
	private ImageView mStartSelectionHandle;

	/** the end selection handle. */
	private ImageView mEndSelectionHandle;

	/** The selection bounds. */
	private Rect mSelectionBounds = null;

	/** The previously selected region. */
	protected Region lastSelectedRegion = null;

	/** The selected range. */
	protected String selectedRange = "";

	/** The selected text. */
	protected String selectedText = "";
	
	/** Javascript interface for catching text selection. */
	protected TextSelectionJavascriptInterface textSelectionJSInterface = null;

	/** Selection mode flag. */
	protected boolean inSelectionMode = false;

	/** Flag to stop from showing context menu twice. */
	protected boolean contextMenuVisible = false;

	/** The current content width. */
	protected int contentWidth = 0;


	/** Identifier for the selection start handle. */
	private final int SELECTION_START_HANDLE = 0;

	/** Identifier for the selection end handle. */
	private final int SELECTION_END_HANDLE = 1;

	/** Last touched selection handle. */
	private int mLastTouchedSelectionHandle = -1;



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
	//*		Touch Listeners
	//*
	//*****************************************************

	private boolean mScrolling = false;
	private float mScrollDiffY = 0;
	private float mLastTouchY = 0;
	private float mScrollDiffX = 0;
	private float mLastTouchX = 0;

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		float xPoint = getDensityIndependentValue(event.getX(), ctx) / getDensityIndependentValue(this.getScale(), ctx);
		float yPoint = getDensityIndependentValue(event.getY(), ctx) / getDensityIndependentValue(this.getScale(), ctx);

		// TODO: Need to update this to use this.getScale() as a factor.

		if(event.getAction() == MotionEvent.ACTION_DOWN){

			String startTouchUrl = String.format("javascript:android.selection.startTouch(%f, %f);", 
					xPoint, yPoint);

			mLastTouchX = xPoint;
			mLastTouchY = yPoint;

			this.loadUrl(startTouchUrl);

		
		}

		else if(event.getAction() == MotionEvent.ACTION_UP){
			// Check for scrolling flag
			if(!mScrolling){
				this.endSelectionMode();
			}

			mScrollDiffX = 0;
			mScrollDiffY = 0;
			mScrolling = false;

		}
		else if(event.getAction() == MotionEvent.ACTION_MOVE){

			mScrollDiffX += (xPoint - mLastTouchX);
			mScrollDiffY += (yPoint - mLastTouchY);

			mLastTouchX = xPoint;
			mLastTouchY = yPoint;


			// Only account for legitimate movement.
			if(Math.abs(mScrollDiffX) > 10 || Math.abs(mScrollDiffY) > 10){
				mScrolling = true;

			}


		}

		// If this is in selection mode, then nothing else should handle this touch
		return false;
	}


	@Override 
	public boolean onLongClick(View v){

		// Tell the javascript to handle this if not in selection mode
		//if(!this.isInSelectionMode()){
		this.loadUrl("javascript:android.selection.longTouch();");
		mScrolling = true;
		//}


		// Don't let the webview handle it
		return true;
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


		// On Touch Listener
		this.setOnLongClickListener(this);
		this.setOnTouchListener(this);


		// Webview setup
		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		this.getSettings().setPluginsEnabled(true);

		// Javascript interfaces
		this.textSelectionJSInterface = new TextSelectionJavascriptInterface(context, this);		
		this.addJavascriptInterface(this.textSelectionJSInterface, this.textSelectionJSInterface.getInterfaceName());


		// Create the selection handles
		createSelectionLayer(context);


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

	/**
	 * Creates the selection layer.
	 * 
	 * @param context
	 */
	protected void createSelectionLayer(Context context){

		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mSelectionDragLayer = (DragLayer) inflater.inflate(R.layout.selection_drag_layer, null);


		// Make sure it's filling parent
		this.mDragController = new DragController(context);
		this.mDragController.setDragListener(this);
		this.mDragController.addDropTarget(mSelectionDragLayer);
		this.mSelectionDragLayer.setDragController(mDragController);


		this.mStartSelectionHandle = (ImageView) this.mSelectionDragLayer.findViewById(R.id.startHandle);
		this.mStartSelectionHandle.setTag(new Integer(SELECTION_START_HANDLE));
		this.mEndSelectionHandle = (ImageView) this.mSelectionDragLayer.findViewById(R.id.endHandle);
		this.mEndSelectionHandle.setTag(new Integer(SELECTION_END_HANDLE));

		OnTouchListener handleTouchListener = new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				boolean handledHere = false;

				final int action = event.getAction();

				// Down event starts drag for handle.
				if (action == MotionEvent.ACTION_DOWN) {
					handledHere = startDrag (v);
					mLastTouchedSelectionHandle = (Integer) v.getTag();
				}

				return handledHere;


			}


		};

		this.mStartSelectionHandle.setOnTouchListener(handleTouchListener);
		this.mEndSelectionHandle.setOnTouchListener(handleTouchListener);


	}

	/**
	 * Starts selection mode on the UI thread
	 */
	private Handler startSelectionModeHandler = new Handler(){

		public void handleMessage(Message m){

			if(mSelectionBounds == null)
				return;

			addView(mSelectionDragLayer);

			drawSelectionHandles();


			int contentHeight = (int) Math.ceil(getDensityDependentValue(getContentHeight(), ctx));

			// Update Layout Params
			ViewGroup.LayoutParams layerParams = mSelectionDragLayer.getLayoutParams();
			layerParams.height = contentHeight;
			layerParams.width = contentWidth;
			mSelectionDragLayer.setLayoutParams(layerParams);

		}

	};
	/**
	 * Starts selection mode.
	 * 
	 * @param	selectionBounds
	 */
	public void startSelectionMode(){

		this.startSelectionModeHandler.sendEmptyMessage(0);

	}
	// Ends selection mode on the UI thread
	private Handler endSelectionModeHandler = new Handler(){
		public void handleMessage(Message m){

			removeView(mSelectionDragLayer);
			if(getParent() != null && mContextMenu != null && contextMenuVisible){
				// This will throw an error if the webview is being redrawn.
				// No error handling needed, just need to stop the crash.
				try{
					mContextMenu.dismiss();
				}
				catch(Exception e){

				}
			}
			mSelectionBounds = null;
			mLastTouchedSelectionHandle = -1;
			loadUrl("javascript: android.selection.clearSelection();");

		}
	};

	/**
	 * Ends selection mode.
	 */
	public void endSelectionMode(){

		this.endSelectionModeHandler.sendEmptyMessage(0);

	}

	/**
	 * Calls the handler for drawing the selection handles.
	 */
	private void drawSelectionHandles(){
		this.drawSelectionHandlesHandler.sendEmptyMessage(0);
	}

	/**
	 * Handler for drawing the selection handles on the UI thread.
	 */

	private Handler drawSelectionHandlesHandler = new Handler(){
		public void handleMessage(Message m){

			MyAbsoluteLayout.LayoutParams startParams = (org.nypl.libretto.drag.MyAbsoluteLayout.LayoutParams) mStartSelectionHandle.getLayoutParams();
			startParams.x = (int) (mSelectionBounds.left - mStartSelectionHandle.getDrawable().getIntrinsicWidth());
			startParams.y = (int) (mSelectionBounds.top - mStartSelectionHandle.getDrawable().getIntrinsicHeight());

			// Stay on screen.
			startParams.x = (startParams.x < 0) ? 0 : startParams.x;
			startParams.y = (startParams.y < 0) ? 0 : startParams.y;

			mStartSelectionHandle.setLayoutParams(startParams);

			MyAbsoluteLayout.LayoutParams endParams = (org.nypl.libretto.drag.MyAbsoluteLayout.LayoutParams) mEndSelectionHandle.getLayoutParams();
			endParams.x = (int) mSelectionBounds.right;
			endParams.y = (int) mSelectionBounds.bottom;

			// Stay on screen
			endParams.x = (endParams.x < 0) ? 0 : endParams.x;
			endParams.y = (endParams.y < 0) ? 0 : endParams.y;

			mEndSelectionHandle.setLayoutParams(endParams);

		}
	};
	private String mText;
	private Dialog mPlayNoteDialog;
	private EditText mNoteText;
	private Button mSaveBtn;
	private Button mCancelBtn;
	public static String URLID;
	public static String HTMLFileLocation = null;

	/**
	 * Checks to see if this view is in selection mode.
	 * @return
	 */
	public boolean isInSelectionMode(){

		return this.mSelectionDragLayer.getParent() != null;


	}
	//*****************************************************
	//*
	//*		DragListener Methods
	//*
	//*****************************************************

	/**
	 * Start dragging a view.
	 *
	 */    
	private boolean startDrag (View v)
	{
		// Let the DragController initiate a drag-drop sequence.
		// I use the dragInfo to pass along the object being dragged.
		// I'm not sure how the Launcher designers do this.
		Object dragInfo = v;
		mDragController.startDrag (v, mSelectionDragLayer, dragInfo, DragController.DRAG_ACTION_MOVE);
		return true;
	}


	@Override
	public void onDragStart(DragSource source, Object info, int dragAction) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onDragEnd() {
		// TODO Auto-generated method stub

		MyAbsoluteLayout.LayoutParams startHandleParams = (MyAbsoluteLayout.LayoutParams) this.mStartSelectionHandle.getLayoutParams();
		MyAbsoluteLayout.LayoutParams endHandleParams = (MyAbsoluteLayout.LayoutParams) this.mEndSelectionHandle.getLayoutParams();

		float scale = getDensityIndependentValue(this.getScale(), ctx);

		float startX = startHandleParams.x - this.getScrollX();
		float startY = startHandleParams.y - this.getScrollY();
		float endX = endHandleParams.x - this.getScrollX();
		float endY = endHandleParams.y - this.getScrollY();

		startX = getDensityIndependentValue(startX, ctx) / scale;
		startY = getDensityIndependentValue(startY, ctx) / scale;
		endX = getDensityIndependentValue(endX, ctx) / scale;
		endY = getDensityIndependentValue(endY, ctx) / scale;


		if(mLastTouchedSelectionHandle == SELECTION_START_HANDLE && startX > 0 && startY > 0){
			String saveStartString = String.format("javascript: android.selection.setStartPos(%f, %f);", startX, startY);
			this.loadUrl(saveStartString);
		}

		if(mLastTouchedSelectionHandle == SELECTION_END_HANDLE && endX > 0 && endY > 0){
			String saveEndString = String.format("javascript: android.selection.setEndPos(%f, %f);", endX, endY);
			this.loadUrl(saveEndString);
		}

	}
	//*****************************************************
	//*
	//*		Context Menu Creation
	//*
	//*****************************************************

	/**
	 * Shows the context menu using the given region as an anchor point.
	 * @param region
	 */
	private void showContextMenu(Rect displayRect){

		// Don't show this twice
		if(this.contextMenuVisible){
			return;
		}

		// Don't use empty rect
		//if(displayRect.isEmpty()){
		if(displayRect.right <= displayRect.left){
			return;
		}



		ActionItem buttonThree = new ActionItem();

		buttonThree.setTitle("Add Note");
		buttonThree.setActionId(3);
	//	buttonThree.setIcon(getResources().getDrawable(R.drawable.menu_eraser));



		// The action menu
		mContextMenu  = new QuickAction(this.getContext());
		mContextMenu.setOnDismissListener(this);

		// Add buttons
	///	mContextMenu.addActionItem(buttonOne);

		//mContextMenu.addActionItem(buttonTwo);

		mContextMenu.addActionItem(buttonThree);



		//setup the action item click listener
		mContextMenu.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {

			@Override
			public void onItemClick(QuickAction source, int pos,
					int actionId) {
				// TODO Auto-generated method stub
				if (actionId == 1) { 
					// Do Button 1 stuff
					((Activity)ctx).runOnUiThread(new Runnable() {



						@Override
						public void run() {
							// TODO Auto-generated method stub

							//SelectionWebView.this.setWebViewClient(new MyWebview());
							SelectionWebView.this.getSettings().setJavaScriptEnabled(true);
							 ViewPagerAdapter.getAudio();
						//	SelectionWebView.this.getSettings().setJavaScriptEnabled(true);
							
						//	SelectionWebView.this.loadUrl("javascript:highlightsTextAudio();");
							///SelectionWebView.this.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
						//	SelectionWebView.this.loadUrl("javascript:window.HTMLOUT.showHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");


						}
					});
				} 
				/*else if (actionId == 2) { 
					// Do Button 2 stuff
				} */
				
				else if (actionId == 3) { 
					// Do Button 3 stuff

					((Activity)ctx).runOnUiThread(new Runnable() {



						@Override
						public void run() {
							// TODO Auto-generated method stub

							//SelectionWebView.this.setWebViewClient(new MyWebview());
							
							SelectionWebView.this.getSettings().setJavaScriptEnabled(true);
							saveNoteDetail();


						}
					});
				}

				contextMenuVisible = false;

			}

		});

		this.contextMenuVisible = true;
		mContextMenu.show(this, displayRect);
	}

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
		
		
		@SuppressWarnings("unused")  
		public void getIdAudio(String html5)  
		{ 
			
			     html6=html5;
				 mNoteId = html5.replace("audioImage:", "");
		}
		
		
		
		@SuppressWarnings("unused")  
		public void showHTML2(String html)  
		{  
			
			
			
			if(HTMLFileLocation!=null){
				HTMLFileLocation=HTMLFileLocation;
				
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
			/*
			new AlertDialog.Builder(ctx)  
			.setTitle("HTML")  
			.setMessage(html)  
			.setPositiveButton(android.R.string.ok, null)  
			.setCancelable(false)  
			.create()  
			.show(); */
			
		}
		@SuppressWarnings("unused")  
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
		
		
		@SuppressWarnings("unused")  
		public void getId(String html5)  
		{ 
			
			
			 mNoteId = html5.replace("highlight:", "");
			
			 PlayNoteDAO.saveNotes(ctx,mNoteId,SelectionWebView.this.mText,PlaysDetailActivity.PlayID,PlaysDetailActivity.VersionID,PlaysDetailActivity.VersionName,textString.trim());
		
		}
		
		
		
	
		
	} 


	public  void saveNoteDetail(){
		mPlayNoteDialog=new Dialog(ctx,R.style.FullHeightDialog);
		mPlayNoteDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		mPlayNoteDialog.setContentView(R.layout.popup_notes);
		mNoteText=(EditText) mPlayNoteDialog.findViewById(R.id.popup_note_txt);
		mSaveBtn= (Button) mPlayNoteDialog.findViewById(R.id.btn_save);
		mCancelBtn= (Button) mPlayNoteDialog.findViewById(R.id.btn_cancel);



		mSaveBtn.setOnClickListener(new OnClickListener() {
			private ImageView mPlayNote;
			
			@Override
			public void onClick(View v) {
				 textString = mNoteText.getText().toString(); 
				if (textString != null && textString.trim().length() ==0)
				{
					Toast.makeText(ctx, "Please enter note text", Toast.LENGTH_LONG).show();
				} else
				{
					pd = ProgressDialog.show(ctx, "Loading", "please wait...");
					SelectionWebView.this.loadUrl("javascript:highlightsText();");
					SelectionWebView.this.loadUrl("javascript:window.HTMLOUT.showHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
					mPlayNoteDialog.cancel();
					
					InputMethodManager inputMethodManager=(InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
					if (inputMethodManager != null){
						mPlayNoteDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
					} 
					
						
						
					SelectionWebView.this.pd.dismiss();
					Toast.makeText(ctx, "Note saved successfully.", Toast.LENGTH_LONG).show();
				}

			}
		});
		mCancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPlayNoteDialog.cancel();
			//	SelectionWebView.this.loadUrl("javascript:deletetagValue("+mNoteId+");");
			}
		});

		mPlayNoteDialog.show();
		InputMethodManager inputMethodManager=(InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null){
			mPlayNoteDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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

	//*****************************************************
	//*
	//*		Text Selection Javascript Interface Listener
	//*
	//*****************************************************


	/**
	 * Shows/updates the context menu based on the range
	 */
	public void tsjiJSError(String error){
	}


	/**
	 * The user has started dragging the selection handles.
	 */
	public void tsjiStartSelectionMode(){

		this.startSelectionMode();


	}

	/**
	 * The user has stopped dragging the selection handles.
	 */
	public void tsjiEndSelectionMode(){

		this.endSelectionMode();
	}

	/**
	 * The selection has changed
	 * @param range
	 * @param text
	 * @param handleBounds
	 * @param menuBounds
	 * @param showHighlight
	 * @param showUnHighlight
	 */
	public void tsjiSelectionChanged(String range, String text, String handleBounds, String menuBounds){
		try {
			mText=text;
			JSONObject selectionBoundsObject = new JSONObject(handleBounds);

			float scale = getDensityIndependentValue(this.getScale(), ctx);

			Rect handleRect = new Rect();
			handleRect.left = (int) (getDensityDependentValue(selectionBoundsObject.getInt("left"), getContext()) * scale);
			handleRect.top = (int) (getDensityDependentValue(selectionBoundsObject.getInt("top"), getContext()) * scale);
			handleRect.right = (int) (getDensityDependentValue(selectionBoundsObject.getInt("right"), getContext()) * scale);
			handleRect.bottom = (int) (getDensityDependentValue(selectionBoundsObject.getInt("bottom"), getContext()) * scale);

			this.mSelectionBounds = handleRect;
			this.selectedRange = range;
			this.selectedText = text;

			JSONObject menuBoundsObject = new JSONObject(menuBounds);

			Rect displayRect = new Rect();
			displayRect.left = (int) (getDensityDependentValue(menuBoundsObject.getInt("left"), getContext()) * scale);
			displayRect.top = (int) (getDensityDependentValue(menuBoundsObject.getInt("top") - 25, getContext()) * scale);
			displayRect.right = (int) (getDensityDependentValue(menuBoundsObject.getInt("right"), getContext()) * scale);
			displayRect.bottom = (int) (getDensityDependentValue(menuBoundsObject.getInt("bottom") + 25, getContext()) * scale);

			if(!this.isInSelectionMode()){
				this.startSelectionMode();
			}

			// This will send the menu rect
			this.showContextMenu(displayRect);

			drawSelectionHandles();


		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}


	/**
	 * Receives the content width for the page.
	 */
	public void tsjiSetContentWidth(float contentWidth){
		this.contentWidth = (int) this.getDensityDependentValue(contentWidth, ctx);
	}


	//*****************************************************
	//*
	//*		Density Conversion
	//*
	//*****************************************************

	/**
	 * Returns the density dependent value of the given float
	 * @param val
	 * @param ctx
	 * @return
	 */
	public float getDensityDependentValue(float val, Context ctx){

		// Get display from context
		Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		// Calculate min bound based on metrics
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);

		return val * (metrics.densityDpi / 160f);


	}

	/**
	 * Returns the density independent value of the given float
	 * @param val
	 * @param ctx
	 * @return
	 */
	public float getDensityIndependentValue(float val, Context ctx){

		// Get display from context
		Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		// Calculate min bound based on metrics
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);


		return val / (metrics.densityDpi / 160f);


	}

}
