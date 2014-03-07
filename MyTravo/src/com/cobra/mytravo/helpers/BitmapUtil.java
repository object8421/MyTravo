package com.cobra.mytravo.helpers;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;

public class BitmapUtil {

	/**
	 * drawable转bitmap
	 * @param d
	 * @return
	 */
	public static Bitmap drawable2Bitmap(Drawable d) {
		Bitmap bitmap = null;
		try {
			BitmapDrawable bd = (BitmapDrawable)d;
			bitmap = bd.getBitmap();
		} catch (Exception e) {
			bitmap = null;
		}
		return bitmap;
	}

	/**
	 * bitmap转byte[]
	 * @param bitmap
	 * @param format
	 * @return
	 */
	public static byte[] bitmap2Bytes(Bitmap bitmap, Bitmap.CompressFormat format){
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(format, 100, baos);
			return baos.toByteArray();
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * bitmap转inputStream
	 * @param bitmap
	 * @param format
	 * @return
	 */
	public static InputStream bitmap2InputStream(Bitmap bitmap, Bitmap.CompressFormat format) {
        try {
        	return new ByteArrayInputStream(bitmap2Bytes(bitmap, format));
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 图片圆角
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	public static Bitmap getRoundBitmap(Bitmap bitmap, int pixels) {  
		if(bitmap == null) {
			return null;
		}
		
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);  
        Canvas canvas = new Canvas(output);  
        
        final int color = 0xffffffff;  
        final Paint paint = new Paint();  
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
        final RectF rectF = new RectF(rect);  
        final float roundPx = pixels;  
  
        paint.setAntiAlias(true);  
        canvas.drawARGB(0, 0, 0, 0);  
        paint.setColor(color);  
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);  
  
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
        canvas.drawBitmap(bitmap, rect, rect, paint);  
        return output;  
    }
	
	/**
	 * recycle bitmap
	 * @param bitmap
	 */
	public static void recycleBitmap(Bitmap bitmap) {
		if(bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
		}
	}
	
	/**
	 * 按给定的宽高缩放图片，如果宽高中有一个值为0，则按照另一个值进行等比例缩放
	 * @param filePath 图片路径
	 * @param width 
	 * @param height
	 * @return
	 */
	public static Bitmap createScaleBitmap(String filePath, int width, int height) {
		if(width <= 0 && height <= 0) {
			return BitmapFactory.decodeFile(filePath);
		}
		
		Options bitmapOptions = new Options();
		bitmapOptions.inJustDecodeBounds = true;// 设置这个后，再调用decode...方法 只取得outHeight(图片原始高度)和outWidth(图片的原始宽度)而不加载图片即返回的bitmap为null
		BitmapFactory.decodeFile(filePath, bitmapOptions);
		
		if(width == 0 || height == 0) {
			if(height == 0) {
				height = width * bitmapOptions.outHeight / bitmapOptions.outWidth;
			} else if(width == 0) {
				width = height * bitmapOptions.outWidth / bitmapOptions.outHeight;
			}
		}
		
		Options options = new Options();
		int widthSample = bitmapOptions.outWidth / width;
		int heightSample = bitmapOptions.outHeight / height;
		options.inSampleSize = widthSample <= heightSample ? widthSample : heightSample;

		Bitmap src = BitmapFactory.decodeFile(filePath, options);
		if(src == null) {
			return null;
		}
		Bitmap dst = Bitmap.createScaledBitmap(src, width, height, true);
		recycleBitmap(src);
		return dst;
	}
	
	/**
	 * 获取压缩图片：如果图片原宽小于指定的宽度，则使用原图，否则等比例压缩。
	 * @param filePath
	 * @param width
	 * @return
	 */
	public static Bitmap createScaleBitmapByWidthIfNeed(String filePath, int width) {
		if(width <= 0) {
			return BitmapFactory.decodeFile(filePath);
		}
		
		Options bitmapOptions = new Options();
		bitmapOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, bitmapOptions);

		Options options = new Options();
		bitmapOptions.inSampleSize = bitmapOptions.outWidth / width;

		Bitmap src = BitmapFactory.decodeFile(filePath, options);
		if(bitmapOptions.outWidth <= width) {
			return src;
		} else {
			int height = width * bitmapOptions.outHeight / bitmapOptions.outWidth;
			Bitmap dst = Bitmap.createScaledBitmap(src, width, height, true);
			recycleBitmap(src);
			return dst;
		}
	}
	
