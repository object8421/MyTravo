package com.cobra.mytravo.adapters;

import java.util.ArrayList;

import com.android.volley.toolbox.ImageLoader;
import com.cobra.mytravo.R;
import com.cobra.mytravo.activities.ProvinceDetailActivity;
import com.cobra.mytravo.data.RequestManager;
import com.cobra.mytravo.models.Country;
import com.cobra.mytravo.models.Province;
import com.cobra.mytravo.models.User;
import com.cobra.mytravo.util.TwoWayView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProvinceAdapter extends BaseAdapter{
	private static final String PROVINCE_TAG = "province";
	private Context context;
    private LayoutInflater mLayoutInflater;
    private TwoWayView mListView;
    private ArrayList<Province> provinces;
    private BitmapDrawable mDefaultBitmap;
    public ProvinceAdapter(Context context, TwoWayView listview, ArrayList<Province> provinces){
    	this.context = context;
    	mLayoutInflater = ((Activity) context).getLayoutInflater();
    	mListView = listview;
    	this.provinces = provinces;
    	mDefaultBitmap = (BitmapDrawable) context
                .getResources().getDrawable(R.drawable.default_avatar);
    }
    public void setData(ArrayList<Province> provinces){
    	if(this.provinces == null)
    		this.provinces = provinces;
    	else 
    		this.provinces.addAll(provinces);
    	this.notifyDataSetChanged();
    }
    public void clearData(){
    	this.provinces = null;
    }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(this.provinces != null)
			return (this.provinces.size()/2)+1;
		return 0;
	}

	@Override
	public Province getItem(int position) {
		// TODO Auto-generated method stub
		if(provinces != null)
			return provinces.get(position);
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
			convertView = mLayoutInflater.inflate(R.layout.listitem_province, null);
			holder = new Holder();
			holder.cover1 = (ImageView) convertView.findViewById(R.id.imageView1);
			holder.cover2 = (ImageView) convertView.findViewById(R.id.imageView2);
			holder.name1 = (TextView) convertView.findViewById(R.id.textView1);
			holder.name2 = (TextView) convertView.findViewById(R.id.textView2);
			convertView.setTag(holder);
		}
		else{
			holder = (Holder) convertView.getTag();
		}
		if(holder.coverRequest1 != null){
			holder.coverRequest1.cancelRequest();
		}
		if(holder.coverRequest2 != null){
			holder.coverRequest2.cancelRequest();
		}
		int firstIndex = position*2;
		int secondIndex = position*2 + 1;
		if(firstIndex <= provinces.size()-1){
			final Province province1 = provinces.get(firstIndex);
			
			holder.name1.setText(province1.getProvince_name());
			holder.coverRequest1 = RequestManager.loadImage(province1.getCover_url(),
	                RequestManager.getImageListener(holder.cover1, mDefaultBitmap,
	                		mDefaultBitmap));
			holder.cover1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent1 = new Intent(context,ProvinceDetailActivity.class);
					intent1.putExtra(PROVINCE_TAG, province1);
					context.startActivity(intent1);
				}
			});
		}
		if(secondIndex <= provinces.size()-1){
			holder.name2.setVisibility(View.VISIBLE);
			holder.cover2.setVisibility(View.VISIBLE);
			final Province province2 = provinces.get(secondIndex);
			holder.name2.setText(province2.getProvince_name());
			holder.coverRequest2 = RequestManager.loadImage(province2.getCover_url(),
	                RequestManager.getImageListener(holder.cover2, mDefaultBitmap,
	                		mDefaultBitmap));
			holder.cover2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent2 = new Intent(context,ProvinceDetailActivity.class);
					intent2.putExtra(PROVINCE_TAG, province2);
					context.startActivity(intent2);
				}
			});
		}
		else{
			holder.name2.setVisibility(View.GONE);
			holder.cover2.setVisibility(View.GONE);
			
		}
		return convertView;
	}
	private class Holder{
		private ImageView cover1;
		private ImageView cover2;
		private TextView name1;
		private TextView name2;
		public ImageLoader.ImageContainer coverRequest1;
		public ImageLoader.ImageContainer coverRequest2;
	}
}
