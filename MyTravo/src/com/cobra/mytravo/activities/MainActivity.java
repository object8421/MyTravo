package com.cobra.mytravo.activities;

import com.cobra.mytravo.R;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.fragments.BaseFragment;
import com.cobra.mytravo.fragments.DrawerFragment;
import com.cobra.mytravo.fragments.FakeFragment;
import com.cobra.mytravo.fragments.PersonalFragment;
import com.cobra.mytravo.fragments.SettingFragment;
import com.cobra.mytravo.fragments.ShotsFragment;
import com.cobra.mytravo.helpers.ActionBarUtils;
import com.cobra.mytravo.util.ComposeBtnUtil;


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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
/**
 * 
 * @author qiuky1 2013/12/2
 * Activity manages the whole UI components, including fragments
 */
public class MainActivity extends FragmentActivity {
	private ViewGroup rootView;
	private Button composeButton;
	private DrawerLayout mDrawerLayout;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private ActionBar actionBar;
	private ActionBarDrawerToggle mDrawerToggle;
	private String[] listItems;
	private ShotsFragment shotsFragment;
	private FakeFragment guideFragment, nearbyFakeFragment;
	private PersonalFragment personalFragment;
	private SettingFragment settingFragment;
	//store current position
	private int current;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
		listItems = getResources().getStringArray(R.array.drawermenu);
		actionBar = getActionBar();
        ActionBarUtils.ShowActionBarLogo(this, actionBar);
        rootView = ComposeBtnUtil.createLayout(this);
        composeButton = (Button) ComposeBtnUtil.addComposeBtn(rootView, this);
        composeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(AppData.getTravelTime() != null){
        			Intent noteIntent = new Intent(MainActivity.this, AddNoteActivity.class);
        			startActivity(noteIntent);
        		}
        		else{
        			Intent travelIntent = new Intent(MainActivity.this, AddTravelActivity.class);
            		startActivity(travelIntent);
        		}
			}
		});
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
		Toast.makeText(this, "user_id is:"+ AppData.getUserId(), Toast.LENGTH_SHORT).show();
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
        		Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
        		startActivity(registerIntent);
        		break;
        	case R.id.add_note:
        		//if last travel time is not null, enter the AddNoteActivity directly, 
        		//otherwise we enter AddTravelActivity first to create a new travel.
        		//L!ar 2014/2/2
        		if(AppData.getTravelTime() != null){
        			Intent noteIntent = new Intent(MainActivity.this, AddNoteActivity.class);
        			startActivity(noteIntent);
        		}
        		else{
        			Intent travelIntent = new Intent(MainActivity.this, AddTravelActivity.class);
            		startActivity(travelIntent);
        		}
        		break;
        	case R.id.add_travel:
        		Intent travelIntent = new Intent(MainActivity.this, AddTravelActivity.class);
        		startActivity(travelIntent);
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
			if(shotsFragment == null)
				shotsFragment = new ShotsFragment();
			mContentFragment = shotsFragment;
			break;
		case 3:
			if(personalFragment == null)
				personalFragment = new PersonalFragment();
			mContentFragment = personalFragment;
			break;
		default:
			mContentFragment = FakeFragment.newInstance(position);
			break;
		}
		 FragmentManager fragmentManager = getFragmentManager();
		 fragmentManager.beginTransaction().replace(R.id.content_frame, mContentFragment).commit();
	}
	public Button getComposeButton(){
		return this.composeButton;
	}
}
