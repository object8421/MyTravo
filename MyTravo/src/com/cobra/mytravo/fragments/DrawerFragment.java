package com.cobra.mytravo.fragments;

import com.cobra.mytravo.R;
import com.cobra.mytravo.activities.MainActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class DrawerFragment extends BaseFragment{
	//private ImageView avatarImageView;
	private ListView drawerListView;
	private MainActivity mainActivity;
	
	private String[] listItems;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		mainActivity = (MainActivity)getActivity();
		View view = inflater.inflate(R.layout.fragment_drawer, null);
		listItems = getResources().getStringArray(R.array.drawermenu);
		drawerListView = (ListView) view.findViewById(R.id.drawer_listview);
		drawerListView.setItemChecked(0, true);
		drawerListView.setAdapter(new ArrayAdapter<String>(mainActivity, R.layout.drawer_list_item,R.id.tv_title,listItems));
		drawerListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				drawerListView.setItemChecked(position, true);
				mainActivity.setSelectedItem(position);
			}
		});		
		return view;
	}
	
}
