package com.cobra.mytravo.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import android.app.Activity;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.BitmapCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
/**
 * 
 * @author L!ar 2013/3/1
 * util for bunch bitmaps of a listview
 *
 */
public class BitmapManager {
    private Context mContext;
    //private final Map<String, Bitmap> bitmapMap;
    private BitmapCache cache;
    public BitmapManager(Context context) {
       mContext = context;
        //bitmapMap = new HashMap<String, Bitmap>();
       cache = new BitmapCache();
    }
    
    public Bitmap fetchBitmap(String urlString) {
//        if (bitmapMap.containsKey(urlString)) {
//            return bitmapMap.get(urlString);
//        }
    	if(cache.getBitmap(urlString) != null){
    		return cache.getBitmap(urlString);
    	}
        Log.d(this.getClass().getSimpleName(), "image url:" + urlString);
        try {
            Bitmap bitmap = fetch(urlString);
            
            if (bitmap != null) {
            	//bitmapMap.put(urlString, bitmap);
               cache.putBitmap(urlString, bitmap);
            } else {
              Log.w(this.getClass().getSimpleName(), "could not get thumbnail");
              
            }

            return bitmap;
        } catch (MalformedURLException e) {
            Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
            return null;
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
            return null;
        }
    }

    public void fetchBitmapOnThread(final String urlString, final ImageView imageView) {

    	
    	if(cache.getBitmap(urlString) != null){
    		Bitmap bitmap = cache.getBitmap(urlString);
    		imageView.setImageBitmap(bitmap);
    	}
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                
                Bitmap bitmap = (Bitmap) message.obj;

                imageView.setImageBitmap(bitmap);
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {
                //TODO : set imageView to a "pending" image
                Bitmap bitmap = fetchBitmap(urlString);
                Message message = handler.obtainMessage(1, bitmap);
                handler.sendMessage(message);
            }
        };
        thread.start();
    }

    private Bitmap fetch(String urlString) throws MalformedURLException, IOException {
    	

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