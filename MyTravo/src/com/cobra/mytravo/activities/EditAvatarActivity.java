package com.cobra.mytravo.activities;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.helpers.PhotoUtils;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

public class EditAvatarActivity extends Activity implements OnMenuItemClickListener{
	private ImageView avatarImageView;
	private ProgressDialog progressDialog;
	public  static final int REQUEST_CODE_CAMERA = 1;
	public  static final int REQUEST_CODE_GALLERY = 2;
	private UploadAvatar thread;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch (msg.what) {
			case MyServerMessage.SUCCESS:
				if(progressDialog.isShowing()){
					progressDialog.dismiss();
					Toast.makeText(EditAvatarActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
				}
					
					
				break;

			default:
				if(progressDialog.isShowing()){
					progressDialog.dismiss();
					Toast.makeText(EditAvatarActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_avatar);
		avatarImageView = (ImageView) findViewById(R.id.imageView1);
		avatarImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				PopupMenu popupMenu = new PopupMenu(EditAvatarActivity.this, view);
				MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.photo,
                		popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(EditAvatarActivity.this);
                popupMenu.show();
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_avatar, menu);
		return true;
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.camera:
			Uri photoUri = PhotoUtils.getPhotoUri("avatar");
			Intent it = new Intent("android.media.action.IMAGE_CAPTURE");//跳转到相机Activity
			//it.setAction(MediaStore.ACTION_IMAGE_CAPTURE);  
			it.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);//告诉相机拍摄完毕输出图片到指定的Uri
			startActivityForResult(it, REQUEST_CODE_CAMERA);
			break;

		case R.id.gallery:
			
			break;
		}
		return false;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode != RESULT_OK)
	 		return;
		switch (requestCode) {
		case REQUEST_CODE_CAMERA:
			progressDialog = ProgressDialog.show(this, "上传头像", "正在上传ing", true, false);
			thread = new UploadAvatar();
			thread.start();
			break;

		case REQUEST_CODE_GALLERY:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
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
				String uploadfile = AppData.TRAVO_PATH + "/avatar.jpg";
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
