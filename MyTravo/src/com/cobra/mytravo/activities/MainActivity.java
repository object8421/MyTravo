package com.cobra.mytravo.activities;

import com.cobra.mytravo.R;
import com.cobra.mytravo.fragments.ShotsFragment;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
/**
 * 
 * @author qiuky1 
 *
 */
public class MainActivity extends FragmentActivity {
//	@InjectView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
//	@InjectView(R.id.left_drawer) RelativeLayout leftDrawerLayout;
//	@InjectView(R.id.left_listview) ListView mDrawerList;
	private DrawerLayout mDrawerLayout;
	private View leftDrawerLayout;
	private ListView mDrawerList;
	private String[] drawerMenu;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		leftDrawerLayout = findViewById(R.id.left_drawer);
		mDrawerList =  (ListView) findViewById(R.id.left_listview);
		mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
		drawerMenu = getResources().getStringArray(R.array.drawermenu);
       

        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, R.id.tv_title,drawerMenu));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerListViewClickListener());
        Fragment fragment = new ShotsFragment();
		FragmentManager fragmentManager = getFragmentManager();  
		fragmentManager.beginTransaction()  
        .replace(R.id.content_frame, fragment)  
        .commit();  
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	/**
	 * 
	 * @return PullToRefreshAttacher
	 */
	public PullToRefreshAttacher getPullToRefreshAttacher() {
        return mPullToRefreshAttacher;
    }
	/**
	 * 
	 * @author qiuky1
	 *
	 */
	private class DrawerListViewClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			selectItem(position);
		}
		
	}
	private void selectItem(int position){
		Fragment fragment = new ShotsFragment();
		FragmentManager fragmentManager = getFragmentManager();  
		fragmentManager.beginTransaction()  
        .replace(R.id.content_frame, fragment)  
        .commit();  
		mDrawerList.setItemChecked(position, true);  
		mDrawerLayout.closeDrawer(leftDrawerLayout);
	}
}
