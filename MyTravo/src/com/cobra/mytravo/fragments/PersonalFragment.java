package com.cobra.mytravo.fragments;


import java.util.Dictionary;
import java.util.Hashtable;

import com.cobra.mytravo.R;
import com.cobra.mytravo.activities.MainActivity;
import com.cobra.mytravo.activities.PersonalTravelDetailActivity;
import com.cobra.mytravo.activities.PersonalTravelEditActivity;
import com.cobra.mytravo.adapters.MeTravelAdapter;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.TravelsDataHelper;
import com.cobra.mytravo.models.Travel;
import com.cobra.mytravo.util.ComposeBtnUtil;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
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

public class PersonalFragment extends BaseFragment implements LoaderCallbacks<Cursor>{
	private static final int DISTANCE_SENSOR = 10;
	private Dictionary<Integer, Integer> listViewItemHeights = new Hashtable<Integer, Integer>();
	private static final String TRAVEL_STRING = "travel";
	private static final String TAG = "PersonalFragment";
	private ListView mListView;
	private MeTravelAdapter mAdapter;
	private TravelsDataHelper mDataHelper;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_me, null);
		mListView = (ListView) view.findViewById(R.id.me_listview);
		
		
		mDataHelper = new TravelsDataHelper(getActivity(), AppData.getUserId());
		mAdapter = new MeTravelAdapter(getActivity(), mListView);
		mListView.setAdapter(mAdapter);
		getLoaderManager().initLoader(0, null, this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int postion,
					long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getActivity(), PersonalTravelDetailActivity.class);
				Travel travel = mAdapter.getItem(postion - mListView.getHeaderViewsCount());
				Bundle bundle = new Bundle();
				bundle.putSerializable(TRAVEL_STRING, travel);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		mListView.setOnScrollListener(new OnScrollListener() {
			int previous = 0;
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				int currentY = getScroll();
				int direction = currentY - previous;
				if(direction > DISTANCE_SENSOR){
					Log.v(TAG, "runOutAnimation");
					//ComposeBtnUtil.runInAnimation(composeButton)
					ComposeBtnUtil.runOutAnimation(((MainActivity)getActivity()).getComposeButton());
				}
				else if(direction < (-DISTANCE_SENSOR)){
					Log.v(TAG, "runInAnimation");
					ComposeBtnUtil.runInAnimation(((MainActivity)getActivity()).getComposeButton());
				}
				previous = currentY;
			}
		});
		return view;
	}
	private int getScroll() {
	    View c = mListView.getChildAt(0); //this is the first visible row
	    try{
		    int scrollY = -c.getTop();
		    listViewItemHeights.put(mListView.getFirstVisiblePosition(), c.getHeight());
		    for (int i = 0; i < mListView.getFirstVisiblePosition(); ++i) {
		        if (listViewItemHeights.get(i) != null) // (this is a sanity check)
		            scrollY += listViewItemHeights.get(i); //add all heights of the views that are gone
		      
		    }
		    return scrollY;
	    }
	    catch(NullPointerException e){
	    	return 0;
	    }
	    
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
		((MainActivity)getActivity()).getComposeButton().setVisibility(View.VISIBLE);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		return mDataHelper.getCursorLoader();
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.changeCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.changeCursor(null);
	}
}
