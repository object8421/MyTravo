package com.cobra.mytravo.activities;

import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.adapters.TravelDetailAdapter;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.NotesDataHelper;
import com.cobra.mytravo.helpers.ActionBarUtils;
import com.cobra.mytravo.helpers.MyImageUtil;
import com.cobra.mytravo.helpers.TimeUtils;
import com.cobra.mytravo.models.Note;
import com.cobra.mytravo.models.Travel;

import android.os.Bundle;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PersonalTravelDetailActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{
	private static final String TRAVEL_STRING = "travel";
	private static final String NOTE_STRING = "note";
	private static final int EDIT_TRAVEL_REQUEST_CODE = 1;
	private TravelDetailAdapter mAdapter;
	private Travel travel;
	private NotesDataHelper mDataHelper;
	
	private View headerView;
	private ImageView travelImageView;
	private TextView titleTextView;
	private TextView timeTextView;
	private TextView descriptionTextView;
	private ListView mListView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_travel_detail);
		ActionBarUtils.InitialDarkActionBar(this, getActionBar(), "游记详情");
		InitialData();
		InitialView();
		mDataHelper = new NotesDataHelper(this, AppData.getUserId(), travel.getCreated_time());
		mAdapter = new TravelDetailAdapter(this, mListView);
		mListView.addHeaderView(headerView);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Note note = mAdapter.getItem(position - mListView.getHeaderViewsCount());
				Intent intent = new Intent();
				intent.setClass(PersonalTravelDetailActivity.this, NoteDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(NOTE_STRING, note);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.travel_detail, menu);
		return true;
	}
	private void InitialView(){
		headerView = LayoutInflater.from(this).inflate(R.layout.listitem_head_note, null);
		travelImageView = (ImageView) headerView.findViewById(R.id.img_travel_detail);
		titleTextView = (TextView) headerView.findViewById(R.id.tv_title_travel_detail);
		timeTextView = (TextView) headerView.findViewById(R.id.tv_time_travel_detail);
		descriptionTextView = (TextView) headerView.findViewById(R.id.tv_description_travel_detail);
		mListView = (ListView) findViewById(R.id.lv_travel_detail);
		if(travel.getCover_url() != null){
			BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inSampleSize = 2;
			travelImageView.setImageBitmap(BitmapFactory.decodeFile(AppData.TRAVO_PATH + "/" + travel.getCover_url() + ".jpg", options));
		}
			
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case R.id.action_add:
			Intent addNoteIntent = new Intent();
			addNoteIntent.setClass(this, AddNoteActivity.class);
			startActivity(addNoteIntent);
			break;
		case R.id.action_edit:
			Intent intent = new Intent();
			intent.setClass(this, AddTravelActivity.class);		
			Bundle bundle = new Bundle();
			bundle.putSerializable(TRAVEL_STRING, travel);
			intent.putExtras(bundle);
			startActivityForResult(intent, EDIT_TRAVEL_REQUEST_CODE);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == EDIT_TRAVEL_REQUEST_CODE){
			if(resultCode == 1){
				Travel editedTravel = (Travel) data.getSerializableExtra(TRAVEL_STRING);
				if(editedTravel != null){
					if(editedTravel.getCover_url() != null){
						MyImageUtil.setBitmap(travelImageView, editedTravel.getCover_url());
					}
					titleTextView.setText(editedTravel.getTitle());
					timeTextView.setText(TimeUtils.getListTime(editedTravel.getCreated_time()));
					descriptionTextView.setText(editedTravel.getDescription());
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
