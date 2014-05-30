package com.cobra.mytravo.fragments;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.cobra.mytravo.R;
import com.cobra.mytravo.activities.CountryDetailActivity;
import com.cobra.mytravo.activities.MainActivity;
import com.cobra.mytravo.adapters.DestinationAdapter;
import com.cobra.mytravo.adapters.CountryAnimationAdapter;
import com.cobra.mytravo.adapters.DomesticAnimationAdapter;
import com.cobra.mytravo.adapters.ProvinceAdapter;
import com.cobra.mytravo.data.AppData;
import com.cobra.mytravo.data.GsonRequest;
import com.cobra.mytravo.data.RequestManager;
import com.cobra.mytravo.models.Country;
import com.cobra.mytravo.models.DestinationRequest;
import com.cobra.mytravo.models.Province;
import com.cobra.mytravo.util.TwoWayView;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

public class DestinationFragment extends BaseFragment implements PullToRefreshAttacher.OnRefreshListener{
	private static final String TAG = "DestinationFragment";
	private static final String COUNTRY_TAG = "country";
	private MainActivity mActivity;
	private ViewPager mPager;
	private ArrayList<View> pagerViews;
	private TwoWayView overseaListView,domesticListView;
	private DestinationAdapter overseaAdapter;
	private ProvinceAdapter domesticAdapter;
	private ArrayList<Country> hottestCountries;
	private ArrayList<Country> countries;
	private ArrayList<Province> provinces;
	private PullToRefreshAttacher mPullToRefreshAttacher;
	private BitmapDrawable mDefaultBitmap;
	private ProgressBar progressBar;
	private View view1, view2;
	private CountryAnimationAdapter mCountryAnimationAdapter;
	private DomesticAnimationAdapter mDomesticAnimationAdapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view  = inflater.inflate(R.layout.fragment_destination, null);
		view1 = view.findViewById(R.id.ll_oversea);
		view2 = view.findViewById(R.id.ll_domestic);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
		mPager = (ViewPager) view.findViewById(R.id.viewpager);
		mActivity = (MainActivity) getActivity();
		mDefaultBitmap = (BitmapDrawable) mActivity
                .getResources().getDrawable(R.drawable.default_avatar);
//		mPullToRefreshAttacher = ((MainActivity) getActivity()).getPullToRefreshAttacher();
//	    mPullToRefreshAttacher.addRefreshableView(view, this);
		overseaListView = (TwoWayView) view.findViewById(R.id.oversea_list);
		overseaAdapter = new DestinationAdapter(mActivity, overseaListView, null);
		overseaListView.setAdapter(overseaAdapter);
		mCountryAnimationAdapter = new CountryAnimationAdapter(overseaAdapter);
		mCountryAnimationAdapter.setAbsListView(overseaListView);
		overseaListView.setAdapter(mCountryAnimationAdapter);
		overseaListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Intent countryIntent = new Intent(mActivity, CountryDetailActivity.class);
				countryIntent.putExtra(COUNTRY_TAG, overseaAdapter.getItem(position));
				mActivity.startActivity(countryIntent);
			}
		});
		domesticListView = (TwoWayView) view.findViewById(R.id.domestic_list);
		domesticAdapter = new ProvinceAdapter(mActivity, domesticListView, null);
		domesticListView.setAdapter(domesticAdapter);
		mDomesticAnimationAdapter = new DomesticAnimationAdapter(domesticAdapter);
		mDomesticAnimationAdapter.setAbsListView(domesticListView);
		domesticListView.setAdapter(mDomesticAnimationAdapter);
	    loadData();
		return view;
	}
	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		loadData();
	}
	public void loadData(){
//		if (!mPullToRefreshAttacher.isRefreshing()) {
//            mPullToRefreshAttacher.setRefreshing(true);
//           
//        }
		executeRequest(new GsonRequest<DestinationRequest>("http://172.16.12.26:8080/destination/mobile", DestinationRequest.class, null, 
				new Response.Listener<DestinationRequest>() {
					@Override
					public void onResponse(DestinationRequest data) {
						// TODO Auto-generated method stub
						countries = new ArrayList<Country>();
						countries = data.getCountrys();
						hottestCountries = new ArrayList<Country>();
						hottestCountries = data.getHottest_countrys();
						for(int i = 0; i < 5; i++)
							countries.remove(0);
						setPagerData(hottestCountries);
						overseaAdapter.clearData();
						overseaAdapter.setData(countries);
						provinces = new ArrayList<Province>();
						provinces = data.getProvinces();
						domesticAdapter.clearData();
						domesticAdapter.setData(provinces);
						view1.setVisibility(View.VISIBLE);
						view2.setVisibility(View.VISIBLE);
						mPager.setVisibility(View.VISIBLE);
						progressBar.setVisibility(View.INVISIBLE);
					}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(mActivity, "Oops!", Toast.LENGTH_SHORT).show();
			}
		}));
		
	}
	public void setPagerData(ArrayList<Country> countryList){
		final ArrayList<Country> list = countryList;
		if(pagerViews == null)
			pagerViews = new ArrayList<View>();
		else {
			pagerViews.clear();
		}
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		for(int i = 0; i < 5; i++){
			View view = inflater.inflate(R.layout.destination_viewpager_item, null);
			final int position = i;
			ImageView imageView = (ImageView) view.findViewById(R.id.imageView1);
			imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent countryIntent = new Intent(mActivity,CountryDetailActivity.class);
					countryIntent.putExtra(COUNTRY_TAG, list.get(position));
					mActivity.startActivity(countryIntent);
				}
			});
			RequestManager.loadImage(countryList.get(i).getCover_url(),
	                RequestManager.getImageListener(imageView, mDefaultBitmap,
	                		mDefaultBitmap));
			TextView title = (TextView) view.findViewById(R.id.tv_title);
			title.setText(list.get(i).getCountry_name());
			pagerViews.add(view);
		}
		PagerAdapter mPagerAdapter = new PagerAdapter() {
			
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
		
		mPager.setAdapter(mPagerAdapter);
		//mPullToRefreshAttacher.setRefreshComplete();
	}
	
}
