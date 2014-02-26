package com.cobra.mytravo.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

public class AddressParser
{
	private Context context;
	private Location location;

	public AddressParser(Context context, Location location)
	{
		this.context = context;
		this.location = location;
	}
	
	public static String getAddressText(Address address)
	{
		return String.format("%s", address.getLocality() + address.getSubLocality() 
				+ address.getThoroughfare());
	}
	
	public ArrayList<String> getAddresses()
	{
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Geocoder.isPresent())
		{
				try
				{
					return (new GetAddressTask(context)).execute(location).get();
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return null;
	}
	
	private class GetAddressTask extends AsyncTask<Location, Void, ArrayList<String>>
	{
		private Context context;

		public GetAddressTask(Context context)
		{
			super();
			this.context = context;
		}
		
		@Override
		protected ArrayList<String> doInBackground(Location... locations)
		{
			Geocoder geocoder = new Geocoder(context, Locale.getDefault());
			Location loc = locations[0];
			List<Address> addresses = null;
			
			try
			{
				addresses  = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 10);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			if(addresses == null)Log.i("alsdfkjasdlf;jkasd;lfkj", "jlaksdjfa;lsdkfja");
			if(addresses != null && addresses.size() > 0)
			{
//				Address address = addresses.get(0);
//				nowAddress = address;
//				aroundAddresses = (ArrayList<Address>) addresses;
//				String addressText = String.format(
//						"%s", address.getLocality() + address.getSubLocality() 
//						+ address.getThoroughfare() + address.getSubThoroughfare());
//				return addressText;
				ArrayList<String> adds = new ArrayList<String>();
				for (Address address : addresses)
				{
					adds.add(AddressParser.getAddressText(address));
				}
				return adds;
			}
			else
			{
				ArrayList<String> adds = new ArrayList<String>();
				adds.add("No address founded");
				return adds;
			}
		}
		
	}
}
