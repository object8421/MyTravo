package com.cobra.mytravo.activities;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.IllegalFormatCodePointException;

import org.json.JSONException;
import org.json.JSONObject;

import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.helpers.BitmapManager;
import com.cobra.mytravo.helpers.MyImageUtil;
import com.cobra.mytravo.helpers.PhotoUtils;
import com.cobra.mytravo.helpers.TimeUtils;
import com.cobra.mytravo.internet.SearchTravelService;
import com.cobra.mytravo.internet.user.BindService;
import com.cobra.mytravo.internet.user.GetFollowsService;
import com.cobra.mytravo.internet.user.GetUserInfoService;
import com.cobra.mytravo.internet.user.GetUserPhotoService;
import com.cobra.mytravo.internet.user.UnFollowFriendService;
import com.cobra.mytravo.internet.user.UpdatePasswordService;
import com.cobra.mytravo.internet.user.UpdateUserInfoService;
import com.cobra.mytravo.internet.user.UpdateUserService;
import com.cobra.mytravo.internet.user.FollowFriendService;
import com.cobra.mytravo.models.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupMenu.OnMenuItemClickListener;

public class UserInfoActivity extends Activity implements OnMenuItemClickListener{
	private String TAG = "userinfoactivity";
	private static final int REQUEST_CODE_NICKNAME = 0;
	private static final int REQUEST_CODE_GENDER = 1;
	private static final int REQUEST_CODE_SIGNATURE = 4;
	public  static final int REQUEST_CODE_CAMERA = 3;
	public  static final int REQUEST_CODE_GALLERY = 2;
	private View nickname_layout, gender_layout, signature_layout, 
	email_layout, password_layout, bind_layout;
	private TextView nicknameTextView;
	private TextView genderTextView;
	private TextView signatureTextView;
	private ImageView avatarImageView;
	private ProgressDialog progressDialog;
	private Intent editIntent;
	private UploadAvatar thread;
	private String access_token;
	String photoTimeString;
	String avatarString;
	Bitmap avatarBitmap;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch (msg.what) {
			case MyServerMessage.SUCCESS:
				//save current avatar uri, example:201408291100.jpg
				AppData.setAvatarUri(avatarString);
				if(progressDialog.isShowing()){
					
					progressDialog.dismiss();
					Toast.makeText(UserInfoActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
					//cache control
					if(avatarBitmap != null){
						avatarBitmap.recycle();
					}
					String avatarFileString = AppData.TRAVO_PATH + "/"+ avatarString;
					avatarBitmap = BitmapFactory.decodeFile(avatarFileString);
					//show the selected photo as avatar
					if(avatarBitmap != null){
						avatarImageView.setImageBitmap(avatarBitmap);
						BitmapManager manager = new BitmapManager(UserInfoActivity.this);
						manager.fetchBitmapOnThread(avatarFileString, avatarImageView);
					}
					else{
						Log.i(TAG,"image is null!");
					}
				}
					
					
				break;

			default:
				if(progressDialog.isShowing()){
					progressDialog.dismiss();
					Toast.makeText(UserInfoActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		
		nicknameTextView = (TextView)findViewById(R.id.tv_nickname);
		genderTextView = (TextView)findViewById(R.id.tv_gender);
		signatureTextView = (TextView)findViewById(R.id.tv_signature);
		
		nickname_layout = findViewById(R.id.layout_nickname);
		gender_layout = findViewById(R.id.layout_gender);
		signature_layout = findViewById(R.id.layout2);
		email_layout = findViewById(R.id.layout3);
		password_layout = findViewById(R.id.layout4);
		bind_layout = findViewById(R.id.layout5);
		
		
		nicknameTextView.setText(AppData.getNickname());
		genderTextView.setText(AppData.getSex());
		signatureTextView.setText(AppData.getSignature());
		
		password_layout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				/*Intent updatepassword = new Intent(UserInfoActivity.this, UpdatePasswordService.class);
				updatepassword.putExtra("email", "op@qq.com");
				updatepassword.putExtra("oldpassword", "fuckmyself");
				updatepassword.putExtra("newpassword", "w");
				startService(updatepassword);*/
				
				
				/*Intent getUserInfo = new Intent(UserInfoActivity.this, GetUserInfoService.class);
				getUserInfo.putExtra("user_id", 2);
				startService(getUserInfo);*/
				
				/*Intent getUserPhoto = new Intent(UserInfoActivity.this, GetUserPhotoService.class);
				getUserPhoto.putExtra("user_id", 1);
				startService(getUserPhoto);*/
				
				/*Intent followfriend = new Intent(UserInfoActivity.this, FollowFriendService.class);
				followfriend.putExtra("user_id", 1);
				startService(followfriend);*/
				
				/*Intent unfollowfriend = new Intent(UserInfoActivity.this, UnFollowFriendService.class);
				unfollowfriend.putExtra("user_id", 1);
				startService(unfollowfriend);*/
				
				/*Intent follows = new Intent(UserInfoActivity.this, GetFollowsService.class);
				startService(follows);*/
				
				Intent searchtravel = new Intent(UserInfoActivity.this, SearchTravelService.class);
				startService(searchtravel);
			}
		});
		
		nickname_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				editIntent = new Intent(UserInfoActivity.this, EditNicknameActivity.class);
				startActivityForResult(editIntent, REQUEST_CODE_NICKNAME);
			}
		});
		
		email_layout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent setEmail = new Intent(UserInfoActivity.this, EditEmailActivity.class);
				startActivity(setEmail);
			}
		});
		
		bind_layout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Tencent mTencent = Tencent.createInstance(AppData.QQ_KEY, UserInfoActivity.this);
				mTencent.login(UserInfoActivity.this, "get_user_info", new IUiListener()
				{
					@Override
					public void onError(UiError arg0)
					{
						Toast.makeText(UserInfoActivity.this, "error", Toast.LENGTH_LONG).show();
					}
					
					@Override
					public void onComplete(Object arg0)
					{
						Toast.makeText(UserInfoActivity.this, "complete", Toast.LENGTH_LONG).show();
						doComplete((JSONObject)arg0);
						AppData.setQQIdToken(access_token);
						
						Intent bindService = new Intent(UserInfoActivity.this, BindService.class);
						startService(bindService);
					}
					
					protected void doComplete(JSONObject values) {
						try
						{
							access_token = (String) values.get("access_token");
							Log.i("access_token", access_token);
						} catch (JSONException e)
						{
							e.printStackTrace();
						}
					}
					
					@Override
					public void onCancel()
					{
						Toast.makeText(UserInfoActivity.this, "cancel", Toast.LENGTH_LONG).show();
					}
				});
			}
		});
		
		avatarImageView = (ImageView) findViewById(R.id.img_avatar);
		avatarImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				PopupMenu popupMenu = new PopupMenu(UserInfoActivity.this, view);
				MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.photo,
                		popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(UserInfoActivity.this);
                popupMenu.show();
				
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode != RESULT_OK)
			return;
		if(requestCode == REQUEST_CODE_NICKNAME){
			nicknameTextView.setText(AppData.getNickname());
		}
		else if(requestCode == REQUEST_CODE_GENDER){
			genderTextView.setText(AppData.getSex());
		}
		else if(requestCode == REQUEST_CODE_SIGNATURE){
			signatureTextView.setText(AppData.getSignature());
		}
		else if(requestCode == REQUEST_CODE_CAMERA){
			progressDialog = ProgressDialog.show(this, "上传头像", "正在上传ing", true, false);
			thread = new UploadAvatar();
			thread.start();
			
		}
		else if(requestCode == REQUEST_CODE_GALLERY){
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
            avatarBitmap = BitmapFactory.decodeFile(temp,options);
            PhotoUtils.saveImage(photoTimeString, avatarBitmap);
            avatarString = PhotoUtils.getPhotoPath(photoTimeString);
            progressDialog = ProgressDialog.show(this, "上传头像", "正在上传ing", true, false);
            thread = new UploadAvatar();
			thread.start();
		}
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.camera:
			photoTimeString = (String) TimeUtils.getPhotoTime(System.currentTimeMillis());
			avatarString = PhotoUtils.getPhotoPath(photoTimeString);
			Uri photoUri = PhotoUtils.getPhotoUri(avatarString);
			
			Log.i(TAG, avatarString);
			Log.i(TAG, photoUri.toString());
			Intent it = new Intent("android.media.action.IMAGE_CAPTURE");//跳转到相机Activity
			it.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);//告诉相机拍摄完毕输出图片到指定的Uri
			startActivityForResult(it, REQUEST_CODE_CAMERA);
			break;

		case R.id.gallery:
			PhotoUtils.selectPicture(UserInfoActivity.this);
			break;
		}
		return false;
	}

	private class UploadAvatar extends Thread{
		@Override
		public void run(){
			URL url;
			InputStreamReader in = null;
			String result = null;
			String nickname = AppData.getNickname();
			if(nickname != null)
			{
				nickname = nickname.replace(" ", "%20");
			}
			String signature = AppData.getSignature();
			if(signature != null)
			{
				signature = signature.replace(" ", "%20");
			}
					
			String end = "\r\n";
			String twoHyphens = "--";
			String boundary = "-----WebKitFormBoundaryp7MA4YWxkTrZu0gW";
			try
			{
				url = new URL(AppData.HOST_IP + "user/update?token=" + AppData.getIdToken() 
						+ "&nickname=" + nickname + "&signature=" + signature);
				
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
				connection.setRequestProperty("Charset", "utf-8");
				
				DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
				dos.write((twoHyphens + boundary + end).getBytes());
				String uploadfile = AppData.TRAVO_PATH + "/" + avatarString;
				dos.write(("Content-Disposition:form-data;name=\"face\"; filename=\"" + 
						uploadfile.substring(uploadfile.lastIndexOf("/") + 1) + "\"" 
						+ end).getBytes());
				dos.write(end.getBytes());
				
				FileInputStream fis = new FileInputStream(uploadfile);
				byte[] buf = new byte[8192];
				int count = 0;
				while((count = fis.read(buf))!=-1)
				{
					dos.write(buf, 0, count);
				}
				fis.close();
				System.out.println("file send to server............");  
			    dos.write(end.getBytes()); 
				
			    dos.write((twoHyphens + boundary + twoHyphens + end).getBytes());
				dos.flush();
				
				in = new InputStreamReader(connection.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(in);
				StringBuffer strBuffer = new StringBuffer();
				String line = null;
				while((line = bufferedReader.readLine())!=null)
				{
					strBuffer.append(line);
				}
				result = strBuffer.toString();
				JSONObject response = new JSONObject(result);
				int rsp_code = response.getInt("rsp_code");
				
				Log.i("rsp_code", String.valueOf(rsp_code));
				Message msg = new Message();
				switch(rsp_code)
				{
				case MyServerMessage.SUCCESS:
					
					msg.what = MyServerMessage.SUCCESS;
					mHandler.sendMessage(msg);
					
					break;
				default:
					Log.i("有情况", String.valueOf(rsp_code));
					
					msg.what = MyServerMessage.SERVER_ERROR;
					mHandler.sendMessage(msg);
					break;
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
}
