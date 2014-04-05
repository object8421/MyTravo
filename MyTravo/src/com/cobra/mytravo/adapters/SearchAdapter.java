package com.cobra.mytravo.adapters;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.cobra.mytravo.R;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class SearchAdapter extends BaseAdapter
{
	private List<JSONObject> jsons = new ArrayList<JSONObject>();
	private LayoutInflater mLayoutInflater;
	private ListView mListView;
	
	public SearchAdapter(Context context, ListView listView, List<JSONObject> jsons)
	{
		this.mLayoutInflater = ((Activity)context).getLayoutInflater();
		this.mListView = listView;
		this.jsons = jsons;
	}

	@Override
	public int getCount()
	{
		return jsons.size();
	}

	@Override
	public Object getItem(int position)
	{
		return jsons.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2)
	{
		return mLayoutInflater.inflate(R.layout.listitem_shot, null);
	}
}
