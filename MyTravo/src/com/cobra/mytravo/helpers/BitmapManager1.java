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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class BitmapManager1 {
    private Context mContext;
    private final Map<String, Bitmap> bitmapMap;
    public BitmapManager1(Context context) {
       mContext = context;
        bitmapMap = new HashMap<String, Bitmap>();
    }
    
    public Bitmap fetchBitmap(String urlString) {
        if (bitmapMap.containsKey(urlString)) {
            return bitmapMap.get(urlString);
        }

        Log.d(this.getClass().getSimpleName(), "image url:" + urlString);
        try {
            Bitmap bitmap = fetch(urlString);
            
            if (bitmap != null) {
            	bitmapMap.put(urlString, bitmap);
               
            } else {
              Log.w(this.getClass().getSimpleName(), "could not get thumbnail");
              return null;
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
        if (bitmapMap.containsKey(urlString)) {
        	Bitmap bitmap = bitmapMap.get(urlString);
//        	int maxHeight = GUIUtils.dp2px(mContext, 400);
//        	//int maxHeight = ((Activity)mContext).getWindowManager().getDefaultDisplay().getHeight();
//        	Log.v("maxHeight", maxHeight+"");
//        	int height = (int) ((float) imageView.getWidth()/bitmap.getWidth()* bitmap.getHeight());
//        	Log.v("height", "imageView's width is:"+imageView.getWidth()+""  
//        	+ "bitmap's width is:" + bitmap.getWidth() + "bitmap's height is:" 
//        			+ bitmap.getHeight());
//        	
//        	if (height > maxHeight) height = maxHeight;
//        	imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height));
            imageView.setImageBitmap(bitmap);
        }

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                
                Bitmap bitmap = (Bitmap) message.obj;
//                int maxHeight = GUIUtils.dp2px(mContext, 400);
//            	Log.v("maxHeight", maxHeight+"");
//            	int height = (int) ((float) imageView.getWidth()/bitmap.getWidth()* bitmap.getHeight());
//            	Log.v("height", "imageView's width is:"+imageView.getWidth()+""  
//                    	+ "bitmap's width is:" + bitmap.getWidth() + "bitmap's height is:" 
//                    			+ bitmap.getHeight());
//            	if (height > maxHeight) height = maxHeight;
//            	imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height));
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
    	BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(AppData.TRAVO_PATH + "/" + urlString + ".jpg", options);
        return bitmap;
    }
}