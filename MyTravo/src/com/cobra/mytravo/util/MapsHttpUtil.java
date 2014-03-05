package com.cobra.mytravo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class MapsHttpUtil
{
	private static final String KEY = "AIzaSyBr_1B0oeER7JwDBrA6DR4sGbzgZP3ZiqY";

	public static String getAroundPlace(String location, String radius) throws ClientProtocolException, IOException
	{
		String urlstr = "https://maps.googleapis.com/maps/api/place/search/json?location="
				+ location
				+ "&radius="
				+ radius
				+ "&language=zh-CN" + "&sensor=false&key=" + KEY;
		Log.i("aroundUrl", urlstr);
		HttpURLConnection connection = null; 
		URL url = new URL(urlstr);
		InputStreamReader in = null;
		connection = (HttpURLConnection) url.openConnection();
		in = new InputStreamReader(connection.getInputStream());
		BufferedReader buf = new BufferedReader(in);
		StringBuffer strbuf = new StringBuffer();
		String line;
		while((line = buf.readLine()) != null)
		{
			strbuf.append(line);
		}
		return strbuf.toString();
	}

	public static String getTextSearchPlaces(String location, String radius,String text) throws ClientProtocolException, IOException
	{
		String formattext = text.replace(" ", "+");
		Log.i("formattext", formattext);
		String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?language=zh-CN" + "&location=" +location +
				"&radius=" + radius + 
				"&query=" +
				formattext + "&sensor=true&key=" + KEY;
		Log.i("textsearch", url);
		HttpPost httpRequest = new HttpPost(url);
		
		HttpResponse httpResponse = new DefaultHttpClient()
				.execute(httpRequest);
		int res = httpResponse.getStatusLine().getStatusCode();
		if (res == 200)
		{
			StringBuilder builder = new StringBuilder();
			BufferedReader bufferedReader2 = new BufferedReader(
					new InputStreamReader(httpResponse.getEntity().getContent()));
			for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2
					.readLine())
			{
				builder.append(s);
			}
			return builder.toString();
		}
		return null;
	}
}
