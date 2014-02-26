package com.cobra.mytravo.util;

import java.util.List;

import com.cobra.mytravo.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AroundListAdapter extends BaseAdapter
{
	private List<Place> aroundPlaces;
	private Context context;
	
	public AroundListAdapter(Context context, List<Place> aroundPlaces)
	{
		this.context = context;
		this.aroundPlaces = aroundPlaces;
	}
	
	@Override
	public int getCount()
	{
		return aroundPlaces.size();
	}

	@Override
	public Place getItem(int arg0)
	{
		return aroundPlaces.get(arg0);
	}

	@Override
	public long getItemId(int arg0)
	{
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if(convertView == null)
		{
			convertView = (View)LayoutInflater.from(context).inflate(R.layout.place_list_item,null);
		}
		TextView placeName = (TextView) convertView.findViewById(R.id.place_name);
		TextView placeAddress = (TextView) convertView.findViewById(R.id.place_address);
		placeName.setText(aroundPlaces.get(position).getName());
		placeAddress.setText(aroundPlaces.get(position).getAddress());
		return convertView;
	}
}
