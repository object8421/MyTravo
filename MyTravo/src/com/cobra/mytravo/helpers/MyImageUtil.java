package com.cobra.mytravo.helpers;

import java.io.IOException;
import java.net.MalformedURLException;

import com.cobra.mytravo.data.AppData;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MyImageUtil {
	public static String getImageUrlString(){
		return null;
	}
	public static void setBitmap(ImageView imageView, String url){
		String pathString = AppData.TRAVO_PATH+"/"+url+".jpg";
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        imageView.setImageBitmap(BitmapFactory.decodeFile(pathString, options));
	}
	public static void setBitmap(ImageButton imageButton, String url){
		String pathString = AppData.TRAVO_PATH+"/"+url+".jpg";
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        imageButton.setImageBitmap(BitmapFactory.decodeFile(pathString, options));
	}
	public static void setBitmapResize(final Context context, final ImageView imageView, final String url){
		
        
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                
                Bitmap bitmap = (Bitmap) message.obj;
                if(bitmap != null){
                	int maxHeight = GUIUtils.dp2px(context, 400);
                	Log.v("maxHeight", maxHeight+"");
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
    	BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(AppData.TRAVO_PATH + "/" + urlString + ".jpg", options);
        return bitmap;
    }
}
