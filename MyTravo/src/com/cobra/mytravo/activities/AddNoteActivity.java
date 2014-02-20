package com.cobra.mytravo.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.MyHandlerMessage;
import com.cobra.mytravo.data.NotesDataHelper;
import com.cobra.mytravo.helpers.ActionBarUtils;
import com.cobra.mytravo.helpers.PhotoUtils;
import com.cobra.mytravo.helpers.TimeUtils;
import com.cobra.mytravo.models.Note;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AddNoteActivity extends Activity implements OnMenuItemClickListener{
	
	private EditText descriptionEditText;
	private TextView locationTextView;
	private ProgressBar locationProgressBar;
	private ImageView imageView;
	
	private Bitmap noteImage;
	private String photoTimeString;
	private String descriptionString;
	private String locationString;
	private Location location;
	private String noteImagePath;
	private boolean imageExist = false;
	private NotesDataHelper mDataHelper;
	private Note note;
	private ProgressDialog progressDialog;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch (msg.what) {
			case MyHandlerMessage.ADD_NEW_NOTE_SUCCESS:
				if(progressDialog != null && progressDialog.isShowing())
					progressDialog.dismiss();
				AddNoteActivity.this.finish();
				break;

			default:
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_note);
		ActionBarUtils.InitialDarkActionBar(this, getActionBar());
		InitialView();
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
		
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	private void InitialView(){
		descriptionEditText = (EditText) findViewById(R.id.edt_add_note);
		locationTextView = (TextView) findViewById(R.id.tv_add_note);
		imageView = (ImageView) findViewById(R.id.img_add_note);
		imageView.setOnClickListener(new OnClickListener() {
			
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
	}
	private class AddNoteThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = new Message();
			if(saveData()){
					msg.what = MyHandlerMessage.ADD_NEW_NOTE_SUCCESS;
				}
			mHandler.sendMessage(msg);
		}
		
	}
	private boolean getData(){
		descriptionString = descriptionEditText.getText().toString().trim();
		note = new Note();
		note.setUser_id(AppData.getUserId());
		note.setTravel_created_time(AppData.getTravelTime());
		note.setTime(new Date().toString());
		note.setDescription(descriptionString);
		if(imageExist){
			if(photoTimeString != null){
				saveImage();
				note.setImage_url(noteImagePath);
			}
		}
		
		return true;
	}
	private boolean saveData(){
		getData();
		mDataHelper.insert(note);
		return true;
	}
	private void saveImage(){
		FileOutputStream fileOutputStream = null;
	    try {
	        // 获取 SD 卡根目录
	        String saveDir = Environment.getExternalStorageDirectory().toString();
	        		
	        // 新建目录
	        File dir = new File(saveDir + "/Travo/" + String.valueOf(AppData.getUserId()));
	        if (! dir.exists()) 
	        	dir.mkdir();
	        
	        // 生成文件名
	       
	        String filename = TimeUtils.getPhotoTime(Long.parseLong(photoTimeString)) + ".jpg";
	        // 新建文件
	        File file = new File(dir.getAbsolutePath(), filename);
	        // 打开文件输出流
	        fileOutputStream = new FileOutputStream(file);
	        // 生成图片文件
	        noteImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
	        // 相片的完整路径
	        noteImagePath = file.getPath();	  
	        fileOutputStream.flush();
	        
	        fileOutputStream.close();
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        
	    } finally {
	        if (fileOutputStream != null) {
	            try {
	                fileOutputStream.close();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}
	 @Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		 	if(resultCode != RESULT_OK)
		 		return;
	        switch (requestCode) {  
	        case 1:  
	            if (data != null) {  
	            	imageExist = true;
	            	photoTimeString = new Date().toString();
	            	Toast.makeText(this, "拿到图片!", Toast.LENGTH_SHORT).show();
	                //取得返回的Uri,基本上选择照片的时候返回的是以Uri形式，但是在拍照中有得机子呢Uri是空的，所以要特别注意  
	                Uri mImageCaptureUri = data.getData();  
	                //返回的Uri不为空时，那么图片信息数据都会在Uri中获得。如果为空，那么我们就进行下面的方式获取  
	                if (mImageCaptureUri != null) {  
	                    
	                    try {  
	                        //这个方法是根据Uri获取Bitmap图片的静态方法  
	                    	noteImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);  
	                        if (noteImage != null) {  
	                        	imageView.setImageBitmap(noteImage);
	                        }  
	                    } catch (Exception e) {  
	                        e.printStackTrace();  
	                    }  
	                } else {  
	                    Bundle extras = data.getExtras();  
	                    if (extras != null) {  
	                        //这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片  
	                    	noteImage = extras.getParcelable("data");  
	                        if (noteImage != null) {  
	                        	imageView.setImageBitmap(noteImage);  
	                        }  
	                    }  
	                }  
	  
	            }  
	            break;
	        case 2:
	        	imageExist = true;
	        	Uri selectedImage = data.getData();  
	            String[] filePathColumn = { MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN};  
	            
	            Cursor cursor = getContentResolver().query(selectedImage,  
	                    filePathColumn, null, null, null);  
	            cursor.moveToFirst();  
	   
	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);  
	            String imagePathString = cursor.getString(columnIndex);
	            photoTimeString = cursor.getString(cursor.getColumnIndex(filePathColumn[1]));
	            Toast.makeText(this, photoTimeString, Toast.LENGTH_LONG).show();
	            cursor.close();  
	            
//	            BitmapFactory.Options options = new BitmapFactory.Options();
//	            options.inSampleSize = 2;
	            noteImage = BitmapFactory.decodeFile(imagePathString);
	            imageView.setImageBitmap(noteImage); 
	        	break;
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
			PhotoUtils.takePicture(AddNoteActivity.this);
			return true;
		case R.id.gallery:
			PhotoUtils.selectPicture(AddNoteActivity.this);
			return true;
		default:
			return false;
		}
		
	}  
	  
	
}
