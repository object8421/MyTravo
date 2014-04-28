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
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.GsonRequest;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.data.RequestManager;
import com.cobra.mytravo.helpers.ActionBarUtils;
import com.cobra.mytravo.internet.GetCommentsService.GetCommentsBinder;
import com.cobra.mytravo.models.Comment;
import com.cobra.mytravo.models.User;
import com.cobra.mytravo.models.Comment.CommentRequestData;
import com.cobra.mytravo.models.Travel;
import com.cobra.mytravo.models.Travel.TravelsRequestData;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PersonalTravelCommentActivity extends Activity implements PullToRefreshAttacher.OnRefreshListener{
	private String TAG = "personaltravelcommentactivity";
	private ListView mListView;
	private ArrayList<Comment> comments;
	private int travel_id;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private CommentAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_travel_comment);
		ActionBarUtils.InitialActionBarWithBackAndTitle(this, getActionBar(), "游记评论");
		mListView = (ListView) findViewById(R.id.listView1);
		mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
		mAdapter = new CommentAdapter(this, mListView, null);
		mListView.setAdapter(mAdapter);
	    mPullToRefreshAttacher.addRefreshableView(mListView, this);
	    AnimationAdapter animationAdapter = new CardsAnimationAdapter(mAdapter);
        animationAdapter.setAbsListView(mListView);
        mListView.setAdapter(animationAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				User user = mAdapter.getItem(position - mListView.getHeaderViewsCount()).getCommenter();
				if(user != null){
					if(user.getId() != AppData.getUserId()){
						Intent intent = new Intent(PersonalTravelCommentActivity.this, OtherUserInfoActivity.class);
						intent.putExtra("user", user);
						startActivity(intent);
					}
						
					
				}
				
			}
		});
		travel_id = getIntent().getIntExtra("travel_id", -1);
		if(travel_id != -1){
			getComments();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.personal_travel_comment, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		getComments();
	}
	private void getComments(){
		mPullToRefreshAttacher.setRefreshing(true);
		RequestManager.addRequest(new GsonRequest<Comment.CommentRequestData>(AppData.HOST_IP + "travel/" + travel_id + "/comments",
				Comment.CommentRequestData.class, null, new Listener<Comment.CommentRequestData>() {

					@Override
					public void onResponse(CommentRequestData requestData) {
						// TODO Auto-generated method stub
						int rsp_code = requestData.getRsp_code();
						Log.i(TAG, String.valueOf(rsp_code));
						if(rsp_code == MyServerMessage.SUCCESS){
							comments = requestData.getComments();
							mAdapter.clearData();
							mAdapter.setData(comments);
						}
						mPullToRefreshAttacher.setRefreshComplete();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						// TODO Auto-generated method stub
						Toast.makeText(PersonalTravelCommentActivity.this, R.string.refresh_list_failed,
                                Toast.LENGTH_SHORT).show();
						mPullToRefreshAttacher.setRefreshComplete();
					}
				}), this);
	}
	
}
