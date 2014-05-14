package com.cobra.mytravo.activities;

import java.util.ArrayList;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.cobra.mytravo.R;
import com.cobra.mytravo.R.layout;
import com.cobra.mytravo.R.menu;
import com.cobra.mytravo.adapters.CityAdapter;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.GsonRequest;
import com.cobra.mytravo.data.MyServerMessage;
import com.cobra.mytravo.data.RequestManager;
import com.cobra.mytravo.helpers.ActionBarUtils;
import com.cobra.mytravo.models.City;
import com.cobra.mytravo.models.Country;
import com.cobra.mytravo.models.Province;
import com.cobra.mytravo.models.UserInfo;
import com.cobra.mytravo.models.UserInfo.UserInfoRequestData;
import com.cobra.mytravo.util.TwoWayView;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProvinceDetailActivity extends Activity {
	private static final String TAG = "CountryDetailActivity";
	private static final String PROVINCE_TAG = "province";
	private static final String CITY_TAG = "city";
	private ViewPager mPager;
	private ArrayList<View> pagerViews;
	private TwoWayView cityListView;
	private CityAdapter mListAdapter;
	private Province province;
	private ArrayList<String> imageList;
	private ArrayList<City> cities;
	private TextView brief_informationTextView;
	private TextView detail_hinTextView,detail_informationTextView;
	private String brief,detail;
	private Button showBtn, hideBtn;
	private BitmapDrawable mDefaultBitmap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_province_detail);
		
		mDefaultBitmap = (BitmapDrawable)getResources().getDrawable(R.drawable.default_avatar);
		mPager = (ViewPager) findViewById(R.id.viewpager);
		province = (Province) getIntent().getSerializableExtra(PROVINCE_TAG);
		ActionBarUtils.InitialActionBarWithBackAndTitle(this, getActionBar(), province.getProvince_name());
		cityListView = (TwoWayView) findViewById(R.id.city_list);
		brief_informationTextView = (TextView) findViewById(R.id.textView1);
		detail_informationTextView = (TextView) findViewById(R.id.textView2);
		detail_hinTextView = (TextView) findViewById(R.id.textView4);
		showBtn = (Button) findViewById(R.id.button1);
		hideBtn = (Button) findViewById(R.id.button2);
		brief = province.getBrief_information();
		detail = province.getDetail_information();
		brief_informationTextView.setText(brief);
		detail_informationTextView.setText(detail);
		showBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				detail_informationTextView.setVisibility(ViewPager.VISIBLE);
				showBtn.setVisibility(View.GONE);
				hideBtn.setVisibility(View.VISIBLE);
				detail_hinTextView.setVisibility(View.VISIBLE);
			}
		});
		hideBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				detail_informationTextView.setVisibility(View.GONE);
				hideBtn.setVisibility(View.GONE);
				showBtn.setVisibility(View.VISIBLE);
				detail_hinTextView.setVisibility(View.GONE);
			}
		});
		
		mListAdapter = new CityAdapter(this, cityListView, null);
		if(cityListView != null)
			cityListView.setAdapter(mListAdapter);
		cityListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Intent cityIntent = new Intent(ProvinceDetailActivity.this, CityDetailActivity.class);
				cityIntent.putExtra(CITY_TAG, mListAdapter.getItem(position));
				ProvinceDetailActivity.this.startActivity(cityIntent);
			}
		});
		loadData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.country_detail, menu);
		return true;
	}
	public void loadPagerData(ArrayList<String> imageList){
		ArrayList<String> list = imageList;
		if(pagerViews == null){
			pagerViews = new ArrayList<View>();
		}
		else{
			pagerViews.clear();
		}
		LayoutInflater inflater = LayoutInflater.from(this);
		for(int i = 0; i < list.size(); i++){
			View view = inflater.inflate(R.layout.destination_viewpager_item, null);
			
			ImageView imageView = (ImageView) view.findViewById(R.id.imageView1);
			RequestManager.loadImage(list.get(i),
	                RequestManager.getImageListener(imageView, mDefaultBitmap,
	                		mDefaultBitmap));
			TextView title = (TextView) view.findViewById(R.id.tv_title);
			title.setVisibility(View.GONE);
			pagerViews.add(view);
		}
		PagerAdapter mAdapter = new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View view, Object object) {
				// TODO Auto-generated method stub
				return view == object;
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return pagerViews.size();
			}
			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				// TODO Auto-generated method stub
				((ViewPager)container).addView(pagerViews.get(position));
				return pagerViews.get(position);

			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				// TODO Auto-generated method stub
				((ViewPager)container).removeView(pagerViews.get(position));

			}
		};
		mPager.setAdapter(mAdapter);
	}
	public void loadData(){
		RequestManager.addRequest(new GsonRequest<Province.ProvinceRequestData>("http://172.16.12.26:8080/destination/mobile/province/"+province.getId(),
				Province.ProvinceRequestData.class, null, new Listener<Province.ProvinceRequestData>() {

					@Override
					public void onResponse(Province.ProvinceRequestData requestData) {
						// TODO Auto-generated method stub
							loadPagerData(requestData.getImage_url_list());
							mListAdapter.clearData();
							mListAdapter.setData(requestData.getRelated_city());
						}
					
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						// TODO Auto-generated method stub
						Toast.makeText(ProvinceDetailActivity.this, R.string.refresh_list_failed,
                                Toast.LENGTH_SHORT).show();
					}
				}), this);
	}
	
}

