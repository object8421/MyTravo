package com.cobra.mytravo.fragments;



import android.support.v4.app.Fragment;

import com.android.volley.Request;
import com.cobra.mytravo.data.RequestManager;

public class V4BaseFragment extends Fragment{
	

	    @Override
	    public void onStop() {
	        super.onStop();
	        RequestManager.cancelAll(this);
	    }

	    protected void executeRequest(Request request) {
	        RequestManager.addRequest(request, this);
	    }
}
