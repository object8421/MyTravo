package com.cobra.mytravo.activities;

import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.internet.user.SetEmailService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditEmailActivity extends Activity
{
	private EditText edtEmail;
	private EditText edtPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_email);
		
		edtEmail = (EditText)findViewById(R.id.edit_email_email);
		edtPassword = (EditText)findViewById(R.id.edit_email_password);
		Button save = (Button)findViewById(R.id.edit_email_save);
		save.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(check())
				{
					Intent setEmail = new Intent(EditEmailActivity.this, SetEmailService.class);
					setEmail.putExtra("email", edtEmail.getText().toString());
					setEmail.putExtra("password", edtPassword.getText().toString());
					startService(setEmail);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_email, menu);
		return true;
	}
	
	private boolean check()
	{
		if("".equals(edtEmail.getText().toString()) || null == edtEmail.getText())
		{
			Toast.makeText(this, "邮箱不能为空", Toast.LENGTH_LONG).show();
			return false;
		}
		if("".equals(edtPassword.getText().toString()) || null == edtPassword.getText())
		{
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
}
