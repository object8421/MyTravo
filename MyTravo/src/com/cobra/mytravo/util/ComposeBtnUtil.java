package com.cobra.mytravo.util;

import com.cobra.mytravo.R;
import com.cobra.mytravo.helpers.BitmapUtil;
import com.cobra.mytravo.helpers.ScreenUtil;

import android.R.animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class ComposeBtnUtil {
	private static Button btn;
	private static boolean isAnimating = false;
	private static boolean isAlreadyIn = true;
	private static boolean isAlreadyOut = false;
	private static final int ANIMATION_DURATION = 300;
	private ComposeBtnUtil(){
		
	}
	public static ViewGroup createLayout(Context context){
		ViewGroup rootView = (ViewGroup) ((Activity)context).getWindow().getDecorView();
		FrameLayout animLayout = new FrameLayout(context);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		animLayout.setLayoutParams(lp);
		animLayout.setId(R.id.anim_mask_layout);
		animLayout.setBackgroundResource(android.R.color.transparent);
	    rootView.addView(animLayout);
	    return animLayout;
	}
	@SuppressLint("NewApi") 
	public static View addComposeBtn(ViewGroup viewGroup, Context context){
		Button composeButton = new Button(context);
		composeButton.setId(R.id.btn_compose);
		composeButton.setBackground(context.getResources().getDrawable(R.drawable.btn_compose_selector));
		viewGroup.addView(composeButton);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ScreenUtil.dip2px(context, 50),
				ScreenUtil.dip2px(context, 50));
		lp.gravity = Gravity.BOTTOM | Gravity.CENTER;
		composeButton.setLayoutParams(lp);
		return composeButton;
	}
	public static void runInAnimation(Button composeButton){
		if(!isAnimating && isAlreadyOut){
			btn = composeButton;
			isAnimating = true;
			AnimationSet set = new AnimationSet(true);
			set.setInterpolator(new AccelerateDecelerateInterpolator());
			TranslateAnimation translateAnimation = 
						new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF,0.0f, 
								TranslateAnimation.RELATIVE_TO_SELF,
								0.0f, TranslateAnimation.RELATIVE_TO_SELF, 1.0f, 
								TranslateAnimation.RELATIVE_TO_SELF,0.0f);
			
			translateAnimation.setDuration(ANIMATION_DURATION);
			
			AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
			alphaAnimation.setDuration(ANIMATION_DURATION);
			
			set.addAnimation(alphaAnimation);
			set.addAnimation(translateAnimation);
			set.setDuration(ANIMATION_DURATION);
			set.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					// TODO Auto-generated method stub
					btn.setVisibility(View.VISIBLE);
					btn.setClickable(true);
					isAnimating = false;
					isAlreadyIn = true;
					isAlreadyOut = false;
				}
			});
			btn.startAnimation(set);
			
		}
		
	}
	public static void runOutAnimation(Button composeButton){
		if(!isAnimating && isAlreadyIn){
			btn = composeButton;
			isAnimating = true;
			AnimationSet set = new AnimationSet(true);
			set.setInterpolator(new AccelerateDecelerateInterpolator());
			TranslateAnimation translateAnimation = 
					new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF,0.0f, 
							TranslateAnimation.RELATIVE_TO_SELF,
							0.0f, TranslateAnimation.RELATIVE_TO_SELF, 0.0f, 
							TranslateAnimation.RELATIVE_TO_SELF,1.0f);
			translateAnimation.setDuration(ANIMATION_DURATION);
			
			AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
			alphaAnimation.setDuration(ANIMATION_DURATION);
			
			set.addAnimation(alphaAnimation);
			set.addAnimation(translateAnimation);
			set.setDuration(ANIMATION_DURATION);
			set.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					// TODO Auto-generated method stub
					
					btn.setClickable(false);
					btn.setVisibility(View.GONE);
					isAnimating = false;
					isAlreadyOut = true;
					isAlreadyIn = false;
				}
			});
			btn.startAnimation(set);
		}
		
	}
}
