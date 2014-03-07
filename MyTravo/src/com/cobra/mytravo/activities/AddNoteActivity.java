package com.cobra.mytravo.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.MyHandlerMessage;
import com.cobra.mytravo.data.NotesDataHelper;
import com.cobra.mytravo.helpers.ActionBarUtils;
import com.cobra.mytravo.helpers.BitmapUtil;
import com.cobra.mytravo.helpers.MyImageUtil;
import com.cobra.mytravo.helpers.PhotoUtils;
import com.cobra.mytravo.helpers.ScreenUtil;
import com.cobra.mytravo.helpers.TimeUtils;
import com.cobra.mytravo.models.MyLocation;
import com.cobra.mytravo.models.Note;
import com.cobra.mytravo.models.Travel;
import com.cobra.mytravo.util.Tools;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author L!ar
 *  Activity that add or edit a note
 *
 */
public class AddNoteActivity extends Activity implements OnMenuItemClickListener{
	private final static String TAG = "AddNoteAtivity";
	private static final String NOTE_STRING = "note";
	private final static String LOCATION_STRING = "mylocation";
	private static final int REQUEST_LOCATION_CODE = 3;
	private EditText descriptionEditText;
	private TextView locationTextView;
	private ProgressBar locationProgressBar;
	private ImageView addImageView;
	private ImageView coverImageView;
	private Button deleteButton;
	private Bitmap coverBitmap;
	private String photoTimeString;
	private String descriptionString;
	private String locationString;
	private Location location;
	private String coverPathString;
	private MyLocation myLocation, editMyLocation;
	//mark if is edit
	private boolean isEdit = false;
	//if imageExist is true, means that we have picked a picture of this note
	//(after we create the AddNoteActivity)
	private boolean imageExist = false;
	private NotesDataHelper mDataHelper;
	private Note note;
	private Note editNote;
	private ProgressDialog progressDialog;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch (msg.what) {
			case MyHandlerMessage.ADD_NEW_NOTE_SUCCESS:
				if(isEdit){
					Intent resultIntent = new Intent();
					Bundle resultBundle = new Bundle();
					resultBundle.putSerializable(NOTE_STRING, editNote);
					resultIntent.putExtras(resultBundle);
					setResult(1, resultIntent);
					Toast.makeText(AddNoteActivity.this, "修改足迹成功", Toast.LENGTH_SHORT).show();
				}
				if(progressDialog != null && progressDialog.isShowing())
					progressDialog.dismiss();
				AddNoteActivity.this.finish();
				break;

			default:
				Toast.makeText(AddNoteActivity.this, "修改足迹失败!", Toast.LENGTH_SHORT).show();
				if(progressDialog != null && progressDialog.isShowing())
					progressDialog.dismiss();
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_note);
		ActionBarUtils.InitialDarkActionBar(this, getActionBar(),"添加足迹");
		InitialData();
		InitialView();
		if(!isEdit)
			getLocation();
		mDataHelper = new NotesDataHelper(this, AppData.getUserId(), AppData.getTravelTime());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_note, menu);
		return true;
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_add:
			progressDialog = ProgressDialog.show(this, "添加游记", "数据保存中......", false);
			new Thread(new AddNoteThread()).start();
			break;
		case android.R.id.home:
			this.onBackPressed();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	private void InitialData(){
		if((getIntent() )!= null){
			 editNote = (Note) getIntent().getSerializableExtra(NOTE_STRING);
			 if(editNote != null){
				 isEdit = true;
				 //get the edditedtravel's coverpathstring if exists
				 photoTimeString = editNote.getImage_url();
				 
			 }
		 }
	}
	private void InitialView(){
		locationProgressBar = (ProgressBar) findViewById(R.id.pgb_add_note);
		descriptionEditText = (EditText) findViewById(R.id.edt_add_note);
		locationTextView = (TextView) findViewById(R.id.tv_add_note);
		addImageView = (ImageView) findViewById(R.id.img_add_note);
		deleteButton = (Button) findViewById(R.id.btn_delete_add_note);
		coverImageView = (ImageView) findViewById(R.id.img_cover_add_note);
		deleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				
					imageExist = false;
					view.setVisibility(View.INVISIBLE);
					coverImageView.setVisibility(View.INVISIBLE);
				
				
			}
		});
		addImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				PopupMenu popupMenu = new PopupMenu(AddNoteActivity.this, view);
				MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.photo,
                		popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(AddNoteActivity.this);
                popupMenu.show();
			}
		});
		locationTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AddNoteActivity.this, AroundPlaceActivity.class);
				startActivityForResult(intent, REQUEST_LOCATION_CODE);
			}
		});
		if(isEdit){
			if(editNote.getImage_url() != null){
				coverBitmap = BitmapUtil.getRoundBitmap(BitmapUtil.createScaleBitmap
	        			(AppData.TRAVO_PATH+"/"+editNote.getImage_url()+".jpg", ScreenUtil.dip2px(this, coverImageView.getWidth()), 
	        					ScreenUtil.dip2px(this, coverImageView.getHeight())), 10);
				//MyImageUtil.setBitmap(coverImageView, editNote.getImage_url());
				if(coverBitmap != null){
					coverImageView.setImageBitmap(coverBitmap);
					coverImageView.setVisibility(View.VISIBLE);
					deleteButton.setVisibility(View.VISIBLE);
				}
				
				
				
			}
			if(editNote.getDescription() != null)
				descriptionEditText.setText(editNote.getDescription());
			if(editNote.getLocation() != null){
				 locationTextView.setText(editNote.getLocation().getNameString());
				 locationProgressBar.setVisibility(View.INVISIBLE);
			 }
		}
	}
	private void getLocation()
	{
		new GetAddressTask(this).execute(Tools.getMyLocation(this));
	}
	private class AddNoteThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = new Message();
			if(saveData()){
					msg.what = MyHandlerMessage.ADD_NEW_NOTE_SUCCESS;
				}
			else{
				msg.what = MyHandlerMessage.ADD_NEW_NOTE_FAIL;
			}
			mHandler.sendMessage(msg);
		}
		
	}
	
	private boolean saveData(){
		try{
			if(!isEdit){
				descriptionString = descriptionEditText.getText().toString().trim();
				note = new Note();
				note.setUser_id(AppData.getUserId());
				note.setTravel_created_time(AppData.getTravelTime());
				note.setTime(TimeUtils.getTime().toString());
				note.setDescription(descriptionString);
				if(myLocation != null){
					note.setLocation(myLocation);
				}
				if(imageExist){
					if(coverPathString == null){
						if(photoTimeString != null){
							coverPathString = PhotoUtils.saveImage(photoTimeString, coverBitmap);
							if(coverPathString != null)
								note.setImage_url(photoTimeString);
						}
					}
					else{
						if(photoTimeString != null)
						note.setImage_url(photoTimeString);
						
					}
					
				}
				
				mDataHelper.insert(note);
				return true;
			}
			else{
				descriptionString = descriptionEditText.getText().toString().trim();
				if(descriptionString != null){
					editNote.setDescription(descriptionString);
				}
				if(imageExist){
					if(coverPathString == null){
						if(photoTimeString  != null){
							coverPathString = PhotoUtils.saveImage(photoTimeString, coverBitmap);
							if(coverPathString != null)
								Log.v("coverPath is:", coverPathString);
								editNote.setImage_url(photoTimeString);
						}
					}
					else{
						editNote.setImage_url(photoTimeString);
					}
				}
				
				Note.clearCache();
				mDataHelper.update(editNote);
				return true;
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
	
	 @Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		 	if(resultCode != RESULT_OK)
		 		return;
	        switch (requestCode) {  
	        case 1:  
	        	if(coverBitmap != null)
            		coverBitmap.recycle();
	        	Log.v(TAG, coverPathString);
	        	coverPathString = AppData.TRAVO_PATH + "/"+ coverPathString;
	        	coverBitmap = BitmapFactory.decodeFile(coverPathString);
	        	
	            if(coverBitmap != null){
	            	Log.v(TAG, "camera data is not null");            	
	 	            imageExist = true;
	 	            coverImageView.setImageBitmap(BitmapUtil.getRoundBitmap(BitmapUtil.createScaleBitmap
		        			(coverPathString, ScreenUtil.dip2px(this, coverImageView.getWidth()), 
		        					ScreenUtil.dip2px(this, coverImageView.getHeight())), 10)); 
	 	            coverImageView.setVisibility(View.VISIBLE);	 	   
	 	            deleteButton.setVisibility(View.VISIBLE);
	            }
	            else{
	            	 Log.v(TAG, "coverBitmap is null");
	            }

	            break;
	        case 2:
	        	Log.v("gallery", "gallery data is not null");
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
	            
	            BitmapFactory.Options options = new BitmapFactory.Options();
	            options.inSampleSize = 2;
	            coverBitmap = BitmapFactory.decodeFile(temp,options);
	            
	            coverImageView.setImageBitmap(BitmapUtil.getRoundBitmap(BitmapUtil.createScaleBitmap
	        			(temp, ScreenUtil.dip2px(this, coverImageView.getWidth()), 
	        					ScreenUtil.dip2px(this, coverImageView.getHeight())), 10));
	            coverImageView.setVisibility(View.VISIBLE);
	            deleteButton.setVisibility(View.VISIBLE);
	        	break;
	        case REQUEST_LOCATION_CODE:
	        	myLocation = (MyLocation) data.getSerializableExtra(LOCATION_STRING);
	        	if(myLocation != null){
	        		locationTextView.setText(myLocation.getNameString());
	        		locationProgressBar.setVisibility(View.INVISIBLE);
	        	}
	        	
	        default:  
	        	Toast.makeText(this, "!", Toast.LENGTH_SHORT).show();
	            break;  
	  
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
			PhotoUtils.selectPicture(AddNoteActivity.this);
			return true;
		default:
			return false;
		}
		
	}  
	/** 通过location获得对应的地理位置信息的异步线程 */
	private class GetAddressTask extends AsyncTask<Location, Void, String>
	{
		Context mContext;

		public GetAddressTask(Context context)
		{
			super();
			mContext = context;
		}

		@Override
		protected String doInBackground(Location... params)
		{
			Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
			Location loc = params[0];
			List<Address> addresses = null;

			try
			{
				addresses = geocoder.getFromLocation(loc.getLatitude(),
						loc.getLongitude(), 1);
			} catch (IOException e)
			{
				Log.e("MainActivity", "IO Exception in getFromLocation()");
				e.printStackTrace();
				return null;
			} catch (IllegalArgumentException e2)
			{
				// Error message to post in the log
				String errorString = "Illegal arguments "
						+ Double.toString(loc.getLatitude()) + " , "
						+ Double.toString(loc.getLongitude())
						+ " passed to address service";
				Log.e("LocationSampleActivity", errorString);
				e2.printStackTrace();
				return null;
			}
			if (addresses != null && addresses.size() > 0)
			{
				Address address = addresses.get(0);
				String addressText = String.format(
						"%s", address.getLocality() + address.getSubLocality() + address.getThoroughfare());
				return addressText;
			} else
			{
				return null;
			}
		}

		@Override
		protected void onPostExecute(String address)
		{
			if(address != null){
				if(locationProgressBar != null ){
					locationProgressBar.setVisibility(View.INVISIBLE);
				}
				if(locationTextView != null)
					locationTextView.setText(address);
				Location location = Tools.getMyLocation(mContext);
				if(location != null){
					
					myLocation = new MyLocation();
					myLocation.setLatitude(String.valueOf(location.getLatitude()));
					myLocation.setLongitude(String.valueOf(location.getLongitude()));
					myLocation.setNameString(address);
				}
			}
			
		}
	}
	
}
