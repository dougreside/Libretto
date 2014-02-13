package org.nypl.libretto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.nypl.libretto.R;
import org.nypl.libretto.adapter.ViewPagerAdapter;
import org.nypl.libretto.database.CsvToSqliteImport;
import org.nypl.libretto.parsing.VersionParser;
import org.nypl.libretto.utils.ZipExtracter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.View;


import com.actionbarsherlock.app.SherlockFragmentActivity;

public class PlaysAddActivity extends SherlockFragmentActivity{

	private String savedURI;
	private static Context thisContext;
	private static final int REQUEST_CODE = 1;

	private static File FilePath = Environment.getExternalStorageDirectory();
		@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.plays_add_activity);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		//((ImageView)findViewById(R.id.s_background_img)).setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.img_background, options)));
		
		
		if(ViewPagerAdapter.mp!=null){
			//ViewPagerAdapter.mp.pause();
		ViewPagerAdapter.mp.release();
		ViewPagerAdapter.mHandler.removeCallbacks(ViewPagerAdapter.mUpdateTimeTask);
		}
	}
		
		
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			if ((thisContext!=null) && (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)) {
				savedURI = data.getData().getPath();
				
				processZipFile(savedURI,thisContext);
				this.finish();
				
			}
			super.onActivityResult(requestCode, resultCode, data);
		}
		
		public static void processZipFile(String fileString,Context ctx){
			
	
		   
			String fileName = fileString.substring(fileString.lastIndexOf(File.separator)+1);
			String CONTENT_LOCATION = "Android/data/"+ctx.getPackageName()+File.separator+"contents";
				
			InputStream inputStream = null;
			try{
	         inputStream = new FileInputStream(fileString);
	             	
			 String output = FilePath.getAbsolutePath()+File.separator+CONTENT_LOCATION+File.separator+fileName;
            
			 if (output.indexOf(".epub")>0){
            	output =  output.substring(0,output.lastIndexOf("."));
             }
	         new File(output).mkdirs();
	         ZipExtracter.extract(inputStream, output+File.separator,ctx);
	         File mVersionFile = new File(output+File.separator+"nav.xhtml");

	     	
	 		try {
	 			InputStream is = new FileInputStream(mVersionFile);
	 			String playid = VersionParser.parsePlayVersion(is,null,ctx,FilePath.getAbsolutePath() + File.separator+ CONTENT_LOCATION+File.separator+fileName.substring(0,fileName.lastIndexOf(".")));
				CsvToSqliteImport.readFromCsvForAudioTable(playid,null,ctx);
	 		//	int rowUpdated=getContentResolver().insert(Uri.parse(MoverContentProvider.CONTENT_URI+"/"+MoverContentProvider.PLAY_PATH), cv, AudioDAO.COLUMN_NAME_AUDIO_VERSION_ID + "=\"" + Version +"\" and "+ AudioDAO.COLUMN_NAME_AUDIO_PLAY_ID + "=\"" + mPlaysId+"\"" , null);
				
	 			//VersionBean playVersionData =VersionParser.parsePlayVersion(is,thisContext.,thisContext,CONTENT_LOCATION);
	 			
	 			//HomeActivity.refreshCarousel();
	 		
	 			
	 		} catch (FileNotFoundException e) {
	 			// TODO Auto-generated catch block

	 			e.printStackTrace();
	 		}
			}
			catch (Exception e){
			}
		}
		public boolean isWhitespace(String str) {
			if (str == null) {
				return false;
			}
			int sz = str.length();
			for (int i = 0; i < sz; i++) {
				if ((Character.isWhitespace(str.charAt(i)) == false)) {
					return false;
				}
			}
			return true;
		}//end of isWhitespace
	@Override
	protected void onPause()
	{
		super.onPause();
		//Save state here
		if(ViewPagerAdapter.mp!=null){
			//ViewPagerAdapter.mp.pause();
		ViewPagerAdapter.mp.release();
		ViewPagerAdapter.mHandler.removeCallbacks(ViewPagerAdapter.mUpdateTimeTask);
		}
	}
	@Override
	protected void onResume()
	{
		
		if(ViewPagerAdapter.mp!=null){
			//ViewPagerAdapter.mp.pause();
		ViewPagerAdapter.mp.release();
		ViewPagerAdapter.mHandler.removeCallbacks(ViewPagerAdapter.mUpdateTimeTask);
		}
		super.onResume();
		
	}
	public void addFromDevice(View view){
		pickBook(view.getContext());
	}
	public static void pickBook(Context mContext) {
		Intent intent = new Intent();
		thisContext = mContext;
		intent.setType("*/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		((FragmentActivity) mContext).startActivityForResult(intent, REQUEST_CODE);
		
	}
}