	/**
	 * 获取压缩图片
	 * <br>如果图片原宽高均小于指定的宽高，则使用原图；
	 * <br>如果图片原宽高其中一个大于指定的宽高，则按此值等比例压缩；
	 * <br>如果图片原宽高都大于指定的宽高，则按比例大的值等比例压缩。
	 * @param filePath
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap createScaleBitmapIfNeed(String filePath, int width, int height) {
		if(width <= 0 && height <= 0) {
			return BitmapFactory.decodeFile(filePath);
		}
		
		Options bitmapOptions = new Options();
		bitmapOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, bitmapOptions);

		if(bitmapOptions.outWidth <= width && bitmapOptions.outHeight <= height) {
			return BitmapFactory.decodeFile(filePath);
		} else {
			if(width == 0) {
				width = height * bitmapOptions.outWidth / bitmapOptions.outHeight;
			} else if(height == 0) {
				height = width * bitmapOptions.outHeight / bitmapOptions.outWidth;
			} else if(bitmapOptions.outWidth * height >= bitmapOptions.outHeight * width) { //width sample > height sample
				height = bitmapOptions.outHeight * width / bitmapOptions.outWidth;
			} else {
				width = bitmapOptions.outWidth * height / bitmapOptions.outHeight;
			}
			
			Options options = new Options();
			options.inSampleSize = bitmapOptions.outWidth / width;
			Bitmap src = BitmapFactory.decodeFile(filePath, options);
			Bitmap dst = Bitmap.createScaledBitmap(src, width, height, true);
			recycleBitmap(src);
			return dst;
		}
	}
	
	/**
	 * 压缩图片，减少大小，并保存到临时目录中
	 * @param filePath
	 * @return
	 */
	public static String compressBitmap(String filePath) {
		if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			return null;
		}
		
		Options bitmapOptions = new Options();
		bitmapOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, bitmapOptions);

		int width = bitmapOptions.outWidth;
		if(bitmapOptions.outWidth >= bitmapOptions.outHeight) {
			if(bitmapOptions.outWidth > 800) {
				bitmapOptions.outWidth = 800;
			}
		} else {
			if(bitmapOptions.outWidth > 480) {
				width = 480;
			}
		}
		
		Bitmap bitmap = createScaleBitmapByWidthIfNeed(filePath, width);
		if(bitmap == null) {
			return null;
		}
		
		try {
			int quality = 100;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
			while ( baos.toByteArray().length > 100 * 1024 && quality > 0) { 
				quality -= 10;
	            baos.reset();
	            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
	        }  
			
			String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/feedback/tmp";
			FileUtils.mkdirIfNotExist(dir);
			String tmpPath = dir + "/" + UUID.randomUUID().toString() + ".jpg";
			FileUtils.saveFile(baos.toByteArray(), tmpPath);
			recycleBitmap(bitmap);
			return tmpPath;
		} catch (Throwable e) {
			//LogUtil.error("BitmapUtil", "compressBitmap", e);
		}
		return null;
	}
	
	/**
	 * 压缩图片，减少大小，并保存到临时目录中
	 * @param filePath
	 * @return
	 */
	public static String compressBitmapWithScreenSize(Context context, String filePath) {
		if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			return null;
		}
		
		int screenHeight = ScreenUtil.getScreenHeight(context);
		int screenWidth = ScreenUtil.getScreenWidth(context);
		int bigSize = screenHeight >= screenWidth ? screenHeight : screenWidth;
		int smallSize = screenHeight < screenWidth ? screenHeight : screenWidth;
		
		Options bitmapOptions = new Options();
		bitmapOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, bitmapOptions);

		int width = bitmapOptions.outWidth;
		if(bitmapOptions.outWidth >= bitmapOptions.outHeight) {
			if(bitmapOptions.outWidth > bigSize) {
				bitmapOptions.outWidth = bigSize;
			}
		} else {
			if(bitmapOptions.outWidth > smallSize) {
				width = smallSize;
			}
		}
		
		Bitmap bitmap = createScaleBitmapByWidthIfNeed(filePath, width);
		if(bitmap == null) {
			return null;
		}
		
		try {
			int quality = 100;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
			while ( baos.toByteArray().length > 100 * 1024 && quality > 0) { 
				quality -= 10;
	            baos.reset();
	            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
	        }  
			
			String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/feedback/tmp";
			FileUtils.mkdirIfNotExist(dir);
			String tmpPath = dir + "/" + UUID.randomUUID().toString() + ".jpg";
			FileUtils.saveFile(baos.toByteArray(), tmpPath);
			recycleBitmap(bitmap);
			return tmpPath;
		} catch (Throwable e) {
			//LogUtil.error("BitmapUtil", "compressBitmap", e);
		}
		return null;
	}
}
