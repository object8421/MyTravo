package com.cobra.mytravo.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class BitmapManager1 {
    
    private final Map<String, Bitmap> bitmapMap;
    public BitmapManager1() {
       
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
            imageView.setImageBitmap(bitmapMap.get(urlString));
        }

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                imageView.setImageBitmap((Bitmap) message.obj);
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
        options.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeFile(urlString, options);
        return bitmap;
    }
}