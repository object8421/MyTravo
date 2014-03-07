package com.cobra.mytravo.activities;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cobra.mytravo.R;
import com.cobra.mytravo.R.id;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.GsonRequest;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.fragments.BaseUploadActivity;
import com.cobra.mytravo.models.User.UserInfoResponse;

import android.os.Bundle;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class EditGenderActivity extends BaseUploadActivity {
	private final static String TAG = "EditGenderActivity";
	private Map<String, String> map;
	private RadioGroup radioGroup;
	private RadioButton maleButton, femaleButton;
	private String genderString, editGender;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_gender);
		genderString = AppData.getSex();
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup_gender);
		maleButton = (RadioButton) findViewById(R.id.radio_male);
		femaleButton = (RadioButton) findViewById(R.id.radio_female);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkId) {
				// TODO Auto-generated method stub
				if(checkId == maleButton.getId())
					editGender = "男";
				else{
					editGender = "女";
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_gender, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_accept:
			
			if(!editGender.equals(genderString) ){
				map = new HashMap<String, String>();
				map.put("nickname", editGender);
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
									Toast.makeText(EditGenderActivity.this, "修改成功!", Toast.LENGTH_SHORT).show();
									setResult(RESULT_OK);
									AppData.setSex(editGender);
									EditGenderActivity.this.finish();
									break;
								default:
									Toast.makeText(EditGenderActivity.this, "修改失败!", Toast.LENGTH_SHORT).show();
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
