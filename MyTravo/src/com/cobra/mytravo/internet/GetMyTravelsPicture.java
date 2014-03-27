package com.cobra.mytravo.internet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.TravelsDataHelper;
import com.cobra.mytravo.helpers.PhotoUtils;
import com.cobra.mytravo.models.Travel;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;

public class GetMyTravelsPicture extends IntentService
{
	Travel travel = null;
	TravelsDataHelper mDataHelper;

	public GetMyTravelsPicture()
	{
		super("getMyTravelsPicture");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		PhotoUtils.makeDir();

		int id = intent.getIntExtra("travel_id", 0);
		mDataHelper = new TravelsDataHelper(this, AppData.getUserId());
		travel = mDataHelper.queryById(id);

		String imageUrl = AppData.HOST_IP + "travel/" + String.valueOf(id)
				+ "/cover?token" + AppData.getIdToken();

		URL url;
		File file = null;
		try
		{
			url = new URL(imageUrl);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.connect();
			if (urlConnection.getResponseCode() != 404)
			{
				InputStream is = urlConnection.getInputStream();
				if (is == null)
				{
					throw new RuntimeException("stream is null");
				}
				file = new File(Environment.getExternalStorageDirectory()
						+ "/Travo/" + travel.getId());
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
				travel.setCover_url(file.getPath());
				mDataHelper.update(travel);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
