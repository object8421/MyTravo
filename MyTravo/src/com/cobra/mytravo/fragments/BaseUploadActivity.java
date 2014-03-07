package com.cobra.mytravo.fragments;

import android.app.Activity;

import com.android.volley.Request;
import com.cobra.mytravo.data.RequestManager;

public class BaseUploadActivity extends Activity{
	@Override
    public void onStop() {
        super.onStop();
        RequestManager.cancelAll(this);
    }

    protected void executeRequest(Request request) {
        RequestManager.addRequest(request, this);
    }
}
