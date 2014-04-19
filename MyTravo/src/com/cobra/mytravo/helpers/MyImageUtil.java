package com.cobra.mytravo.helpers;

import java.io.IOException;
import java.net.MalformedURLException;

import com.cobra.mytravo.data.AppData;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
/**
 * 
 * @author L!ar 2014/3/1
 * Util for single bitmap for a ImageView or ImageButton
 *
 */
public class MyImageUtil {
	public static String getImageUrlString(){
		return null;
	}
	public static void setBitmap(ImageView imageView, String url){
		
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        imageView.setImageBitmap(BitmapFactory.decodeFile(url, options));
	}
	public static void setBitmap(ImageButton imageButton, String url){
		
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        imageButton.setImageBitmap(BitmapFactory.decodeFile(url, options));
	}
	public static void setBitmapResize(final Context context, final ImageView imageView, final String url){
		
        
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                
                Bitmap bitmap = (Bitmap) message.obj;
                if(bitmap != null){
                	int maxHeight = GUIUtils.dp2px(context, 550);
                	//Log.v("maxHeight", maxHeight+"");
                	int height = (int) ((float) imageView.getWidth()/bitmap.getWidth()* bitmap.getHeight());
                	Log.v("height", "imageView's width is:"+imageView.getWidth()+""  
                        	+ "bitmap's width is:" + bitmap.getWidth() + "bitmap's height is:" 
                        			+ bitmap.getHeight());
                	if (height > maxHeight) height = maxHeight;
                	imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height));
                    imageView.setImageBitmap(bitmap);
                }
                
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {
                //TODO : set imageView to a "pending" image
                Bitmap bitmap = null;
				try {
					bitmap = fetch(url);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                Message message = handler.obtainMessage(1, bitmap);
                handler.sendMessage(message);
            }
        };
        thread.start();
	}
	private static Bitmap fetch(String urlString) throws MalformedURLException, IOException {
		BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(urlString, o);
        // Find the correct scale value. It should be the power of 2.
        final int REQUIRED_SIZE = 140;
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 0;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
        scale++;
        }
        System.out.println("sclae is : " + scale);
        // decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bm = BitmapFactory.decodeFile(urlString, o2);
        Bitmap bitmap = bm;

        ExifInterface exif = new ExifInterface(urlString);

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

        Log.e("ExifInteface .........", "rotation ="+orientation);

        //exif.setAttribute(ExifInterface.ORIENTATION_ROTATE_90, 90);

        Log.e("orientation", "" + orientation);
        Matrix m = new Matrix();

        if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
            m.postRotate(180);
            //m.postScale((float) bm.getWidth(), (float) bm.getHeight());
            // if(m.preRotate(90)){
            Log.e("in orientation", "" + orientation);
            bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), m, true);
            return bitmap;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            m.postRotate(90); 
            Log.e("in orientation", "" + orientation);
            bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), m, true);
            return bitmap;
        }
        else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            m.postRotate(270);
            Log.e("in orientation", "" + orientation);
            bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),bm.getHeight(), m, true);
            return bitmap;
        } 
        return bitmap;
    
    }
}
