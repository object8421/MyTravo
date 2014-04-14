package com.cobra.mytravo.fragments;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.cobra.mytravo.R;
import com.cobra.mytravo.activities.MainActivity;
import com.cobra.mytravo.activities.TravelDetailActivity;
import com.cobra.mytravo.adapters.CardsAnimationAdapter;
import com.cobra.mytravo.adapters.HotTravelAdapter;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.GsonRequest;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.models.Travel;
import com.cobra.mytravo.ui.LoadingFooter;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;

public class FavoriteFragment extends BaseFragment implements PullToRefreshAttacher.OnRefreshListener{
	private String TAG = "favoritefragment";
	private MainActivity mActivity;
	private ListView mListView;
	private HotTravelAdapter mAdapter;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private ArrayList<Travel> travels;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_shot, null);
		mActivity = (MainActivity) getActivity();
		mListView = (ListView) view.findViewById(R.id.listView);
		mAdapter = new HotTravelAdapter(mActivity, mListView, null);
        mListView.setAdapter(mAdapter);
        mPullToRefreshAttacher = ((MainActivity) getActivity()).getPullToRefreshAttacher();
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
					Intent intent = new Intent(mActivity, TravelDetailActivity.class);
					intent.putExtra("travel", travel);
					startActivity(intent);
				}
			}
		});
        getMyFavorite();
		return view;
	}
	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		getMyFavorite();
	}
	private void getMyFavorite(){
		if(!mPullToRefreshAttacher.isRefreshing()){
			mPullToRefreshAttacher.setRefreshing(true);
		}
		executeRequest(new GsonRequest<Travel.TravelsRequestData>(AppData.HOST_IP+"travel/favorit?token="+AppData.getIdToken(),
        		Travel.TravelsRequestData.class, null,
                new Response.Listener<Travel.TravelsRequestData>() {
                    @Override
                    public void onResponse(final Travel.TravelsRequestData requestData) {
                        	    
                            	
                            	int rsp_code = requestData.getRsp_code();
                                travels = requestData.getTravels();
                                

                                //mAdapter = new HotTravelAdapter(mActivity, mListView, travels);
                                //mListView.setAdapter(mAdapter);
                                if(rsp_code == MyServerMessage.SUCCESS){
                                    Log.i(TAG,travels.toString());
                                	mAdapter.clearData();
                                    mAdapter.setData(travels);
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
                }));
	}
}
