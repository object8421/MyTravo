package com.cobra.mytravo.internet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.NotesDataHelper;
import com.cobra.mytravo.models.Note;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

public class GetMyNotePicture extends IntentService
{
	Note note = null;
	NotesDataHelper notesDataHelper;

	public GetMyNotePicture()
	{
		super("GetMyNotePicture");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log.i("getMyNotePicture", "start");
		int id = intent.getIntExtra("note_id", 0);
		String image_path = intent.getStringExtra("image_path");
		notesDataHelper = new NotesDataHelper(this, AppData.getUserId());
		note = notesDataHelper.getNoteById(id);

		String imageUrl = "http://travo-note-pic.oss-cn-hangzhou.aliyuns.com/" + image_path;

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
						+ "/Travo/" + note.getId());
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
				note.setImage_url(file.getPath());
				notesDataHelper.update(note);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
