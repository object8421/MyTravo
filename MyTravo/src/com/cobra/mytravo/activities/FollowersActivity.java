package com.cobra.mytravo.activities;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.adapters.FollowersAdapter;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.GsonRequest;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.data.RequestManager;
import com.cobra.mytravo.helpers.ActionBarUtils;
import com.cobra.mytravo.models.Travel;
import com.cobra.mytravo.models.Travel.TravelsRequestData;
import com.cobra.mytravo.models.User;
import com.cobra.mytravo.models.User.UserFollowersRequestData;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class FollowersActivity extends Activity implements PullToRefreshAttacher.OnRefreshListener{
	private String TAG = "followersativity";
	private int type;
	private ListView mListView;
	private FollowersAdapter mAdapter;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private ArrayList<User> users;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_followers);
		ActionBarUtils.InitialActionBarWithBackAndTitle(this, getActionBar(), "关注列表");
		mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
		mListView = (ListView) findViewById(R.id.listView1);
		mAdapter = new FollowersAdapter(this, mListView, null);
		mListView.setAdapter(mAdapter);
		mPullToRefreshAttacher.addRefreshableView(mListView, this);
		getData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.followers, menu);
		return true;
	}
	private void getData(){
		mPullToRefreshAttacher.setRefreshing(true);
		RequestManager.addRequest(new GsonRequest<User.UserFollowersRequestData>(AppData.HOST_IP + "user/follow/list?token=" + AppData.getIdToken(),
				User.UserFollowersRequestData.class, null, new Listener<User.UserFollowersRequestData>() {

					@Override
					public void onResponse(UserFollowersRequestData requestData) {
						// TODO Auto-generated method stub
						int rsp_code = requestData.getRsp_code();
						Log.i(TAG, String.valueOf(rsp_code));
						if(rsp_code == MyServerMessage.SUCCESS){
							users = requestData.getUsers();
							mAdapter.clearData();
							mAdapter.setData(users);
						}
						mPullToRefreshAttacher.setRefreshComplete();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						// TODO Auto-generated method stub
						Toast.makeText(FollowersActivity.this, R.string.refresh_list_failed,
                                Toast.LENGTH_SHORT).show();
						mPullToRefreshAttacher.setRefreshComplete();
					}
				}), this);
	}

	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		getData();
	}
}
