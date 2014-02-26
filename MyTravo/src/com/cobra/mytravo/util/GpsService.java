package com.cobra.mytravo.util;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

public class GpsService extends Service
{
	private Gps gps = null;
	private boolean threadDisable = false;

	@Override
	public void onCreate()
	{
		super.onCreate();

		gps = new Gps(GpsService.this);
		// cellIds=UtilTool.init(GpsService.this)

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				while (!threadDisable)
				{
					try
					{
						Thread.sleep(1000);
					} catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (gps != null)
					{
						Location location = gps.getLocation();
						AddressParser addressparser = new AddressParser(GpsService.this, location);
						String address = addressparser.getAddresses().get(0);
						
						if (location == null)
						{
//							Log.i("location", "null");
						} else
						{
//							Log.i("location", "notnull");
						}
						Intent intent = new Intent();
						intent.putExtra("lat",
								location == null ? "" : location.getLatitude()
										+ "");
						intent.putExtra("lon",
								location == null ? "" : location.getLongitude()
										+ "");
						intent.putExtra("address", address == null? "" : address);
						
						intent.setAction("com.cobra.mytravo.util.GpsService");
						sendBroadcast(intent);
					}
				}
			}

		}).start();
	}

	@Override
	public void onDestroy()
	{
		threadDisable = true;
		if (gps != null)
		{
			gps.closeLocation();
			gps = null;
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
