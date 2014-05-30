package com.cobra.mytravo.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cobra.mytravo.R;
import com.cobra.mytravo.activities.MainActivity;
import com.cobra.mytravo.activities.TravelDetailActivity;
import com.cobra.mytravo.adapters.CardsAnimationAdapter;
import com.cobra.mytravo.adapters.HotTravelAdapter;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.GsonRequest;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.helpers.CommonUtils;
import com.cobra.mytravo.helpers.ListViewUtils;
import com.cobra.mytravo.internet.GetMyTravelsPicture;
import com.cobra.mytravo.internet.SearchTravelService;
import com.cobra.mytravo.models.Travel;
import com.cobra.mytravo.ui.LoadingFooter;
import com.cobra.mytravo.util.ComposeBtnUtil;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;




import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

public class HotTravelFragment extends BaseFragment implements PullToRefreshAttacher.OnRefreshListener{
	private final static String TAG = "hottravelfragment";
	private final static String TYPE = "type";
	private int mPage = 1;
	private int rsp_code = 0;
	private MainActivity mActivity;
	private ListView mListView;
	private String searchType;
	private HotTravelAdapter mAdapter;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private LoadingFooter mLoadingFooter;
	ArrayList<Travel> travels = new ArrayList<Travel>();
	private int index = 0;
	public static HotTravelFragment newInstance(String searchType) {
        HotTravelFragment fragment = new HotTravelFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TYPE, searchType);
        fragment.setArguments(bundle);
        return fragment;
    }
	private void parseArgument() {
        Bundle bundle = getArguments();
        searchType = bundle.getString(TYPE,"default");
    }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//inflater the layout 
        View view = inflater.inflate(R.layout.fragment_shot, null);  
        mActivity = (MainActivity) getActivity();
        mListView = (ListView) view.findViewById(R.id.listView);
        mAdapter = new HotTravelAdapter(mActivity, mListView, null);
        mListView.setAdapter(mAdapter);
        mPullToRefreshAttacher = ((MainActivity) getActivity()).getPullToRefreshAttacher();
	    mPullToRefreshAttacher.addRefreshableView(mListView, this);
	    mLoadingFooter = new LoadingFooter(getActivity());
	    mListView.addFooterView(mLoadingFooter.getView());
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
					mActivity.overridePendingTransition(R.anim.anim_bottom_to_top_in, android.R.anim.fade_out);
				}
			}
		});
	    mListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				switch(scrollState){
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					index = view.getLastVisiblePosition();
					break;
				case OnScrollListener.SCROLL_STATE_IDLE:
					int newIndex = view.getLastVisiblePosition();
					if(newIndex > index){
						ComposeBtnUtil.runOutAnimation(((MainActivity)getActivity()).getComposeButton());
					}
					else{
						ComposeBtnUtil.runInAnimation(((MainActivity)getActivity()).getComposeButton());
					}
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				if (mLoadingFooter.getState() == LoadingFooter.State.Loading
                        || mLoadingFooter.getState() == LoadingFooter.State.TheEnd) {
                    return;
                }
                if (firstVisibleItem + visibleItemCount >= totalItemCount
                        && totalItemCount != 0
                        && totalItemCount != mListView.getHeaderViewsCount()
                                + mListView.getFooterViewsCount() && mAdapter.getCount() > 0) {
                    loadNextPage();
                }
			}
		});
	    parseArgument();
	    loadFirstPage();
		return view;
	}
	private void loadData(final int page){
		Log.i(TAG,String.valueOf(page));
		final boolean isRefreshFromTop = page == 1;
		if (!mPullToRefreshAttacher.isRefreshing()&&isRefreshFromTop) {
            mPullToRefreshAttacher.setRefreshing(true);
           
        }
		
		executeRequest(new GsonRequest<Travel.TravelsRequestData>(String.format(AppData.HOST_IP+"travel/search?order=%1$s&first_idx=%2$d&max_qty=%3$d",
        		searchType,(page-1)*10+1,10*page), Travel.TravelsRequestData.class, null,
                new Response.Listener<Travel.TravelsRequestData>() {
                    @Override
                    public void onResponse(final Travel.TravelsRequestData requestData) {
                        	    
                            	
                            	rsp_code = requestData.getRsp_code();
                                travels = requestData.getTravels();
                                
                                Log.i(TAG,travels.toString());
                                //mAdapter = new HotTravelAdapter(mActivity, mListView, travels);
                                //mListView.setAdapter(mAdapter);
                                if(page == 1)
                                	mAdapter.clearData();
                                if(rsp_code == MyServerMessage.SUCCESS && travels.size() > 0)
                                	mPage++;
                                mAdapter.setData(travels);
                                if (isRefreshFromTop) {
                                    mPullToRefreshAttacher.setRefreshComplete();
                                } 
                                else {
                                    mLoadingFooter.setState(LoadingFooter.State.Idle, 3000);
                                }
                            
                     
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    	 Toast.makeText(getActivity(), R.string.refresh_list_failed,
                                 Toast.LENGTH_SHORT).show();
                         if (isRefreshFromTop) {
                             mPullToRefreshAttacher.setRefreshComplete();
                         } 
                         else {
                             mLoadingFooter.setState(LoadingFooter.State.Idle, 3000);
                         }
                    }
                }));
	}
	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		loadFirstPage();
	}
	private void loadNextPage() {
        mLoadingFooter.setState(LoadingFooter.State.Loading);
        loadData(mPage);
    }

    private void loadFirstPage() {
        loadData(1);
    }

    public void loadFirstPageAndScrollToTop() {
        ListViewUtils.smoothScrollListViewToTop(mListView);
        loadFirstPage();
    }

   
}
