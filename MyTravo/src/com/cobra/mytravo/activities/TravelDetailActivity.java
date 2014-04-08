package com.cobra.mytravo.activities;



import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.adapters.CardsAnimationAdapter;
import com.cobra.mytravo.adapters.NotesAdapter;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.GsonRequest;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.data.RequestManager;
import com.cobra.mytravo.helpers.ActionBarUtils;
import com.cobra.mytravo.helpers.TimeUtils;
import com.cobra.mytravo.models.Note;
import com.cobra.mytravo.models.Travel;
import com.cobra.mytravo.ui.LoadingFooter;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TravelDetailActivity extends Activity implements PullToRefreshAttacher.OnRefreshListener{
	private String TAG = "traveldetailactivity";
	private Travel travel;
	private ArrayList<Note> notes;
	private ListView mListView;
	private View headerView;
	private TextView title;
	private TextView nickname;
	private ImageView cover;
	private ImageView avatar;
	private TextView time;
	private TextView notesCount;
	private TextView content;
	private int count;//number of notes
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private NotesAdapter mAdapter;
	private BitmapDrawable mDefaultAvatarBitmap;
	private Drawable mDefaultImageDrawable = new ColorDrawable(Color.argb(255, 201, 201, 201));
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(ActionBarUtils.hasSmartBar()){
			getWindow().setUiOptions(ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
		}
		setContentView(R.layout.activity_travel_detail);
		travel = (Travel) getIntent().getSerializableExtra("travel");
		InitialView();
		loadData();
	}
	private void InitialView(){
		ActionBarUtils.InitialDarkActionBar(this, getActionBar(), "游记详情");
		mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
		
		headerView = LayoutInflater.from(this).inflate(R.layout.listitem_head_travel, null);
		cover = (ImageView) headerView.findViewById(R.id.img_travel_detail);
		title = (TextView) headerView.findViewById(R.id.tv_title_travel_detail);
		time = (TextView) headerView.findViewById(R.id.tv_time_travel_detail);
		content = (TextView) headerView.findViewById(R.id.tv_description_travel_detail);
		notesCount = (TextView) headerView.findViewById(R.id.tv_note_count_travel_detail);
		notesCount.setText("0");
		title.setText(travel.getTitle());
		content.setText(travel.getDescription());
		time.setText(TimeUtils.getListTime(travel.getCreate_time()));
		mListView = (ListView) findViewById(R.id.lv_travel_detail);
		mAdapter = new NotesAdapter(this, mListView, null);
		mListView.addHeaderView(headerView);
		mListView.setAdapter(mAdapter);
		
	    mPullToRefreshAttacher.addRefreshableView(mListView, this);
	    AnimationAdapter animationAdapter = new CardsAnimationAdapter(mAdapter);
        animationAdapter.setAbsListView(mListView);
        mListView.setAdapter(animationAdapter);
		RequestManager.loadImage("http://travo-travel-cover.oss-cn-hangzhou.aliyuncs.com/"+travel.getCover_path(), RequestManager
                .getImageListener(cover, mDefaultImageDrawable, mDefaultImageDrawable));
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.travel_detail, menu);
		return true;
	}
	private void loadData(){
		RequestManager.addRequest(new GsonRequest<Note.NotesRequestData>(AppData.HOST_IP+"travel/"+travel.getId()+"/note",
        		Note.NotesRequestData.class, null,
                new Response.Listener<Note.NotesRequestData>() {
                    @Override
                    public void onResponse(final Note.NotesRequestData requestData) {
                        	    
                            	
                            	int rsp_code = requestData.getRsp_code();
                            	if(rsp_code == MyServerMessage.SUCCESS){
                            		notes = requestData.getNotes();
                            		 Log.i(TAG,notes.toString());
                            		 mAdapter.clearData();
                                     mAdapter.setData(notes);
                                     notesCount.setText(String.valueOf(notes.size()));
                            	}
                            		
                               
                                mPullToRefreshAttacher.setRefreshComplete();
                                
                            
                     
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    	 Toast.makeText(TravelDetailActivity.this, R.string.refresh_list_failed,
                                 Toast.LENGTH_SHORT).show();
                        
                             mPullToRefreshAttacher.setRefreshComplete();
                        
                    }
                }), this);
	}
	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		loadData();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		RequestManager.cancelAll(this);
	}
	
}
