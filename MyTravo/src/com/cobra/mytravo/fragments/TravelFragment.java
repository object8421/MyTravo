package com.cobra.mytravo.fragments;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cobra.mytravo.R;
import com.cobra.mytravo.activities.OtherUserInfoActivity;
import com.cobra.mytravo.activities.TravelDetailActivity;
import com.cobra.mytravo.adapters.CardsAnimationAdapter;
import com.cobra.mytravo.adapters.NotesAdapter;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.GsonRequest;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.data.RequestManager;
import com.cobra.mytravo.helpers.TimeUtils;
import com.cobra.mytravo.models.Note;
import com.cobra.mytravo.models.Travel;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TravelFragment extends V4BaseFragment implements PullToRefreshAttacher.OnRefreshListener{
	private String TAG = "travelfragment";
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_travel_detail, null);
		travel = ((TravelDetailActivity)getActivity()).getTravel();
		mPullToRefreshAttacher = ((TravelDetailActivity)getActivity()).getPullToRefreshAttacher();
		headerView = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_head_others_travel, null);
		avatar = (ImageView) headerView.findViewById(R.id.imageView1);
		avatar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), OtherUserInfoActivity.class);
				intent.putExtra("user", travel.getUser());
				getActivity().startActivity(intent);
			}
		});
		mDefaultAvatarBitmap = (BitmapDrawable) getActivity()
                .getResources().getDrawable(R.drawable.default_avatar);
		RequestManager.loadImage("http://travo-user-avatar.oss-cn-hangzhou.aliyuncs.com/"+travel.getUser().getFace_path(), RequestManager
                .getImageListener(avatar, mDefaultAvatarBitmap, mDefaultAvatarBitmap));
		nickname = (TextView) headerView.findViewById(R.id.nickname);
		cover = (ImageView) headerView.findViewById(R.id.img_travel_detail);
		title = (TextView) headerView.findViewById(R.id.tv_title_travel_detail);
		time = (TextView) headerView.findViewById(R.id.tv_time_travel_detail);
		content = (TextView) headerView.findViewById(R.id.tv_description_travel_detail);
		notesCount = (TextView) headerView.findViewById(R.id.tv_note_count_travel_detail);
		nickname.setText("by " + travel.getUser().getNickname());
		notesCount.setText("0");
		title.setText(travel.getTitle());
		content.setText(travel.getDescription());
		time.setText(TimeUtils.getListTime(travel.getCreate_time()));
		mListView = (ListView) view.findViewById(R.id.lv_travel_detail);
		mAdapter = new NotesAdapter(getActivity(), mListView, null);
		mListView.addHeaderView(headerView);
		mListView.setAdapter(mAdapter);
		
	    mPullToRefreshAttacher.addRefreshableView(mListView, this);
	    AnimationAdapter animationAdapter = new CardsAnimationAdapter(mAdapter);
        animationAdapter.setAbsListView(mListView);
        mListView.setAdapter(animationAdapter);
        if(travel.getCover_path() != null && travel.getSnap_path() != null && travel.getCover_path().equals(travel.getSnap_path()))
        	RequestManager.loadImage("http://travo-travel-cover.oss-cn-hangzhou.aliyuncs.com/"+travel.getCover_path(), RequestManager
                .getImageListener(cover, mDefaultImageDrawable, mDefaultImageDrawable));
        else
        	RequestManager.loadImage("http://travo-travel-cover-snap.oss-cn-hangzhou.aliyuncs.com/"+travel.getSnap_path(), RequestManager
                    .getImageListener(cover, mDefaultImageDrawable, mDefaultImageDrawable));
        loadData();
		return view;
	}
	private void loadData(){
		executeRequest((new GsonRequest<Note.NotesRequestData>(AppData.HOST_IP+"travel/"+travel.getId()+"/note?token="+AppData.getIdToken(),
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
                    	 Toast.makeText(getActivity(), R.string.refresh_list_failed,
                                 Toast.LENGTH_SHORT).show();
                        
                             mPullToRefreshAttacher.setRefreshComplete();
                        
                    }
                })));
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		loadData();
	}
}
