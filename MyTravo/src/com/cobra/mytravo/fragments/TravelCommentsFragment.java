package com.cobra.mytravo.fragments;

import java.util.ArrayList;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cobra.mytravo.R;
import com.cobra.mytravo.activities.TravelDetailActivity;
import com.cobra.mytravo.adapters.CardsAnimationAdapter;
import com.cobra.mytravo.adapters.CommentAdapter;
import com.cobra.mytravo.adapters.NotesAdapter;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.GsonRequest;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.models.Comment;
import com.cobra.mytravo.models.Note;
import com.cobra.mytravo.models.Travel;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

public class TravelCommentsFragment extends V4BaseFragment implements PullToRefreshAttacher.OnRefreshListener{
	private String TAG = "travelcommentfragment";
	private Travel travel;
	private ArrayList<Comment> comments;
	private ListView mListView;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private CommentAdapter mAdapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_travel_detail, null);
		travel = ((TravelDetailActivity)getActivity()).getTravel();
		mPullToRefreshAttacher = ((TravelDetailActivity)getActivity()).getPullToRefreshAttacher();
		mListView = (ListView) view.findViewById(R.id.lv_travel_detail);
		mAdapter = new CommentAdapter(getActivity(), mListView, null);
		
		mListView.setAdapter(mAdapter);
		
	    mPullToRefreshAttacher.addRefreshableView(mListView, this);
	    AnimationAdapter animationAdapter = new CardsAnimationAdapter(mAdapter);
        animationAdapter.setAbsListView(mListView);
        mListView.setAdapter(animationAdapter);
        loadData();
		return view;
	}
	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		loadData();
	}
	private void loadData(){
		executeRequest((new GsonRequest<Comment.CommentRequestData>(AppData.HOST_IP+"travel/"+travel.getId()+"/comments",
				Comment.CommentRequestData.class, null,
                new Response.Listener<Comment.CommentRequestData>() {
                    @Override
                    public void onResponse(final Comment.CommentRequestData requestData) {
                        	    
                            	
                            	int rsp_code = requestData.getRsp_code();
                            	if(rsp_code == MyServerMessage.SUCCESS){
                            		 comments = requestData.getComments();
                            		 Log.i(TAG,comments.toString());
                            		 mAdapter.clearData();
                                     mAdapter.setData(comments);
                                     
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
}
