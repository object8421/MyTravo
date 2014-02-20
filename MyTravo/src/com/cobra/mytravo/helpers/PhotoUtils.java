package com.cobra.mytravo.helpers;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

public class PhotoUtils {
	File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/Travo/Camera");  
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
//	        intent.setType("image/*");  
//	        intent.putExtra("crop", "true");  
//	        intent.putExtra("aspectX", 1);  
//	        intent.putExtra("aspectY", 1);  
//	        intent.putExtra("outputX", 80);  
//	        intent.putExtra("outputY", 80);  
//	        intent.putExtra("return-data", true);  
            mContext.startActivityForResult(intent, REQUEST_CODE_GALLERY);  
		}
	}
}
