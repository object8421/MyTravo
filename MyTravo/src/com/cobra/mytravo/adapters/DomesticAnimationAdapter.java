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
public class DomesticAnimationAdapter extends SpecialAnimationAdapter {

	private float mTranslationX = 150;
	//the default translation distance is 150px
   
    
    private long mDuration;
    
    public DomesticAnimationAdapter(BaseAdapter baseAdapter) {
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
	                
	        };
    	}
    	else if(this.mDirection < 0){
    		return new Animator[] {
	                ObjectAnimator.ofFloat(view, "translationX", -mTranslationX, 0),
	                
	        };
    	}
    	else{
    		return new Animator[] {
	                ObjectAnimator.ofFloat(view, "translationX", 0, 0),
	                
    		};
    	}
    }
    
    protected void prepareAnimation(View view) {
        view.setTranslationY(mTranslationX);
    }
    
    

}

