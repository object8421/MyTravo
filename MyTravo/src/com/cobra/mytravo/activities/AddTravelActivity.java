package com.cobra.mytravo.activities;

import java.util.Calendar;
import java.util.Date;

import com.cobra.mytravo.R;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.TravelsDataHelper;
import com.cobra.mytravo.helpers.ActionBarUtils;
import com.cobra.mytravo.helpers.TimeUtils;
import com.cobra.mytravo.models.MyHandlerMessage;
import com.cobra.mytravo.models.Travel;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class AddTravelActivity extends Activity {

	private Travel travel;
	
	private EditText edtTitle;
	private EditText edtExpense;
	private EditText edtDestination;
	private EditText edtDescription;
	private Button startButton;
	private Button endButton;
	private ProgressDialog progressDialog;
	private AddTravelThread addTravelThread;
	
	private String startString;
	private String endString;
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
			case MyHandlerMessage.SET_START_TIME_FINISH:
				
				break;
			case MyHandlerMessage.SET_END_TIME_FINISH:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_travel);
		ActionBarUtils.InitialDarkActionBar(this, getActionBar());
		
//		initalize the travelDataHelper
		/*
		 * begin
		 * old 
		 * mDataHelper = new TravelsDataHelper(this.getApplicationContext());
		 * new L!ar 2013/12/24
		 */
		mDataHelper = new TravelsDataHelper(this, 0);
		/*
		 * end
		 */
		edtTitle = (EditText) findViewById(R.id.travel_title);
		edtExpense = (EditText) findViewById(R.id.travel_expense);
		startButton = (Button) findViewById(R.id.btn_travel_start);
		startButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showTimeDialog(0);
			}
		});
		endButton = (Button) findViewById(R.id.btn_travel_end);
		endButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showTimeDialog(1);
			}
		});
		
		edtDescription = (EditText) findViewById(R.id.travel_description);
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
		
		
	}
	//show the time pick dialog, 0 for starting time and 1 for end time
	 protected void showTimeDialog(final int value) {
         // TODO Auto-generated method stub
         
         Calendar c = Calendar.getInstance() ;
         

         int year = c.get(Calendar.YEAR);

         int monthOfYear = c.get(Calendar.MONTH);

         int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

         DatePickerDialog date = new DatePickerDialog(
                         AddTravelActivity.this, new OnDateSetListener() {

                                 

                                 @Override
                                 public void onDateSet(DatePicker arg0, int year, int month,
                                                 int day) {
                                         // TODO Auto-generated method stub
                                	     
                     				     Calendar c = Calendar.getInstance();
                     				     c.set(year, month, day);
                     				     if(value == 0){
                     				    	startString = TimeUtils.getCalendarTime(c);
                     				    	startButton.setText(year + "-" + (month + 1) + "-"
                                                    + day);
                     				     }
                     				    	 
                     				     else {
											endString = TimeUtils.getCalendarTime(c);
											endButton.setText(year + "-" + (month + 1) + "-"
                                                    + day);
										}
                                         
                                        
                                 }

                         }, year, monthOfYear, dayOfMonth);

         date.show();
 }
	//check the entered info is correct
	private boolean checkInfo() {
		// TODO Auto-generated method stub
		if(edtTitle.getText().toString().isEmpty())
			return false;
//		if(edtExpense.getText().toString().isEmpty())
//			return false;
//		else
//		{
//			try
//			{
//				Double.parseDouble(edtExpense.getText().toString());
//			}
//			catch(NumberFormatException e)
//			{
//				return false;
//			}
//		}
		//if(edtDestination.getText().toString().isEmpty())
		//	return false;
		//if(edtDescription.getText().toString().isEmpty())
		//	return false;
		return true;
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
		if(item.getItemId() == R.id.action_add)
		{
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
		return super.onOptionsItemSelected(item);
	}
	
	private class AddTravelThread extends Thread {
		@Override
		public void run() {
				travel = new Travel();
				travel.setTitle(edtTitle.getText().toString());
				travel.setAverage_spend(Double.parseDouble(edtExpense.getText().toString()));
				Log.v("start", startString);
				Log.v("end", endString);
				if(startString != null)
					travel.setBegin_date(startString);
				if(endString != null)
					travel.setEnd_date(endString);
				
				travel.setDescription(edtDescription.getText().toString());
				
				travel.setCreated_time(new Date().toString());
				travel.setComment_qty(0);
				travel.setFavorite_qty(0);
				travel.setVote_qty(0);
				travel.setIs_deleted(0);
				travel.setIs_public(0);
				travel.setUser_id(0);
				
				mDataHelper.insert(travel);
				Message msg = new Message();
				msg.what = MyHandlerMessage.ADD_NEW_TRAVEL_SUCCESS;
				handler.sendMessage(msg);
		}
	
	}
}
