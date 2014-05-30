package com.cobra.mytravo.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * HorizontalAnimationAdapter is used for create animation for TwoWayView
 * It includes rotation & translation animation
 * @author lenovo.Hery1
 *
 */
public class CountryAnimationAdapter extends SpecialAnimationAdapter {

	private float mTranslationX = 150;
	//the default translation distance is 150px
    private float mRotationY = 70;
    //the default rotation degree is 70
    
    private long mDuration;
    
    public CountryAnimationAdapter(BaseAdapter baseAdapter) {
		super(baseAdapter);
		mDuration = 500;
		//default animation duration is set to 500
	}
    
    public float getmTranslationX() {
		return mTranslationX;
	}

	public void setmTranslationX(float mTranslationX) {
		this.mTranslationX = mTranslationX;
	}

	public float getmRotationY() {
		return mRotationY;
	}

	public void setmRotationY(float mRotationY) {
		this.mRotationY = mRotationY;
	}

	public long getmDuration() {
		return mDuration;
	}

	public void setmDuration(long mDuration) {
		this.mDuration = mDuration;
	}

	@Override
	protected long getAnimationDelayMillis() {
	        return 30;
	 }

    @Override
	protected long getAnimationDurationMillis() {
	        return mDuration;
	 }
	 
    /**
     * Use this method to offer the animation we want to the animation Lib
     */
    @Override
	public Animator[] getAnimators(ViewGroup parent, View view) {
    	if(this.mDirection > 0){
		 return new Animator[] {
	                ObjectAnimator.ofFloat(view, "translationX", mTranslationX, 0),
	                ObjectAnimator.ofFloat(view, "rotationY", -mRotationY, 0)
	        };
    	}
    	else if(this.mDirection < 0){
    		return new Animator[] {
	                ObjectAnimator.ofFloat(view, "translationX", -mTranslationX, 0),
	                ObjectAnimator.ofFloat(view, "rotationY", mRotationY, 0)
	        };
    	}
    	else{
    		return new Animator[] {
	                ObjectAnimator.ofFloat(view, "translationX", 0, 0),
	                ObjectAnimator.ofFloat(view, "rotationY", 0, 0)
    		};
    	}
    }
    
    protected void prepareAnimation(View view) {
        view.setTranslationY(mTranslationX);
        view.setRotationX(mRotationY);
    }
    
    

}

