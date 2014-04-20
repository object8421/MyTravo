package com.cobra.mytravo.activities;

import java.util.ArrayList;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cobra.mytravo.R;
import com.cobra.mytravo.R.id;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.adapters.HotTravelAdapter;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.GsonRequest;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.data.RequestManager;
import com.cobra.mytravo.models.Travel;
import com.cobra.mytravo.ui.LoadingFooter;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;

public class SearchActivity extends Activity implements OnQueryTextListener{
	private String TAG = "searchactivity";
	private ActionBar actionBar;
	private SearchView searchView;
	private ListView mListView;
	private ProgressBar mProgressBar;
	private HotTravelAdapter mAdapter;
	private ArrayList<Travel> travels;
	private String keyword;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		actionBar = getActionBar();
		actionBar.setTitle("搜索");
		mListView = (ListView) findViewById(R.id.listView1);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	//	 Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setIconifiedByDefault(false);
		searchView.setQueryHint("输入游记关键字");
		searchView.setOnQueryTextListener(this);
		searchView.setFocusable(true);
	    searchView.setIconified(false);
	    searchView.requestFocusFromTouch();
		return true;
	}

	@Override
	public boolean onQueryTextChange(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String keyword) {
		// TODO Auto-generated method stub
		if(keyword != null && !keyword.equals(""))
			search();
		return true;
	}
	private void search(){
		mListView.setVisibility(View.INVISIBLE);
		mProgressBar.setVisibility(View.VISIBLE);
		RequestManager.addRequest(new GsonRequest<Travel.TravelsRequestData>(String.format(AppData.HOST_IP+"travel/search?order=%1$s&first_idx=%2$d&max_qty=%3$d",
        		"default",1,10), Travel.TravelsRequestData.class, null,
                new Response.Listener<Travel.TravelsRequestData>() {
                    @Override
                    public void onResponse(final Travel.TravelsRequestData requestData) {
                        	    
                            	
                            	int rsp_code = requestData.getRsp_code();
                                travels = requestData.getTravels();
                                
                                Log.i(TAG,travels.toString());
                                mAdapter = new HotTravelAdapter(SearchActivity.this, mListView, travels);
                                mListView.setAdapter(mAdapter);
                                mAdapter.clearData();
                                mAdapter.setData(travels);
                                mListView.setVisibility(View.VISIBLE);
                        		mProgressBar.setVisibility(View.INVISIBLE);
                            
                     
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    	 Toast.makeText(SearchActivity.this, R.string.refresh_list_failed,
                                 Toast.LENGTH_SHORT).show();
                    	 mListView.setVisibility(View.INVISIBLE);
                 		mProgressBar.setVisibility(View.INVISIBLE);
                    }
                }), this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		RequestManager.cancelAll(this);
	}
	
}
