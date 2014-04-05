package com.cobra.mytravo.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cobra.mytravo.R;
import com.cobra.mytravo.activities.MainActivity;
import com.cobra.mytravo.adapters.SearchAdapter;
import com.cobra.mytravo.internet.SearchTravelService;

public class SearchFragment extends BaseFragment
{
	private final static String TAG = "SearchFragment";
	
	private MainActivity mActivity;
	private SearchAdapter searchAdapter;
	private ListView mListView;
	private SearchTravelService searchTravelService;
	private List<JSONObject> jsons;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what)
			{
			case 1:
				searchAdapter = new SearchAdapter(getActivity(), mListView, jsons);
				mListView.setAdapter(searchAdapter);
				break;
			}
		};
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_search, null);
		mListView = (ListView) view.findViewById(R.id.search_listview);
		
		mActivity = (MainActivity) getActivity();
		
		Intent intent = new Intent(mActivity, SearchTravelService.class);
		mActivity.bindService(intent, conn, Context.BIND_AUTO_CREATE);
		mActivity.startService(intent);
		
		new Thread(){
			public void run() {
				while(searchTravelService == null)
				{
					try {  
                        Thread.sleep(1000);  
                    } catch (InterruptedException e) {  
                        e.printStackTrace();  
                    }
				}
				while(searchTravelService.getProgress() != 100)
				{
					Log.i("progress", String.valueOf(searchTravelService.getProgress()));
					try {  
                        Thread.sleep(1000);  
                    } catch (InterruptedException e) {  
                        e.printStackTrace();  
                    } 	
				}
				Log.i("progress", String.valueOf(searchTravelService.getProgress()));
				jsons = searchTravelService.getJsons();
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
			};
		}.start();
		return view;
	}
	
	ServiceConnection conn = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder service) {
			Log.i("searchTravelService", "get");
			searchTravelService = ((SearchTravelService.SearchTravelBinder)service).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
		}
		
	};
	
	@Override
	public void onDestroy() {
		mActivity.unbindService(conn);
		super.onDestroy();
	};
	
}
