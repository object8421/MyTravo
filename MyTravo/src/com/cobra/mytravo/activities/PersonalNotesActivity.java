package com.cobra.mytravo.activities;

import java.util.ArrayList;

import roboguice.inject.ContentView;

import uk.co.senab.photoview.PhotoView;

import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.adapters.TravelDetailAdapter;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.NotesDataHelper;
import com.cobra.mytravo.helpers.ActionBarUtils;
import com.cobra.mytravo.models.Note;
import com.cobra.mytravo.models.Travel;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.Bundle;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PersonalNotesActivity extends Activity {
	private final static String TAG = "PersonalNotesActivity";
	private String type;
	private NotesDataHelper mDataHelper;
	private ViewPager mPager;
	private int position;
	private Travel travel;
	private ArrayList<Note> notes;
	private TravelDetailAdapter mAdapter;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_notes);
		ActionBarUtils.InitialActionBarWithBackAndTitle(this, getActionBar(), "足迹详情");
		//travel = (Travel) getIntent().getSerializableExtra("travel");
		notes = (ArrayList<Note>) getIntent().getSerializableExtra("notes");
		type = getIntent().getStringExtra("type");
		mDataHelper = new NotesDataHelper(this, AppData.getUserId());
		mPager = (ViewPager) findViewById(R.id.viewpager);
		MyPagerAdapter myPagerAdapter = new MyPagerAdapter(notes);
		mPager.setAdapter(myPagerAdapter);
		position = getIntent().getIntExtra("position", 0);
		mPager.setCurrentItem(position);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.personal_notes, menu);
		return true;
	}


	class MyPagerAdapter extends PagerAdapter{
		private LayoutInflater inflater;
		private ArrayList<Note> notesList;
		private int count;
		public MyPagerAdapter(ArrayList<Note> notes){
			inflater = getLayoutInflater();
			this.notesList = notes;
			count = notesList.size();
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return notesList.size();
		}
		@Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
		@Override
		public boolean isViewFromObject(View view, Object object) {
			// TODO Auto-generated method stub
			return view == object;
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			View layout = inflater.inflate(R.layout.viepager_item_note, container, false);
			PhotoView photoView = (PhotoView) layout.findViewById(R.id.imageView1);
			TextView contentView = (TextView) layout.findViewById(R.id.textView2);
			TextView locationView = (TextView) layout.findViewById(R.id.textView3);
			TextView countView = (TextView) layout.findViewById(R.id.textView1);
			String url = notesList.get(position).getImage_url();
			contentView.setText(notesList.get(position).getContent());
			locationView.setText(notesList.get(position).getLocation().getAddress());
			countView.setText(position+1+"/"+count);
			if(type.equals("local")){
				imageLoader.displayImage("file:///"+url , photoView);
			}
			else{
				imageLoader.displayImage("http://travo-note-pic.oss-cn-hangzhou.aliyuncs.com/"+notesList.get(position).getImage_path() , photoView);
			}
			 ((ViewPager) container).addView(layout, 0);
			 return layout;
		}
		
	}
}
