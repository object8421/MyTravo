package com.cobra.mytravo.activities;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.adapters.CardsAnimationAdapter;
import com.cobra.mytravo.adapters.HotTravelAdapter;
import com.cobra.mytravo.adapters.OtherTravelAdapter;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.GsonRequest;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.data.RequestManager;
import com.cobra.mytravo.internet.user.GetUserInfoService;
import com.cobra.mytravo.models.Travel;
import com.cobra.mytravo.models.User;
import com.cobra.mytravo.models.UserInfo;
import com.cobra.mytravo.models.Travel.TravelsRequestData;
import com.cobra.mytravo.models.UserInfo.UserInfoRequestData;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OtherUserInfoActivity extends Activity implements PullToRefreshAttacher.OnRefreshListener{
	private String TAG = "otheruserinfoactivity";
	private User user;
	private UserInfo userInfo;
	private ArrayList<Travel> travels;
	private ListView mListView;
	private OtherTravelAdapter mAdapter;
	private View headerView;
	private TextView nickname;
	private TextView gender;
	private TextView signature;
	private Button focusButton;
	private ImageView avatar;
	private TextView travelCounTextView;
	private TextView followingCountTextView;
	private TextView favoriteCountTextView;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private Drawable mDefaultImageDrawable = new ColorDrawable(Color.argb(255, 201, 201, 201));
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_other_user_info);
		user = (User) getIntent().getSerializableExtra("user");
		mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
		headerView = LayoutInflater.from(this).inflate(R.layout.userinfo_header, null);
		avatar = (ImageView) headerView.findViewById(R.id.avatar);
		nickname = (TextView) headerView.findViewById(R.id.tv_nickname);
		gender = (TextView) headerView.findViewById(R.id.tv_gender);
		signature = (TextView) headerView.findViewById(R.id.tv_signature);
		focusButton = (Button) headerView.findViewById(R.id.btn_focus);
		travelCounTextView = (TextView) headerView.findViewById(R.id.tv_travel_count);
		followingCountTextView = (TextView) headerView.findViewById(R.id.tv_folower_count);
		favoriteCountTextView = (TextView) headerView.findViewById(R.id.tv_favorite_count);
		travelCounTextView.setText(String.valueOf(user.getTravel_qty()));
		followingCountTextView.setText(String.valueOf(user.getFollower_qty()));
		favoriteCountTextView.setText(String.valueOf(user.getFavorite_travel_qty()));
		nickname.setText(user.getNickname());
		signature.setText(user.getSignature());
		RequestManager.loadImage("http://travo-user-avatar.oss-cn-hangzhou.aliyuncs.com/"+user.getFace_path(), RequestManager
                .getImageListener(avatar, mDefaultImageDrawable, mDefaultImageDrawable));
		mListView = (ListView) findViewById(R.id.lv_travel);
		mAdapter = new OtherTravelAdapter(this, mListView, null);
		mListView.addHeaderView(headerView);
		mListView.setAdapter(mAdapter);
		
	    mPullToRefreshAttacher.addRefreshableView(mListView, this);
	    AnimationAdapter animationAdapter = new CardsAnimationAdapter(mAdapter);
        animationAdapter.setAbsListView(mListView);
        mListView.setAdapter(animationAdapter);
        getUserInfo();
        loadData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.other_user_info, menu);
		return true;
	}

	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		getUserInfo();
		loadData();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		RequestManager.cancelAll(this);
	}
	private void getUserInfo(){
		RequestManager.addRequest(new GsonRequest<UserInfo.UserInfoRequestData>(AppData.HOST_IP + "user/" + user.getId() + "/info",
				UserInfo.UserInfoRequestData.class, null, new Listener<UserInfo.UserInfoRequestData>() {

					@Override
					public void onResponse(UserInfoRequestData requestData) {
						// TODO Auto-generated method stub
						int rsp_code = requestData.getRsp_code();
						if(rsp_code == MyServerMessage.SUCCESS){
							userInfo = requestData.getUser_info();
							if(userInfo.getSex() != null){
								gender.setText(userInfo.getSex());
							}
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						// TODO Auto-generated method stub
						Toast.makeText(OtherUserInfoActivity.this, R.string.refresh_list_failed,
                                Toast.LENGTH_SHORT).show();
					}
				}), this);
	}
	private void loadData(){
		mPullToRefreshAttacher.setRefreshing(true);
		RequestManager.addRequest(new GsonRequest<Travel.TravelsRequestData>(AppData.HOST_IP + "friend/" + user.getId() + "/travels?token=" + AppData.getIdToken(),
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
						Toast.makeText(OtherUserInfoActivity.this, R.string.refresh_list_failed,
                                Toast.LENGTH_SHORT).show();
						mPullToRefreshAttacher.setRefreshComplete();
					}
				}), this);
	}
}
