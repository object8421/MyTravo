package com.cobra.mytravo.adapters;

import android.R;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cobra.mytravo.data.AppData;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

public class CardsAnimationAdapter extends AnimationAdapter {

    private float mTranslationY = 150;

    private float mRotationX = 10;

    private long mDuration;

    public CardsAnimationAdapter(BaseAdapter baseAdapter) {
        super(baseAdapter);
        mDuration = AppData.getContext().getResources().getInteger(R.integer.config_mediumAnimTime);
        
    }

    @Override
    protected long getAnimationDelayMillis() {
        return 30;
    }

    @Override
    protected long getAnimationDurationMillis() {
        return mDuration;
    }

    @Override
    public Animator[] getAnimators(ViewGroup parent, View view) {
        return new Animator[] {
                ObjectAnimator.ofFloat(view, "translationY", mTranslationY, 0),
                ObjectAnimator.ofFloat(view, "rotationX", mRotationX, 0)
        };
    }

//    @Override
//    protected void prepareAnimation(View view) {
//        view.setTranslationY(mTranslationY);
//        view.setRotationX(mRotationX);
//    }
}