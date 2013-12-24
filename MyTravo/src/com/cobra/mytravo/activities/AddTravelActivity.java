package com.cobra.mytravo.activities;

import java.util.Date;

import com.cobra.mytravo.R;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.TravelsDataHelper;
import com.cobra.mytravo.models.MyHandlerMessage;
import com.cobra.mytravo.models.Travel;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddTravelActivity extends Activity {

	private Travel travel;
	
	private EditText edtTitle;
	private EditText edtExpense;
	private EditText edtBegindate;
	private EditText edtEnddate;
	private EditText edtDestination;
	private EditText edtDescription;
	private Button btnSave;
	private Button btnCancel;
	
	private ProgressDialog progressDialog;
	private AddTravelThread addTravelThread;
	
	private String title;
	private Double expense;
	private Date begindate;
	private Date enddate;
	private String destination;
	private String description;
	
	private TravelsDataHelper mDataHelper;
	
	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(android.os.Message msg) 
		{
			super.handleMessage(msg);
			progressDialog.dismiss();
			switch(msg.what)
			{
			case MyHandlerMessage.ADD_NEW_TRAVEL_SUCCESS:
				Toast.makeText(AddTravelActivity.this, "添加游记成功", Toast.LENGTH_SHORT).show();
				AddTravelActivity.this.finish();
				break;
			case MyHandlerMessage.ADD_NEW_TRAVEL_FAIL:
				Toast.makeText(AddTravelActivity.this, "Oops! 游记未添加成功", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_travel);
		
//		initalize the travelDataHelper
		mDataHelper = new TravelsDataHelper(this.getApplicationContext());
		
		edtTitle = (EditText) findViewById(R.id.travel_title);
		edtExpense = (EditText) findViewById(R.id.travel_expense);
		edtBegindate = (EditText) findViewById(R.id.travel_starttime);
		edtEnddate = (EditText) findViewById(R.id.travel_endtime);
		edtDestination = (EditText) findViewById(R.id.travel_destination);
		edtDescription = (EditText) findViewById(R.id.travel_description);
		btnSave = (Button) findViewById(R.id.travel_save);
		btnCancel = (Button) findViewById(R.id.travel_cancel);
		
		progressDialog = new ProgressDialog(AddTravelActivity.this);
		progressDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				if(addTravelThread != null && addTravelThread.isAlive())
				{
					addTravelThread.interrupt();
				}
			}
		});
		
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(checkInfo())
				{
					progressDialog = ProgressDialog.show(AddTravelActivity.this, "请等待", "添加游记中。。。", true, true);
					addTravelThread = new AddTravelThread();
					addTravelThread.start();
				}
				else
				{
					Toast.makeText(AddTravelActivity.this, "输入不正确", Toast.LENGTH_SHORT).show();
				}
			}
			
			private boolean checkInfo() {
				// TODO Auto-generated method stub
				if(edtTitle.getText().toString().isEmpty())
					return false;
				if(edtExpense.getText().toString().isEmpty())
					return false;
				else
				{
					try
					{
						Double.parseDouble(edtExpense.getText().toString());
					}
					catch(NumberFormatException e)
					{
						return false;
					}
				}
				if(edtBegindate.getText().toString().isEmpty())
					return false;
				if(edtEnddate.getText().toString().isEmpty())
					return false;
				if(edtDestination.getText().toString().isEmpty())
					return false;
				if(edtDescription.getText().toString().isEmpty())
					return false;
				return true;
			}
		});
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_travel, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId() == R.id.add_note)
		{
			Intent intent = new Intent(this, AddNoteActivity.class);
			
			this.startActivity(intent);	
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class AddTravelThread extends Thread {
		@Override
		public void run() {
				travel = new Travel();
				travel.setTitle(edtTitle.getText().toString());
				travel.setAverage_spend(Double.parseDouble(edtExpense.getText().toString()));
				travel.setBegin_date(edtBegindate.getText().toString());
				travel.setEnd_date(edtEnddate.getText().toString());
				travel.setDestination(edtDestination.getText().toString());
				travel.setDescription(edtDescription.getText().toString());
				
				travel.setCreated_time(new Date().toString());
				travel.setComment_qty(0);
				travel.setFavorite_qty(0);
				travel.setVote_qty(0);
				travel.setIs_deleted(false);
				travel.setIs_public(false);
				travel.setUser_id(0);
				
				mDataHelper.insert(travel);
				Message msg = new Message();
				msg.what = MyHandlerMessage.ADD_NEW_TRAVEL_SUCCESS;
				handler.sendMessage(msg);
		}
	
	}
}
