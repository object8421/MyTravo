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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.TextView;

public class WelcomeActivity extends Activity
{
	private ImageView mShowPicture;
	private TextView mTextView;
	private Animation mFadeIn;
	private Animation mTextFadeIn;
	private Animation mFadeInScale;
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
		mTextView  = (TextView) findViewById(R.id.textView1);
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

		return false;
	}

	
	private void initAnim()
	{
		mFadeIn = AnimationUtils.loadAnimation(this,
				R.anim.guide_welcome_fade_in);
		mTextFadeIn = AnimationUtils.loadAnimation(this, R.anim.welcome_text_fade_in);
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
				AnimationSet set = new AnimationSet(true);
				mShowPicture.setAnimation(mFadeInScale);
				mTextView.setAnimation(mTextFadeIn);
				set.addAnimation(mFadeInScale);
				set.addAnimation(mTextFadeIn);
				set.start();
			}
		});
		mTextFadeIn.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				mTextView.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				
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
		JPushInterface.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(this);
	}
	
}
