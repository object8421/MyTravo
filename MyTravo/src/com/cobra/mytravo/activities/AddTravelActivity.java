package com.cobra.mytravo.activities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.cobra.mytravo.R;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.MyHandlerMessage;
import com.cobra.mytravo.data.TravelsDataHelper;
import com.cobra.mytravo.helpers.ActionBarUtils;
import com.cobra.mytravo.helpers.MyImageUtil;
import com.cobra.mytravo.helpers.PhotoUtils;
import com.cobra.mytravo.helpers.TimeUtils;
import com.cobra.mytravo.models.Travel;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

@SuppressLint("NewApi") public class AddTravelActivity extends Activity implements OnMenuItemClickListener{
	private static final String TRAVEL_STRING = "travel";
	private static final String TAG = "AddTravelActivity";
	private Travel travel;
	private View backView;
	private EditText edtTitle;
	private EditText edtExpense;
	private EditText edtDestination;
	private EditText edtDescription;
	private Button startButton;
	private Button endButton;
	private ImageButton coverButton;
	private ProgressDialog progressDialog;
	private AddTravelThread addTravelThread;
	
	//mark if current activity is a edit travel activity
	private boolean isEdit = false;
	//mark if user add an image to the travel
	private boolean imageExist = false;
	//mark if th photo we pick is from camera
	private boolean isFromCamera = false;
	private String startString;
	private String endString;
	private String title;
	private Double expense;
	private Date begindate;
	private Date enddate;
	private String destination;
	private String description;
	private Bitmap coverBitmap;
	private String photoTimeString;
	private String coverPathString;
	private TravelsDataHelper mDataHelper;
	private Travel editTravel;
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
				if(isEdit){
					Intent resultIntent = new Intent();
					Bundle resultBundle = new Bundle();
					resultBundle.putSerializable(TRAVEL_STRING, editTravel);
					resultIntent.putExtras(resultBundle);
					setResult(1, resultIntent);
					Toast.makeText(AddTravelActivity.this, "修改游记成功", Toast.LENGTH_SHORT).show();
				}
				else{
					if(progressDialog != null && progressDialog.isShowing())
						progressDialog.dismiss();
					Toast.makeText(AddTravelActivity.this, "添加游记成功", Toast.LENGTH_SHORT).show();
				}
				
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
		ActionBarUtils.InitialDarkActionBar(this, getActionBar(), "添加游记");
		InitialData();
		InitialView();

		
		/*
		 * end
		 */
		
		
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
	 private void InitialData(){
		 if((getIntent() )!= null){
			 editTravel = (Travel) getIntent().getSerializableExtra(TRAVEL_STRING);
			 if(editTravel != null){
				 isEdit = true;
				 //get the edditedtravel's coverpathstring if exists
				 photoTimeString = editTravel.getCover_url();
			 }
		 }
			 
		 mDataHelper = new TravelsDataHelper(this, 0);
	 }
	 private void InitialView(){
		 	backView = findViewById(R.id.layout_travel_add);
			edtTitle = (EditText) findViewById(R.id.travel_title);
			edtExpense = (EditText) findViewById(R.id.travel_expense);
			startButton = (Button) findViewById(R.id.btn_travel_start);
			endButton = (Button) findViewById(R.id.btn_travel_end);
			coverButton = (ImageButton) findViewById(R.id.btn_cover_travel);
			edtDescription = (EditText) findViewById(R.id.travel_description);
			progressDialog = new ProgressDialog(AddTravelActivity.this);
			if(isEdit){
				if(editTravel.getCover_url() != null){
					imageExist = true;
					MyImageUtil.setBitmap(coverButton, editTravel.getCover_url());
				}
					
				edtTitle.setText(editTravel.getTitle());
				if(String.valueOf(editTravel.getAverage_spend()) != null)
					edtExpense.setText(String.valueOf(editTravel.getAverage_spend()));
				if(editTravel.getBegin_date() != null)
					startButton.setText(editTravel.getBegin_date());
				if(editTravel.getEnd_date() != null)
					endButton.setText(editTravel.getEnd_date());
				if(editTravel.getDescription() != null)
				edtDescription.setText(editTravel.getDescription());
			}
			startButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showTimeDialog(0);
				}
			});
			
			endButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showTimeDialog(1);
				}
			});
			
			coverButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					PopupMenu popupMenu = new PopupMenu(AddTravelActivity.this, view);
					MenuInflater inflater = popupMenu.getMenuInflater();
	                inflater.inflate(R.menu.photo,
	                		popupMenu.getMenu());
	                popupMenu.setOnMenuItemClickListener(AddTravelActivity.this);
	                popupMenu.show();
				}
			});
			
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
	 @SuppressLint("NewApi") @Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		 	if(resultCode != RESULT_OK){
		 		Log.v(TAG, "get photo error!");
		 		return;
		 	}
		 		
	        switch (requestCode) { 
	        
	        case 1:  
	            //if (data != null) {
	        	
	        	if(coverBitmap != null)
            		coverBitmap.recycle();
	        	Log.v(TAG, coverPathString);
	        	coverPathString = AppData.TRAVO_PATH + "/"+ coverPathString;
	        	BitmapFactory.Options options = new BitmapFactory.Options();
	            options.inSampleSize = 2;
	        	coverBitmap = BitmapFactory.decodeFile(coverPathString, options);
	            if(coverBitmap != null){
	            	 Log.v(TAG, "camera data is not null");
	            	isFromCamera = true;
	 	            imageExist = true;
	 	            coverButton.setImageBitmap(coverBitmap);
	 	            
	            }
	            else{
	            	 Log.v(TAG, "coverBitmap is null");
	            }
	            	
	                
	  
	            //}  
	            break;
	        case 2:
	        	Log.v(TAG, "gallery data is not null");
	        	isFromCamera = false;
	        	imageExist = true;
	        	coverPathString = null;
	        	Uri selectedImage = data.getData();  
	            String[] filePathColumn = { MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN};  
	            
	            Cursor cursor = getContentResolver().query(selectedImage,  
	                    filePathColumn, null, null, null);  
	            cursor.moveToFirst();  
	   
	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);  
	            String temp = cursor.getString(columnIndex);
	            photoTimeString = (String) TimeUtils.getPhotoTime(Long.parseLong(cursor.getString(cursor.getColumnIndex(filePathColumn[1]))));
	            Toast.makeText(this, photoTimeString, Toast.LENGTH_LONG).show();
	            cursor.close();  
	            
