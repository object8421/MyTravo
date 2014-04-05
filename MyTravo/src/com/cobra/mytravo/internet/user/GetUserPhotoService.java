package com.cobra.mytravo.internet.user;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.NotesDataHelper;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

public class GetUserPhotoService extends IntentService
{

	public GetUserPhotoService()
	{
		super("GetUserPhotoService");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log.i("GetUserPhotoService", "start");
		
		int user_id = intent.getIntExtra("user_id", 0);

		String imageUrl = AppData.HOST_IP + "user/" + String.valueOf(user_id)
				+ "/face?" ;

		URL url;
		File file = null;
		try
		{
			url = new URL(imageUrl);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.connect();
			if (404 != urlConnection.getResponseCode())
			{
				InputStream is = urlConnection.getInputStream();
				if (is == null)
				{
					throw new RuntimeException("stream is null");
				}
				file = new File(Environment.getExternalStorageDirectory()
						+ "/Travo/" + "user" + String.valueOf(user_id));
				FileOutputStream fos = new FileOutputStream(file);
				byte buf[] = new byte[128];
				do
				{
					int numread = is.read(buf);
					if (numread <= 0)
					{
						break;
					}
					fos.write(buf, 0, numread);
				} while (true);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
