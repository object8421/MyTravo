package com.cobra.mytravo.fragments;

import com.android.volley.toolbox.ImageLoader;
import com.cobra.mytravo.R;
import com.cobra.mytravo.activities.MainActivity;
import com.cobra.mytravo.activities.UserInfoActivity;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.RequestManager;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class DrawerFragment extends BaseFragment{
	private ImageView avatarImageView;
	private ListView drawerListView;
	private MainActivity mainActivity;
	private BitmapDrawable mDefaultAvatarBitmap ;
	private String[] listItems;
	private ImageLoader.ImageContainer imageRequest;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		mainActivity = (MainActivity)getActivity();
		View view = inflater.inflate(R.layout.fragment_drawer, null);
		mDefaultAvatarBitmap = (BitmapDrawable) mainActivity
                .getResources().getDrawable(R.drawable.default_avatar);
		avatarImageView = (ImageView) view.findViewById(R.id.avatar_imgview);
		avatarImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent userInfoIntent = new Intent(getActivity(), UserInfoActivity.class);
				getActivity().startActivity(userInfoIntent);
			}
		});
		
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
		
//		RequestManager.loadImage("http://travo-travel-cover.oss-cn-hangzhou.aliyuncs.com/"+travel.getCover_path(), RequestManager
//                .getImageListener(cover, mDefaultImageDrawable, mDefaultImageDrawable));
		return view;
	}
	
}
