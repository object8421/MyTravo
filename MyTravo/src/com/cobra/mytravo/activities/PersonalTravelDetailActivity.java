package com.cobra.mytravo.activities;

import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.adapters.TravelDetailAdapter;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.NotesDataHelper;
import com.cobra.mytravo.helpers.ActionBarUtils;
import com.cobra.mytravo.helpers.TimeUtils;
import com.cobra.mytravo.models.Note;
import com.cobra.mytravo.models.Travel;

import android.os.Bundle;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PersonalTravelDetailActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{
	private static final String TRAVEL_STRING = "travel";
	private TravelDetailAdapter mAdapter;
	private Travel travel;
	private NotesDataHelper mDataHelper;
	
	private ImageView travelImageView;
	private TextView titleTextView;
	private TextView timeTextView;
	private TextView descriptionTextView;
	private ListView mListView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_travel_detail);
		ActionBarUtils.InitialDarkActionBar(this, getActionBar());
		InitialData();
		InitialView();
		mDataHelper = new NotesDataHelper(this, AppData.getUserId(), travel.getCreated_time());
		mAdapter = new TravelDetailAdapter(this, mListView);
		mListView.setAdapter(mAdapter);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.travel_detail, menu);
		return true;
	}
	private void InitialView(){
		travelImageView = (ImageView) findViewById(R.id.img_travel_detail);
		titleTextView = (TextView) findViewById(R.id.tv_title_travel_detail);
		timeTextView = (TextView) findViewById(R.id.tv_time_travel_detail);
		descriptionTextView = (TextView) findViewById(R.id.tv_description_travel_detail);
		mListView = (ListView) findViewById(R.id.lv_travel_detail);
		titleTextView.setText(travel.getTitle());
		timeTextView.setText(TimeUtils.getListTime(travel.getCreated_time()));
		descriptionTextView.setText(travel.getDescription());
	}
	private void InitialData(){
		Intent intent = getIntent();
		if(intent != null){
			travel = (Travel) intent.getSerializableExtra(TRAVEL_STRING);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return mDataHelper.getCursorLoader();
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
		// TODO Auto-generated method stub
		mAdapter.changeCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		mAdapter.changeCursor(null);
	}
}
