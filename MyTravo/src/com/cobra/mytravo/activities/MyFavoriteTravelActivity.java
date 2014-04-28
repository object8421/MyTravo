package com.cobra.mytravo.activities;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.adapters.CardsAnimationAdapter;
import com.cobra.mytravo.adapters.CommentAdapter;
import com.cobra.mytravo.adapters.HotTravelAdapter;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.GsonRequest;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.data.RequestManager;
import com.cobra.mytravo.helpers.ActionBarUtils;
import com.cobra.mytravo.models.Comment;
import com.cobra.mytravo.models.Travel;
import com.cobra.mytravo.models.Comment.CommentRequestData;
import com.cobra.mytravo.models.Travel.TravelsRequestData;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyFavoriteTravelActivity extends Activity implements PullToRefreshAttacher.OnRefreshListener{
	private String TAG = "myfavoritetravelactivity";
	private ListView mListView;
	private ArrayList<Travel> travels;
	private int travel_id;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private HotTravelAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_favorite_travel);
		ActionBarUtils.InitialActionBarWithBackAndTitle(this, getActionBar(), "我的收藏");
		mListView = (ListView) findViewById(R.id.listView1);
		mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
		mAdapter = new HotTravelAdapter(this, mListView, null);
		mListView.setAdapter(mAdapter);
		mPullToRefreshAttacher.addRefreshableView(mListView, this);
	    AnimationAdapter animationAdapter = new CardsAnimationAdapter(mAdapter);
        animationAdapter.setAbsListView(mListView);
        mListView.setAdapter(animationAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Travel travel = mAdapter.getItem(position);
				if(travel != null){
					Intent intent = new Intent(MyFavoriteTravelActivity.this, TravelDetailActivity.class);
					intent.putExtra("travel", travel);
					startActivity(intent);
				}
			}
		});
        getFavoriteTravels();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_favorite_travel, menu);
		return true;
	}

	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		getFavoriteTravels();
	}
	private void getFavoriteTravels(){
		if(!mPullToRefreshAttacher.isRefreshing()){
			mPullToRefreshAttacher.setRefreshing(true);
		}
		RequestManager.addRequest(new GsonRequest<Travel.TravelsRequestData>(AppData.HOST_IP+"travel/favorit?token="+AppData.getIdToken(),
				Travel.TravelsRequestData.class, null, new Listener<Travel.TravelsRequestData>() {

					@Override
					public void onResponse(TravelsRequestData requestData) {
						// TODO Auto-generated method stub
						int rsp_code = requestData.getRsp_code();
						Log.i(TAG, String.valueOf(rsp_code));
						if(rsp_code == MyServerMessage.SUCCESS){
							travels = requestData.getTravels();
							mAdapter.clearData();
							mAdapter.setData(travels);
						}
						mPullToRefreshAttacher.setRefreshComplete();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						// TODO Auto-generated method stub
						Toast.makeText(MyFavoriteTravelActivity.this, R.string.refresh_list_failed,
                                Toast.LENGTH_SHORT).show();
						mPullToRefreshAttacher.setRefreshComplete();
					}
				}), this);
	}
}
