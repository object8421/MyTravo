package com.cobra.mytravo.helpers;

import java.io.File;
import java.io.FileOutputStream;

import com.cobra.mytravo.data.AppData;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
/**
 * 
 * @author L!ar
 * Photo Utils: including Camera and Gallery, 
 * and the file operation(copy image file to TRAVO path, compressed to .JPG)
 */
public class PhotoUtils {
	private static final String TAG = "PhotoUtils";
	//File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/Travo/Camera");  
	private static final int REQUEST_CODE_CAMERA = 1;
	private static final int REQUEST_CODE_GALLERY = 2;
	public static void takePicture(Context context){
		
		Activity mContext = (Activity)context;
		String status = Environment.getExternalStorageState();
		if(!status.equals(Environment.MEDIA_MOUNTED))
			Toast.makeText(mContext, "未检测到SD卡，请检查您的手机", Toast.LENGTH_SHORT).show();
		else{
			
			Toast.makeText(mContext, "拍照获取图片", Toast.LENGTH_SHORT).show();
			
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
	        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);  
	        mContext.startActivityForResult(intent, REQUEST_CODE_CAMERA);  
		}
		
	}
	public static void selectPicture(Context context){
		Activity mContext = (Activity)context;
		String status = Environment.getExternalStorageState();
		if(!status.equals(Environment.MEDIA_MOUNTED))
			Toast.makeText(mContext, "未检测到SD卡，请检查您的手机", Toast.LENGTH_SHORT).show();
		else{
			
			Toast.makeText(mContext, "请从图库中选取图片", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(Intent.ACTION_PICK,  
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  
            mContext.startActivityForResult(intent, REQUEST_CODE_GALLERY);  
		}
	}
	public static String getPhotoPath(String photoTimeString){
		
	    try {
	        
            File dir = new File(AppData.TRAVO_PATH);
	        if (! dir.exists()){
	        	Log.v(TAG, "dir is not exist!");
	        	dir.mkdir();
	        }	  
	        // 生成文件名	       
	        String photoPath = photoTimeString + ".jpg";
	        // 新建文件
	       
	        return photoPath;
	        
	    } catch (Exception e) {
	        e.printStackTrace();
        	Log.v(TAG, "save image fail!");
	       return null;
	    } 
	}
	public static Uri getPhotoUri(String photoPath){
		 File file = new File(AppData.TRAVO_PATH, photoPath);	       
	      return Uri.fromFile(file);
	}
	
	public static void makeDir()
	{
		String saveDir = Environment.getExternalStorageDirectory() + "/";
		
		File dir = new File(saveDir + "Travo");
		if(!dir.exists())
		{
			Log.v(TAG, "dir is not exist!");
			Log.v(TAG, String.valueOf(dir.mkdir()));
		}
	}
	
	public static  String  saveImage(String photoTimeString, Bitmap bitmap){
		FileOutputStream fileOutputStream = null;
	    try {
	        String saveDir = Environment.getExternalStorageDirectory() + "/";
	        		
	        File dir = new File(saveDir + "Travo");
	        if (! dir.exists()){
	        	Log.v(TAG, "dir is not exist!");
	        	dir.mkdir();
	        }
	        	
	        
	        // 生成文件名
	       
	        String filename = photoTimeString + ".jpg";
	        // 新建文件
	        File file = new File(dir.getAbsolutePath(), filename);
	        // 打开文件输出流
	        fileOutputStream = new FileOutputStream(file);
	        // 生成图片文件
	        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
	        // 相片的完整路径
	        String pathString = file.getPath();	  
	        fileOutputStream.flush();
	        return pathString;
	        
	    } catch (Exception e) {
	        e.printStackTrace();
        	Log.v(TAG, "save image fail!");
	        return null;
	    } finally {
	        if (fileOutputStream != null) {
	            try {
	            	
	                fileOutputStream.close();
	                
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}
}
