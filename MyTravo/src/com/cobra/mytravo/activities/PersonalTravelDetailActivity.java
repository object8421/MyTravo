package com.cobra.mytravo.activities;

import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.adapters.TravelDetailAdapter;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.DataProvider;
import com.cobra.mytravo.data.NotesDataHelper;
import com.cobra.mytravo.helpers.ActionBarUtils;
import com.cobra.mytravo.helpers.MyImageUtil;
import com.cobra.mytravo.helpers.TimeUtils;
import com.cobra.mytravo.models.Note;
import com.cobra.mytravo.models.Travel;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Toast;
/**
 * 
 * @author L!ar
 * Activity shows a detail infomation about the current user's travel
 * (including the list of notes related to the travel)
 *
 */
public class PersonalTravelDetailActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{
	private static final String TRAVEL_STRING = "travel";
	private static final String NOTE_STRING = "note";
	private static final int EDIT_TRAVEL_REQUEST_CODE = 1;
	private TravelDetailAdapter mAdapter;
	private Travel travel;
	//show the number of notes
	private int noteCount = 0;
	private NotesDataHelper mDataHelper;
	private SQLiteDatabase db;
	private View headerView;
	private ImageView travelImageView;
	private TextView titleTextView;
	private TextView timeTextView;
	private TextView descriptionTextView;
	private TextView noteCountTextView;
	private ListView mListView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_travel_detail);
		ActionBarUtils.InitialActionBarWithBackAndTitle(this, getActionBar(), "游记详情");
		InitialData();
		InitialView();
		mAdapter = new TravelDetailAdapter(this, mListView);
		mListView.addHeaderView(headerView);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Note note = mAdapter.getItem(position - mListView.getHeaderViewsCount());
				if(note != null){
					Intent intent = new Intent();
					intent.setClass(PersonalTravelDetailActivity.this, NoteDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(NOTE_STRING, note);
					intent.putExtras(bundle);
					startActivity(intent);
				}
				
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
		headerView = LayoutInflater.from(this).inflate(R.layout.listitem_head_travel, null);
		travelImageView = (ImageView) headerView.findViewById(R.id.img_travel_detail);
		titleTextView = (TextView) headerView.findViewById(R.id.tv_title_travel_detail);
		timeTextView = (TextView) headerView.findViewById(R.id.tv_time_travel_detail);
		descriptionTextView = (TextView) headerView.findViewById(R.id.tv_description_travel_detail);
		noteCountTextView = (TextView) headerView.findViewById(R.id.tv_note_count_travel_detail);
		mListView = (ListView) findViewById(R.id.lv_travel_detail);
		if(travel.getCover_url() != null){
//			BitmapFactory.Options options = new BitmapFactory.Options();
//	        options.inSampleSize = 2;
//			travelImageView.setImageBitmap(BitmapFactory.decodeFile(AppData.TRAVO_PATH + "/" + travel.getCover_url() + ".jpg", options));
			MyImageUtil.setBitmapResize(this, travelImageView, travel.getCover_url());
		}
			
		titleTextView.setText(travel.getTitle());
		timeTextView.setText(TimeUtils.getListTime(travel.getCreated_time()));
		descriptionTextView.setText(travel.getDescription());
		if(noteCount > 0){
			noteCountTextView.setText(String.valueOf(noteCount));
		}
		else{
			noteCountTextView.setText("0");
		}
	}
	private void InitialData(){
		Intent intent = getIntent();
		if(intent != null){
			travel = (Travel) intent.getSerializableExtra(TRAVEL_STRING);
		}
		mDataHelper = new NotesDataHelper(this, AppData.getUserId(), travel.getCreated_time());
		noteCount = mDataHelper.getCount();
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
		if(noteCountTextView != null){
			noteCount = mDataHelper.getCount();
			if(noteCount > 0){
				noteCountTextView.setText(String.valueOf(noteCount));
			}
			else{
				noteCountTextView.setText("0");
			}
		}
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
		case android.R.id.home:
			this.onBackPressed();
			break;
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
		case R.id.action_finish:
			
			AlertDialog.Builder builder = new Builder(this);
			builder.setMessage("确认结束该游记吗?");
			builder.setPositiveButton("确认", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					AppData.setTravel_time(null);
					PersonalTravelDetailActivity.this.finish();
				}
			});
			builder.setNegativeButton("取消", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			builder.create().show();
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
						MyImageUtil.setBitmapResize(this, travelImageView, editedTravel.getCover_url());
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
