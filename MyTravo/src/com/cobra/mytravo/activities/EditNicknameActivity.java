package com.cobra.mytravo.activities;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.GsonRequest;
import com.cobra.mytravo.data.MyHandlerMessage;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.fragments.BaseUploadActivity;
import com.cobra.mytravo.models.User.UserInfoResponse;
import com.cobra.mytravo.models.User.UserRegisterResponse;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author L!ar
 *
 */
public class EditNicknameActivity extends BaseUploadActivity {
	private final static String TAG = "EditNicknameActivity";
	private Map<String, String> map;
	private EditText nicknameEditText;
	private String nicknameString;
	private String editnickname;
	private ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_nickname);
		nicknameString = AppData.getNickname();
		nicknameEditText = (EditText) findViewById(R.id.edt_nickname);
		nicknameEditText.setText(nicknameString);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_nickname, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_accept:
			editnickname = nicknameEditText.getText().toString();
			if(!editnickname.equals(nicknameString) && editnickname != null){
				map = new HashMap<String, String>();
				map.put("nickname", editnickname);
				String url = AppData.HOST_IP + "user/update";
				executeRequest(new GsonRequest<UserInfoResponse>(Method.PUT,url,
						UserInfoResponse.class, null,
						new Listener<UserInfoResponse>() {
							Message msg = new Message();
							@Override
							public void onResponse(UserInfoResponse response) {
								
								int status = response.getRsp_code();
								switch(status)
								{
								case MyServerMessage.SUCCESS:
									Toast.makeText(EditNicknameActivity.this, "修改成功!", Toast.LENGTH_SHORT).show();
									setResult(RESULT_OK);
									AppData.setNickname(editnickname);
									EditNicknameActivity.this.finish();
									break;
								
								case MyServerMessage.DUP_NICKNAME:
									Toast.makeText(EditNicknameActivity.this, "Oops!昵称重复了,修改失败!", Toast.LENGTH_SHORT).show();
									break;
								default:
									Toast.makeText(EditNicknameActivity.this, "修改失败!", Toast.LENGTH_SHORT).show();
									break;
								}
								
							}
						}, new ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								if(error != null){
									Log.i(TAG, error.toString());
								}
								
							}
						}, map));
			}
			
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