//	            BitmapFactory.Options options = new BitmapFactory.Options();
//	            options.inSampleSize = 2;
	            if(coverBitmap != null)
            		coverBitmap.recycle();
	            BitmapFactory.Options opt = new BitmapFactory.Options();
	            opt.inSampleSize = 2;
	            coverBitmap = BitmapFactory.decodeFile(temp, opt);
	            coverButton.setImageBitmap(coverBitmap);
	        	break;
	        default:  
	        	Toast.makeText(this, "!!!!!!", Toast.LENGTH_SHORT).show();
	            break;  
	  
	        }  
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
				
				Message msg = new Message();
				if(saveData())
					msg.what = MyHandlerMessage.ADD_NEW_TRAVEL_SUCCESS;
				else {
					msg.what = MyHandlerMessage.ADD_NEW_TRAVEL_FAIL;
				}
				handler.sendMessage(msg);
		}
	
	}
	public boolean saveData(){
		try{
			if(!isEdit){
				travel = new Travel();
				travel.setTitle(edtTitle.getText().toString());
				travel.setAverage_spend(Double.parseDouble(edtExpense.getText().toString()));
				
				if(startString != null)
					travel.setBegin_date(startString);
				if(endString != null)
					travel.setEnd_date(endString);
				
				travel.setDescription(edtDescription.getText().toString());
				travel.setCreated_time(new Date().toString());
				travel.setUser_id(0);
				if(imageExist){
					Log.v("image", "image exist!");
					if(coverPathString == null){
						if(photoTimeString != null){
							
								coverPathString = PhotoUtils.saveImage(photoTimeString, coverBitmap);
								if(coverPathString != null){
									Log.v("coverPath", coverPathString);
									travel.setCover_url(photoTimeString);
								}
								else{
									Log.v("coverPath", "null");
								}
							}
							
						
					}
					else{
						if(photoTimeString != null)
						travel.setCover_url(photoTimeString);
						
					}
				}
				mDataHelper.insert(travel);
				//only for testing, later we will move user_id to the moment 
				//we successfully login
				AppData.setUserId(0);
				AppData.setTravel_time(travel.getCreated_time());
			}
			else{
				editTravel.setTitle(edtTitle.getText().toString());
				editTravel.setAverage_spend(Double.parseDouble(edtExpense.getText().toString()));
				
				if(startString != null)
					editTravel.setBegin_date(startString);
				if(endString != null)
					editTravel.setEnd_date(endString);
				
				editTravel.setDescription(edtDescription.getText().toString());
				if(imageExist){
					//if is null that means we have changed the imageurl
					if(coverPathString == null){
						if(photoTimeString  != null){
							coverPathString = PhotoUtils.saveImage(photoTimeString, coverBitmap);
							if(coverPathString != null)
								Log.v("coverPath is:", coverPathString);
								editTravel.setCover_url(photoTimeString);
						}
					}
					else{
						travel.setCover_url(photoTimeString);
					}
				}
				Travel.clearCache();
				mDataHelper.update(editTravel);
			}
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
	@Override
	public boolean onMenuItemClick(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case R.id.camera:
			
			photoTimeString = (String) TimeUtils.getPhotoTime(System.currentTimeMillis());
			coverPathString = PhotoUtils.getPhotoPath(photoTimeString);
			if(coverPathString != null){
				Uri imageUri = PhotoUtils.getPhotoUri(coverPathString);
				Intent it = new Intent("android.media.action.IMAGE_CAPTURE");//跳转到相机Activity
				//it.setAction(MediaStore.ACTION_IMAGE_CAPTURE);  
				it.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);//告诉相机拍摄完毕输出图片到指定的Uri
				  startActivityForResult(it, 1);
			}
				
			return true;
		case R.id.gallery:
			PhotoUtils.selectPicture(AddTravelActivity.this);
			return true;
		default:
			return false;
		}
	}
	
}
