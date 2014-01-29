package com.cobra.mytravo.activities;

import com.cobra.mytravo.R;
import com.cobra.mytravo.fragments.BaseFragment;
import com.cobra.mytravo.fragments.DrawerFragment;
import com.cobra.mytravo.fragments.FakeFragment;
import com.cobra.mytravo.fragments.MeFragment;
import com.cobra.mytravo.fragments.ShotsFragment;


import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
/**
 * 
 * @author qiuky1 2013/12/2
 *
 */
public class MainActivity extends FragmentActivity {

	private DrawerLayout mDrawerLayout;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private ActionBar actionBar;
	private ActionBarDrawerToggle mDrawerToggle;
	private String[] listItems;
	//store current position
	private int current;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
		listItems = getResources().getStringArray(R.array.drawermenu);
        //get and set actionbar
        actionBar = getActionBar();
       
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setSplitBackgroundDrawable(getResources().getDrawable(R.color.black));
        
        //set drawer Listener
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, 
        		R.string.drawer_open, R.string.drawer_close){
        	@Override
        	public void onDrawerOpened(View view) {
        		 getActionBar().setTitle(getResources().getString(R.string.app_name));
        		 invalidateOptionsMenu();
			}
        	@Override
        	public void onDrawerClosed(View view){
        		actionBar.setTitle(listItems[current]);
        		invalidateOptionsMenu();
        	}
        };
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        //set Hot_Travel fragment as default fragment
        setSelectedItem(0);
        //set our custom navigation drawer by using DrawerFragment
		FragmentManager fragmentManager = getFragmentManager();  
		fragmentManager.beginTransaction()  
        .replace(R.id.left_drawer, new DrawerFragment())  
        .commit();  
	}
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        // Handle your other action bar items...
        else{
        	switch(item.getItemId()){
        	case R.id.main_register:
        		Intent intent = new Intent();
        		intent.setClass(MainActivity.this, RegisterActivity.class);
        		startActivity(intent);
        		break;
        	case R.id.show_travels:
        		Intent intent1 = new Intent();
        		intent1.setClass(MainActivity.this, TravelActivity.class);
        		startActivity(intent1);
        		break;
        	case R.id.add_travel:
        		Intent intent2 = new Intent();
        		intent2.setClass(MainActivity.this, AddTravelActivity.class);
        		startActivity(intent2);
        		break;
        	}
        }
        return super.onOptionsItemSelected(item);
    }
	/**
	 * 
	 * @return PullToRefreshAttacher
	 */
	public PullToRefreshAttacher getPullToRefreshAttacher() {
        return mPullToRefreshAttacher;
    }
	
	/**
	 * Switch to selected category, like 0 for hot travel fragment, 1 for nearby fragment etc.
	 * @param position
	 */
	public void setSelectedItem(int position){
		current = position;
		actionBar.setTitle(listItems[current]);
		mDrawerLayout.closeDrawer(GravityCompat.START);
		mPullToRefreshAttacher.setRefreshing(false);
		BaseFragment mContentFragment;
		
		switch (position) {
		case 0:
			
			mContentFragment = new ShotsFragment();
			break;
		case 3:
			mContentFragment = new MeFragment();
			break;
		default:
			mContentFragment = FakeFragment.newInstance(position);
			break;
		}
		 FragmentManager fragmentManager = getFragmentManager();
		 fragmentManager.beginTransaction().replace(R.id.content_frame, mContentFragment).commit();
	}
}
