package com.cobra.mytravo.activities;

import cn.jpush.android.api.JPushInterface;

import com.cobra.mytravo.R;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.fragments.BaseFragment;
import com.cobra.mytravo.fragments.DrawerFragment;
import com.cobra.mytravo.fragments.FakeFragment;
import com.cobra.mytravo.fragments.FavoriteFragment;
import com.cobra.mytravo.fragments.HotTravelFragment;
import com.cobra.mytravo.fragments.PersonalFragment;
import com.cobra.mytravo.fragments.SearchFragment;
import com.cobra.mytravo.fragments.SettingFragment;
import com.cobra.mytravo.fragments.ShotsFragment;
import com.cobra.mytravo.helpers.ActionBarUtils;
import com.cobra.mytravo.helpers.PhotoUtils;
import com.cobra.mytravo.internet.GetMyTravelsService;
import com.cobra.mytravo.internet.SyncService;
import com.cobra.mytravo.internet.UploadNoteService;
import com.cobra.mytravo.internet.UploadTravelService;
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
import android.util.Log;
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
import android.widget.SpinnerAdapter;
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
	private HotTravelFragment hottravelFragment;
	private FakeFragment guideFragment, nearbyFakeFragment;
	private FavoriteFragment favoriteFragment;
	private PersonalFragment personalFragment;
	private SettingFragment settingFragment;
	private String hotTravelTag = "hottravel";
	private String fakeTag = "fake";
	private String favoriteTag = "favorite";
	private String personalTag = "personal";
	private String settingTag = "setting";
	//store current position
	private int current;
	private String searchType;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		actionBar = getActionBar();
        ActionBarUtils.ShowActionBarLogo(this, actionBar);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
		listItems = getResources().getStringArray(R.array.drawermenu);
		
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
        	case R.id.logout:
        		AppData.clearData();
        		AppData.setIsLogin(false);
        		Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        		startActivity(loginIntent);
        		MainActivity.this.finish();
        	case R.id.uploadnote:
        		/*Intent uploadnewnote = new Intent(
						MainActivity.this,
						UploadNoteService.class);
				uploadnewnote.putExtra("type", "dirty");
				startService(uploadnewnote);*/
        		Intent syncService = new Intent(MainActivity.this, SyncService.class);
        		syncService.putExtra("token", AppData.getIdToken());
        		startService(syncService);
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
		if(position != 0){
			searchType = "";
		}
		current = position;
		if(position != 2 && position != 5)
			getActionBar().setTitle(listItems[current]);
		mDrawerLayout.closeDrawer(GravityCompat.START);
		mPullToRefreshAttacher.setRefreshing(false);
		BaseFragment mContentFragment;
		FragmentManager fragmentManager = getFragmentManager();
		switch (position) {
//		case 0:
//			if(shotsFragment == null)
//				shotsFragment = new ShotsFragment();
//			mContentFragment = shotsFragment;
//			break;
		case 0:
			setSpinner();
			setCategory(searchType);
//			mContentFragment = (BaseFragment) fragmentManager.findFragmentByTag(hotTravelTag);
//			if(mContentFragment == null){
//				mContentFragment = new HotTravelFragment();
//				fragmentManager.beginTransaction().
//				replace(R.id.content_frame, mContentFragment, hotTravelTag).commit();
//			}
//			else{
//				fragmentManager.beginTransaction().attach(mContentFragment).commit();
//			}
			
			break;
		case 2:
			removeSpinner();
			Intent searcIntent = new Intent(this, SearchActivity.class);
			startActivity(searcIntent);
			break;
		case 3:
			removeSpinner();
			mContentFragment = (BaseFragment) fragmentManager.findFragmentByTag(favoriteTag);
			if(mContentFragment == null){
				mContentFragment = new FavoriteFragment();
				
			}
			fragmentManager.beginTransaction().
			replace(R.id.content_frame, mContentFragment, favoriteTag).commit();
			
			break;
		case 4:
			removeSpinner();
			mContentFragment = (BaseFragment) fragmentManager.findFragmentByTag(personalTag);
			if(mContentFragment == null){
				mContentFragment = new PersonalFragment();
				
			}
			fragmentManager.beginTransaction().
			replace(R.id.content_frame, mContentFragment, personalTag).commit();
			break;
		case 5:
			Intent preferenceIntent = new Intent(this, SettingsActivity.class);
			startActivity(preferenceIntent);
			overridePendingTransition(R.anim.anim_right_to_left_in, R.anim.anim_left_fade_out);
		}
		 
		 
	}
	public Button getComposeButton(){
		return this.composeButton;
	}
	public void setSpinner(){
		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.travel_type,
                android.R.layout.simple_spinner_dropdown_item);
		ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        class OnNavigationListener implements ActionBar.OnNavigationListener{

            @Override
            public boolean onNavigationItemSelected(int itemPosition,
                    long itemId) {
                // TODO Auto-generated method stub
                switch (itemPosition) {
				case 0:
					setCategory("default");
					
					break;
				case 1:
					setCategory("read_times");
					break;
				case 2:
					setCategory("vote_qty");
					break;
				default:
					setCategory("default");
					break;
				}
                return false;
            }
            
        }
        actionBar.setListNavigationCallbacks(mSpinnerAdapter, new OnNavigationListener() );
	}
	public void removeSpinner(){
		ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.DISPLAY_SHOW_TITLE);
	}
	 public void setCategory(String searchType) {
	        
	        if (this.searchType == searchType) {
	            return;
	        }
	        mPullToRefreshAttacher.setRefreshing(false);
	        this.searchType = searchType;
	        
	       
	        BaseFragment mContentFragment;
	        mContentFragment = HotTravelFragment.newInstance(searchType);
			FragmentManager fragmentManager = getFragmentManager();
	        fragmentManager.beginTransaction().replace(R.id.content_frame, mContentFragment).commit();
	    }
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		
	}
	 
}
