package com.cobra.mytravo.adapters;

import java.util.ArrayList;

import com.android.volley.toolbox.ImageLoader;
import com.cobra.mytravo.R;
import com.cobra.mytravo.data.RequestManager;
import com.cobra.mytravo.models.Country;
import com.cobra.mytravo.models.User;
import com.cobra.mytravo.util.TwoWayView;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DestinationAdapter extends BaseAdapter{
	private Context context;
    private LayoutInflater mLayoutInflater;
    private TwoWayView mListView;
    private ArrayList<Country> countries;
    private BitmapDrawable mDefaultBitmap;
    public DestinationAdapter(Context context, TwoWayView listview, ArrayList<Country> countries){
    	this.context = context;
    	mLayoutInflater = ((Activity) context).getLayoutInflater();
    	mListView = listview;
    	this.countries = countries;
    	mDefaultBitmap = (BitmapDrawable) context
                .getResources().getDrawable(R.drawable.default_avatar);
    }
    public void setData(ArrayList<Country> countries){
    	if(this.countries == null)
    		this.countries = countries;
    	else 
    		this.countries.addAll(countries);
    	this.notifyDataSetChanged();
    }
    public void clearData(){
    	this.countries = null;
    }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(this.countries != null)
			return this.countries.size();
		return 0;
	}

	@Override
	public Country getItem(int position) {
		// TODO Auto-generated method stub
		if(countries != null)
			return countries.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder holder = null;
		if(convertView == null){
			convertView = mLayoutInflater.inflate(R.layout.destiation_listitem, null);
			holder = new Holder();
			holder.cover = (ImageView) convertView.findViewById(R.id.imageView1);
			holder.name = (TextView) convertView.findViewById(R.id.textView1);
			convertView.setTag(holder);
		}
		else{
			holder = (Holder) convertView.getTag();
		}
		if(holder.coverRequest != null){
			holder.coverRequest.cancelRequest();
		}
		Country country = countries.get(position);
		holder.name.setText(country.getCountry_name());
		holder.coverRequest = RequestManager.loadImage(country.getCover_url(),
                RequestManager.getImageListener(holder.cover, mDefaultBitmap,
                		mDefaultBitmap));
		return convertView;
	}
	private class Holder{
		private ImageView cover;
		private TextView name;
		public ImageLoader.ImageContainer coverRequest;
	}
}
