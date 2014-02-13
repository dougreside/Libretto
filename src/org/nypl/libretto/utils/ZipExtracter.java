package org.nypl.libretto.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ZipExtracter {
	
	private static String TAG = "ZipExtracter";
	private static boolean DEBUG =  true;
	
	
	public static void extract(InputStream input, String destination, Context ctx) throws FileNotFoundException, IOException{
		
			
	       
	        
			ZipInputStream inputStream = new ZipInputStream(input);

			for (ZipEntry entry = inputStream.getNextEntry(); entry != null; entry = inputStream.getNextEntry())
			{
				String innerFileName = destination +  entry.getName();
				File innerFile = new File(innerFileName);

				if (innerFile.exists()){
					innerFile.delete();
				}
				if (entry.isDirectory()){
//					if(DEBUG)Log.i(TAG, "CREATING FOLDER : " + innerFile.toString());
//					innerFile.mkdirs();
				}else
				{
				
					
					File file = new File(innerFile.getParent());
					
					if(!file.exists()){
						file.mkdirs();
						if(DEBUG)Log.i(TAG, "CREATING FOLDER : " + innerFile.toString());
					}
					if(DEBUG)Log.i(TAG, "EXTRACTING FILE : " + innerFile.toString().replace("HTMLContent",""));
					//+innerFile.getParent());
			
					FileOutputStream outputStream = new FileOutputStream(innerFileName.replace("HTMLContent",""));
					final int BUFFER = 4096;

					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream,
							BUFFER);

					int count = 0;
					byte[] data = new byte[BUFFER];
					while ((count = inputStream.read(data, 0, BUFFER)) != -1)
					{
						bufferedOutputStream.write(data, 0, count);
					}
					bufferedOutputStream.flush();
					bufferedOutputStream.close();
					outputStream.close();
				}

				inputStream.closeEntry();
			}
			inputStream.close();
			
			if(DEBUG)Log.i(TAG, "ZIP EXTRACTION SUCCESSFULLY DONE");
			
		
	}
}