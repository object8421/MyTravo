package com.cobra.mytravo.activities;

import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.NotesDataHelper;
import com.cobra.mytravo.helpers.ActionBarUtils;
import com.cobra.mytravo.helpers.BitmapManager;
import com.cobra.mytravo.helpers.MyImageUtil;
import com.cobra.mytravo.helpers.TimeUtils;
import com.cobra.mytravo.models.Note;
import com.cobra.mytravo.models.Travel;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class NoteDetailActivity extends Activity {
	private final static String TAG = "NoteDetailActivity";
	private final static int EDIT_NOTE_REQUEST_CODE = 1;
	private static final String NOTE_STRING = "note";
	private BitmapManager bitmapManager;
	private Note note;
	private NotesDataHelper mDataHelper;
	private ImageView imageView;
	private TextView descriptionTextView;
	private TextView timeTextView;
	private TextView locationTextView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.activity_note_detail);
		InitialData();
		InitialViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.note_detail, menu);
		return true;
	}
	private void InitialData(){
		if(getIntent() != null){
			note = (Note) getIntent().getSerializableExtra(NOTE_STRING);
		}
		mDataHelper = new NotesDataHelper(this, AppData.getUserId());
		bitmapManager = new BitmapManager(this);
	}
	private void InitialViews(){
		ActionBarUtils.InitialActionBarWithBackAndTitle(this, getActionBar(), "足迹详情");
		imageView = (ImageView) findViewById(R.id.img_cover_note);
		descriptionTextView = (TextView) findViewById(R.id.tv_description_note);
		timeTextView = (TextView) findViewById(R.id.tv_time_note);
		locationTextView = (TextView) findViewById(R.id.tv_location_note);
		if(note.getImage_url() != null){
//			BitmapFactory.Options options = new BitmapFactory.Options();
//	        options.inSampleSize = 2;
			MyImageUtil.setBitmapResize(this, imageView, note.getImage_url());
			//bitmapManager.fetchBitmapOnThread(note.getImage_url(), imageView);
		}
		if(note.getContent() != null){
			descriptionTextView.setText(note.getContent());
		}
		if(note.getLocation() != null){
			locationTextView.setText(note.getLocation().getAddress());
		}
		timeTextView.setText(TimeUtils.getListTime(note.getCreate_time()));
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case android.R.id.home:
			this.onBackPressed();
			break;
		case R.id.action_edit:
			Intent intent = new Intent();
			intent.setClass(this, AddNoteActivity.class);		
			Bundle bundle = new Bundle();
			bundle.putSerializable(NOTE_STRING, note);
			intent.putExtras(bundle);
			startActivityForResult(intent, EDIT_NOTE_REQUEST_CODE);
			break;
		case R.id.action_delete:
			AlertDialog.Builder builder = new Builder(this);
			builder.setMessage("确认删除吗?");
			builder.setPositiveButton("删除", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					note.setIs_deleted(1);
					mDataHelper.update(note);
					NoteDetailActivity.this.finish();
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
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == EDIT_NOTE_REQUEST_CODE){
			if(resultCode == 1){
				Note editNote = (Note) data.getSerializableExtra(NOTE_STRING);
				if(editNote != null){
					if(editNote.getImage_url() != null){
						MyImageUtil.setBitmap(imageView, editNote.getImage_url());
					}
					if(editNote.getContent() != null)
						descriptionTextView.setText(editNote.getContent());
					
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
