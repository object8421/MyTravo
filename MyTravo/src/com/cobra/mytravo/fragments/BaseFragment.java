
package com.cobra.mytravo.fragments;

import com.android.volley.Request;
import com.cobra.mytravo.data.RequestManager;


import android.app.Fragment;

/**
 * Created by Issac on 7/18/13.
 */
public class BaseFragment extends Fragment {

    @Override
    public void onStop() {
        super.onStop();
        RequestManager.cancelAll(this);
    }

    protected void executeRequest(Request request) {
        RequestManager.addRequest(request, this);
    }
}
