package com.cobra.mytravo.fragments;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.cobra.mytravo.R;
import com.cobra.mytravo.activities.MainActivity;
import com.cobra.mytravo.adapters.ShotsAdapter;
import com.cobra.mytravo.data.GsonRequest;
import com.cobra.mytravo.data.ShotsDataHelper;
import com.cobra.mytravo.helpers.CommonUtils;
import com.cobra.mytravo.helpers.ListViewUtils;
import com.cobra.mytravo.models.Category;
import com.cobra.mytravo.models.DribbbleApi;
import com.cobra.mytravo.models.Shot;
import com.cobra.mytravo.ui.LoadingFooter;


import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;





import android.content.res.Configuration;
import android.database.Cursor;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import android.app.LoaderManager.LoaderCallbacks;
import android.app.LoaderManager;
import android.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;


public class ShotsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,PullToRefreshAttacher.OnRefreshListener {
	private int mPage = 1;
	public static final String EXTRA_CATEGORY = "EXTRA_CATEGORY";
	private Category mCategory;
	private ShotsDataHelper mDataHelper;
	private MainActivity mActivity;
	private ListView mListView;
	private ShotsAdapter mAdapter;
	private ArrayList<String> items = new ArrayList<String>();
	private Handler mHandler;
	private int start = 0;
	private static int refreshCnt = 0;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private LoadingFooter mLoadingFooter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setRetainInstance(true);
	        mCategory = Category.popular;
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        
	        //inflater the layout 
	        View view = inflater.inflate(R.layout.fragment_shot, null);
	        
	        mListView = (ListView) view.findViewById(R.id.listView);
			//初始化数据库
	       Log.i("mycontext", getActivity().getApplicationContext().toString());
	       
			
	        mDataHelper = new ShotsDataHelper(getActivity().getApplicationContext(), mCategory);
			
			mAdapter = new ShotsAdapter(getActivity(), mListView);
			mListView.setAdapter(mAdapter);
			mHandler = new Handler();
			mPullToRefreshAttacher = ((MainActivity) getActivity()).getPullToRefreshAttacher();
		    mPullToRefreshAttacher.addRefreshableView(mListView, this);
	        mLoadingFooter = new LoadingFooter(getActivity());
	        mListView.addFooterView(mLoadingFooter.getView());
	        
	        getLoaderManager().initLoader(0, null, this);
	        mActivity = (MainActivity) getActivity();
	        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
	            @Override
	            public void onScrollStateChanged(AbsListView view, int scrollState) {

	            }

	            @Override
	            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
	                    int totalItemCount) {
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
	        return view;
	}
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
       
    }

    private void parseArgument() {
        Bundle bundle = getArguments();
        mCategory = Category.valueOf(bundle.getString(EXTRA_CATEGORY));
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mDataHelper.getCursorLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
        if (data != null && data.getCount() == 0) {
            loadFirstPage();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    private void loadData(final int page) {
        final boolean isRefreshFromTop = page == 1;
        if (!mPullToRefreshAttacher.isRefreshing() && isRefreshFromTop) {
            mPullToRefreshAttacher.setRefreshing(true);
           
        }
        executeRequest(new GsonRequest<Shot.ShotsRequestData>(String.format(DribbbleApi.SHOTS_LIST,
        		mCategory.name(), page), Shot.ShotsRequestData.class, null,
                new Response.Listener<Shot.ShotsRequestData>() {
                    @Override
                    public void onResponse(final Shot.ShotsRequestData requestData) {
                        CommonUtils.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
                            @Override
                            protected Object doInBackground(Object... params) {
                                mPage = requestData.getPage();
                                if (mPage == 1) {
                                    mDataHelper.deleteAll();
                                }
                                ArrayList<Shot> shots = requestData.getShots();
                                mDataHelper.bulkInsert(shots);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Object o) {
                                super.onPostExecute(o);
                                if (isRefreshFromTop) {
                                    mPullToRefreshAttacher.setRefreshComplete();
                                } else {
                                    mLoadingFooter.setState(LoadingFooter.State.Idle, 3000);
                                }
                            }
                        });
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), R.string.refresh_list_failed,
                                Toast.LENGTH_SHORT).show();
                        if (isRefreshFromTop) {
                            mPullToRefreshAttacher.setRefreshComplete();
                        } else {
                            mLoadingFooter.setState(LoadingFooter.State.Idle, 3000);
                        }
                    }
                }));
    }

    private void loadNextPage() {
        mLoadingFooter.setState(LoadingFooter.State.Loading);
        loadData(mPage + 1);
    }

    private void loadFirstPage() {
        loadData(1);
    }

    public void loadFirstPageAndScrollToTop() {
        ListViewUtils.smoothScrollListViewToTop(mListView);
        loadFirstPage();
    }

    @Override
    public void onRefreshStarted(View view) {
        loadFirstPage();
    }
	
	
	
}
