package com.cobra.mytravo.activities;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.astuetz.PagerSlidingTabStrip;
import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.adapters.CardsAnimationAdapter;
import com.cobra.mytravo.adapters.NotesAdapter;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.GsonRequest;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.data.RequestManager;
import com.cobra.mytravo.fragments.SuperAwesomeCardFragment;
import com.cobra.mytravo.fragments.TravelCommentsFragment;
import com.cobra.mytravo.fragments.TravelFragment;
import com.cobra.mytravo.helpers.ActionBarUtils;
import com.cobra.mytravo.helpers.TimeUtils;
import com.cobra.mytravo.models.CommonRequestData;
import com.cobra.mytravo.models.Note;
import com.cobra.mytravo.models.Travel;
import com.cobra.mytravo.ui.LoadingFooter;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TravelDetailActivity extends FragmentActivity{
	private String TAG = "traveldetailactivity";
	private Travel travel;
	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MyPagerAdapter adapter;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private View mCustomView;
	private TextView title;
	private int mActionBarOptions;
	private AlertDialog.Builder commentAlertDialog;
	private String comment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(ActionBarUtils.hasSmartBar()){
			getWindow().setUiOptions(ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
		}
		setContentView(R.layout.activity_travel_detail);
		
		travel = (Travel) getIntent().getSerializableExtra("travel");
		
		InitialView();
		
	}
	private void InitialView(){
		ActionBarUtils.InitialDarkActionBar(this, getActionBar(), "游记详情");
		mCustomView = LayoutInflater.from(this).inflate(R.layout.actionbar_customview, null);
		title = (TextView) mCustomView.findViewById(R.id.tv_title);
		title.setText("游记详情");
		title.setTextSize((float)20.0);
		title.setTextColor(getResources().getColor(android.R.color.white));
		getActionBar().setCustomView(mCustomView);
		mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
		tabs = (PagerSlidingTabStrip) mCustomView.findViewById(R.id.actionbar_tabs);
		//tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager = (ViewPager) findViewById(R.id.pager);
		adapter = new MyPagerAdapter(this.getSupportFragmentManager());
		pager.setAdapter(adapter);
		tabs.setViewPager(pager);
	}
	@Override
    protected void onResume() {
        super.onResume();
        
        ActionBar bar =getActionBar();
        mActionBarOptions = bar.getDisplayOptions();
        // 设置DisplayOptions，显示ActionBar自定义的View
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM | mActionBarOptions);
        
        
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.travel_detail, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case R.id.action_comment:
			if(AppData.getIsLogin()){
				commentAlertDialog = new AlertDialog.Builder(this);
				commentAlertDialog.setTitle("评论游记");
				commentAlertDialog.setMessage("请输入您的评论");
				final EditText contentEditText = new EditText(this);
				commentAlertDialog.setView(contentEditText);
				commentAlertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
				       public void onClick(DialogInterface dialog, int whichButton) {  
				    	   comment = contentEditText.getText().toString();  
				    	   uploadComment(comment);
				             }  
				           });  
				commentAlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
				       public void onClick(DialogInterface dialog, int whichButton) {  

				           return;
				         }  
				      });  
				commentAlertDialog.show();
			}else{
				Toast.makeText(this, "您尚未登录，不能进行评论哦", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.action_vote:
			if(AppData.getIsLogin()){
				Vote();
			}else{
				Toast.makeText(this, "您尚未登录，不能点赞哦", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.action_favorite:
			if(AppData.getIsLogin()){
				Favorite();
			}else{
				Toast.makeText(this, "您尚未登录，不能使用收藏功能哦", Toast.LENGTH_SHORT).show();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		RequestManager.cancelAll(this);
	}
	public Travel getTravel(){
		return this.travel;
	}
	public PullToRefreshAttacher getPullToRefreshAttacher() {
        return mPullToRefreshAttacher;
    }
	public class MyPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "足迹", "评论"};

		public MyPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			if(position == 0){
				return new TravelFragment();
			}
			else{
				return new TravelCommentsFragment();
			}
				
			
		}

	}
	private void uploadComment(String comment){
		Map<String, String> map = new HashMap<String, String>();
		map.put("content", comment);
		
		RequestManager.addRequest(new GsonRequest<CommonRequestData>(com.android.volley.Request.Method.POST,AppData.HOST_IP + "travel/" + travel.getId() + 
				"/comment?token=" + AppData.getIdToken(), CommonRequestData.class, null, new Response.Listener<CommonRequestData>(){
					@Override
                    public void onResponse(final CommonRequestData requestData) {
                        	    
                            	
                            	int rsp_code = requestData.getRsp_code();
                            	if(rsp_code == MyServerMessage.SUCCESS){
                            		Toast.makeText(TravelDetailActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                            	}
                            	
                    }
				}, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    	 Toast.makeText(TravelDetailActivity.this, "评论失败",
                                 Toast.LENGTH_SHORT).show();
                        
                    }
                }, map),this);
	}
	private void Vote(){
		RequestManager.addRequest(new GsonRequest<CommonRequestData>(com.android.volley.Request.Method.POST,AppData.HOST_IP + "travel/" + travel.getId() + 
				"/vote?token=" + AppData.getIdToken(), CommonRequestData.class, null, new Response.Listener<CommonRequestData>(){
					@Override
                    public void onResponse(final CommonRequestData requestData) {
                        	    
                            	
                            	int rsp_code = requestData.getRsp_code();
                            	if(rsp_code == MyServerMessage.SUCCESS){
                            		Toast.makeText(TravelDetailActivity.this, "点赞成功", Toast.LENGTH_SHORT).show();
                            	}
                            	
                    }
				}, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    	 Toast.makeText(TravelDetailActivity.this, "点赞失败",
                                 Toast.LENGTH_SHORT).show();
                        
                    }
                }, null),this);
	}
	private void Favorite(){
		RequestManager.addRequest(new GsonRequest<CommonRequestData>(com.android.volley.Request.Method.POST,AppData.HOST_IP + "travel/" + travel.getId() + 
				"/favorit?token=" + AppData.getIdToken(), CommonRequestData.class, null, new Response.Listener<CommonRequestData>(){
					@Override
                    public void onResponse(final CommonRequestData requestData) {
                        	    
                            	
                            	int rsp_code = requestData.getRsp_code();
                            	if(rsp_code == MyServerMessage.SUCCESS){
                            		Toast.makeText(TravelDetailActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                            	}
                            	
                    }
				}, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    	 Toast.makeText(TravelDetailActivity.this, "收藏失败",
                                 Toast.LENGTH_SHORT).show();
                        
                    }
                }, null),this);
	}
}
