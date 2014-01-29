package com.cobra.mytravo.fragments;


import com.cobra.mytravo.R;
import com.cobra.mytravo.activities.MyTravelEditActivity;
import com.cobra.mytravo.activities.TravelActivity;
import com.cobra.mytravo.adapters.MeTravelAdapter;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.TravelsDataHelper;
import com.cobra.mytravo.models.Travel;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MeFragment extends BaseFragment implements LoaderCallbacks<Cursor>{
	private static final String TRAVEL_STRING = "travel";
	private ListView mListView;
	private MeTravelAdapter mAdapter;
	private TravelsDataHelper mDataHelper;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_me, null);
		mListView = (ListView) view.findViewById(R.id.me_listview);
		mDataHelper = new TravelsDataHelper(getActivity(), 0);
		mAdapter = new MeTravelAdapter(getActivity(), mListView);
		mListView.setAdapter(mAdapter);
		getLoaderManager().initLoader(0, null, this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int postion,
					long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getActivity(), MyTravelEditActivity.class);
				Travel travel = mAdapter.getItem(postion - mListView.getHeaderViewsCount());
				Bundle bundle = new Bundle();
				bundle.putSerializable(TRAVEL_STRING, travel);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		return view;
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
