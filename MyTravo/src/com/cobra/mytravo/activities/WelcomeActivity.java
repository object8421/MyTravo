package com.cobra.mytravo.activities;

import cn.jpush.android.api.JPushInterface;

import com.cobra.mytravo.R;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.MyHandlerMessage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class WelcomeActivity extends Activity
{
	private ImageView mShowPicture;
	private Animation mFadeIn;
	private Animation mFadeInScale;

	// private Handler handler = new Handler() {
	//
	// @Override
	// public void handleMessage(Message msg) {
	// super.handleMessage(msg);
	// switch (msg.what) {
	//
	// case MyHandlerMessage.WELCOME_USER_EXIST:
	// Intent intent = new Intent();
	// intent.setClass(getApplicationContext(), MainActivity.class);
	// startActivity(intent);
	// WelcomeActivity.this.finish();
	// break;
	// case MyHandlerMessage.WELCOME_USER_NOT_EXIST:
	//
	// break;
	// }
	// }
	// };
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		PreferenceManager.setDefaultValues(this, R.xml.preference, false);  
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_welcome);
		mShowPicture = (ImageView) findViewById(R.id.guide_picture);
		init();
	}

	private void init()
	{
		initAnim();
		mShowPicture.startAnimation(mFadeIn);
		setListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.

		return true;
	}

	// private class enterThread extends Thread{
	// @Override
	// public void run(){
	//
	// try {
	//
	// Thread.sleep(3000);
	//
	// Message msg = new Message();
	// msg.what = MyHandlerMessage.WELCOME_USER_EXIST;
	// handler.sendMessage(msg);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	// }
	private void initAnim()
	{
		mFadeIn = AnimationUtils.loadAnimation(this,
				R.anim.guide_welcome_fade_in);
		mFadeInScale = AnimationUtils.loadAnimation(this,
				R.anim.guide_welcome_fade_in_scale);

	}

	private void setListener()
	{
		mFadeIn.setAnimationListener(new AnimationListener()
		{
			public void onAnimationStart(Animation animation)
			{
			}

			public void onAnimationRepeat(Animation animation)
			{
			}

			public void onAnimationEnd(Animation animation)
			{
				mShowPicture.startAnimation(mFadeInScale);
			}
		});
		mFadeInScale.setAnimationListener(new AnimationListener()
		{
			public void onAnimationStart(Animation animation)
			{
			}

			public void onAnimationRepeat(Animation animation)
			{
			}

			public void onAnimationEnd(Animation animation)
			{
				decideWhichActivityToGO();
			}
		});

	}

	private void decideWhichActivityToGO()
	{
		boolean isLogin = AppData.getIsLogin();
		// boolean isLogin = true;
		// 判断是否已经有用户登陆
		if (!isLogin)
		{
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), LoginActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.main_fade_in,
					R.anim.welcome_fade_out);
			WelcomeActivity.this.finish();
		}

		else
		{
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), MainActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.main_fade_in,
					R.anim.welcome_fade_out);
			WelcomeActivity.this.finish();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.resumePush(getApplicationContext());
		JPushInterface.setAliasAndTags(getApplicationContext(), AppData.getNickname(), null);
	}
	
}
