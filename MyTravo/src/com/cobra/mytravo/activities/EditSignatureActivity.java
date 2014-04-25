package com.cobra.mytravo.activities;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.GsonRequest;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.fragments.BaseUploadActivity;
import com.cobra.mytravo.models.Comment.CommentRequestData;

import android.os.Bundle;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class EditSignatureActivity extends BaseUploadActivity {
	private String TAG = "editsignatureactivity";
	private EditText signatureEditText;
	private String signature;
	private Map<String,String> map;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_signature);
		signatureEditText = (EditText) findViewById(R.id.editText1);
		signatureEditText.setText(AppData.getSignature());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_signature, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_accept:
			signature = signatureEditText.getText().toString();
			if(signature != null){
				map = new HashMap<String, String>();
				map.put("signature", signature);
				String url = AppData.HOST_IP + "user/update?signature="+signature+"&token="+ AppData.getIdToken();
				executeRequest(new GsonRequest<CommentRequestData>(Method.POST,url,
						CommentRequestData.class, null,
						new Listener<CommentRequestData>() {
							Message msg = new Message();
							@Override
							public void onResponse(CommentRequestData request) {
								
								int status = request.getRsp_code();
								switch(status)
								{
								case MyServerMessage.SUCCESS:
									Toast.makeText(EditSignatureActivity.this, "修改成功!", Toast.LENGTH_SHORT).show();
									setResult(RESULT_OK);
									AppData.setSignature(signature);
									EditSignatureActivity.this.finish();
									break;
								default:
									Toast.makeText(EditSignatureActivity.this, "修改失败!", Toast.LENGTH_SHORT).show();
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
