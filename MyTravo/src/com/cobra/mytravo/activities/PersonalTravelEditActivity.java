package com.cobra.mytravo.activities;

import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.MyHandlerMessage;
import com.cobra.mytravo.data.TravelsDataHelper;
import com.cobra.mytravo.helpers.ActionBarUtils;
import com.cobra.mytravo.helpers.TimeUtils;
import com.cobra.mytravo.internet.UploadTravelCover;
import com.cobra.mytravo.models.Travel;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 
 * @author L!ar
 * 
 *         Activity
 */
public class PersonalTravelEditActivity extends Activity
{
	private static final String TRAVEL_STRING = "travel";
	private static final int SAVE_SUCCESS = 0;
	private static final int SAVE_FAIL = 1;
	private static final int DELETE_SUCCESS = 2;
	private static final int DELETE_FAIL = 3;
	private Travel travel, editedTravel;
	private EditText titleEditText, expensEditText, descriptionEditText;
	private Button startButton, endButton;
	private String title, expense, description, start, end;
	private String editedTitle, editedExpense, editedDescription, editedStart,
			editedEnd;
	private TravelsDataHelper mDataHelper;
	private SaveThread saveThread;
	private DeleteThread deleteThread;
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case SAVE_SUCCESS:
				Toast.makeText(PersonalTravelEditActivity.this, "保存成功",
						Toast.LENGTH_SHORT).show();

				Intent intent = new Intent(PersonalTravelEditActivity.this,
						UploadTravelCover.class);
				intent.putExtra("type", "dirty");
				startService(intent);

				Intent resultIntent = new Intent();
				Bundle resultBundle = new Bundle();
				resultBundle.putSerializable(TRAVEL_STRING, editedTravel);
				resultIntent.putExtras(resultBundle);
				setResult(1, resultIntent);

				PersonalTravelEditActivity.this.finish();
				break;
			case DELETE_SUCCESS:
				Toast.makeText(PersonalTravelEditActivity.this, "删除成功",
						Toast.LENGTH_SHORT).show();
				PersonalTravelEditActivity.this.finish();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_travel);
		// Initial the actionBar especially the split actionBar,
		// 2014/1/30 by L!ar@Fuzhou , I hope this year I can make a girl friend!
		ActionBarUtils.InitialDarkActionBar(this, getActionBar());
		titleEditText = (EditText) findViewById(R.id.travel_title);
		expensEditText = (EditText) findViewById(R.id.travel_expense);
		descriptionEditText = (EditText) findViewById(R.id.travel_description);
		startButton = (Button) findViewById(R.id.btn_travel_start);
		endButton = (Button) findViewById(R.id.btn_travel_end);
		mDataHelper = new TravelsDataHelper(this, AppData.getUserId());
		initialData();
	}

	public void initialData()
	{
		Intent intent = getIntent();
		if ((travel = (Travel) intent.getSerializableExtra(TRAVEL_STRING)) != null)
		{
			titleEditText.setText(travel.getTitle());
			if ((expense = String.valueOf(travel.getAverage_spend())) != null)
				expensEditText.setText(expense);
			if ((description = (travel.getDescription())) != null)
				descriptionEditText.setText(description);
			if ((start = (travel.getBegin_date())) != null)
				startButton.setText(start);
			if ((end = (travel.getEnd_date())) != null)
				endButton.setText(end);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_travel_edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case android.R.id.home:
			this.onBackPressed();
			break;
		case R.id.action_save:
			if (saveThread == null)
			{
				saveThread = new SaveThread();
				saveThread.start();
			}
			break;
		case R.id.action_delete:
			if (deleteThread == null)
			{
				deleteThread = new DeleteThread();
				deleteThread.start();
			}
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	class SaveThread extends Thread
	{
		@Override
		public void run()
		{

			// notice that travel's created time isn't change at here
			editedTravel = travel;
			editedTravel.setIs_sync(1);
			if ((editedTitle = titleEditText.getText().toString().trim()) != null)
				editedTravel.setTitle(editedTitle);
			if ((editedExpense = expensEditText.getText().toString().trim()) != null)
				editedTravel.setAverage_spend(Double.parseDouble(editedExpense));
			if ((editedDescription = descriptionEditText.getText().toString().trim()) != null)
				editedTravel.setDescription(editedDescription);
			if ((editedStart = startButton.getText().toString().trim()) != null)
				editedTravel.setBegin_date(editedStart);
			if ((editedEnd = endButton.getText().toString().trim()) != null)
				editedTravel.setEnd_date(editedEnd);
			// clear the cache
			Travel.clearCache();
			Log.i("editedtravel id",String.valueOf(editedTravel.getId()));
			int result = mDataHelper.update(editedTravel);
			Log.i("result", String.valueOf(result));
			Message msg = new Message();
			msg.what = SAVE_SUCCESS;
			mHandler.sendMessage(msg);
		}
	}

	class DeleteThread extends Thread
	{
		@Override
		public void run()
		{
			travel.setIs_deleted(1);
			mDataHelper.delete(travel);
			Message msg = new Message();
			msg.what = DELETE_SUCCESS;
			mHandler.sendMessage(msg);
		}
	}
}
