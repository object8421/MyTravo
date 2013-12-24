package com.cobra.mytravo.activities;

import com.cobra.mytravo.R;
import com.cobra.mytravo.adapters.TravelsAdapter;
import com.cobra.mytravo.data.TravelsDataHelper;

import android.os.Bundle;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.database.Cursor;
import android.view.Menu;
import android.widget.ListView;

public class TravelActivity extends Activity implements LoaderCallbacks<Cursor> {
	
	private TravelsDataHelper mDataHelper;
	private TravelsAdapter mAdapter;
	private ListView mListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_travel);
		
		mListView = (ListView)findViewById(R.id.travels);
		mDataHelper = new TravelsDataHelper(this);
		mAdapter = new TravelsAdapter(this, mListView);
		mListView.setAdapter(mAdapter);
		
		getLoaderManager().initLoader(0, null, this);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.travel, menu);
		return true;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return mDataHelper.getCursorLoader();
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.changeCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		mAdapter.changeCursor(null);
	}

}
