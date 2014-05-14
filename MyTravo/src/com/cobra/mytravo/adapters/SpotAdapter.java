package com.cobra.mytravo.adapters;

import java.util.ArrayList;
import com.android.volley.toolbox.ImageLoader;
import com.cobra.mytravo.R;
import com.cobra.mytravo.data.RequestManager;
import com.cobra.mytravo.models.Spot;
import com.cobra.mytravo.util.TwoWayView;
import android.annotation.SuppressLint;
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

public class SpotAdapter extends BaseAdapter{
	private Context context;
    private LayoutInflater mLayoutInflater;
    private TwoWayView mListView;
    private ArrayList<Spot> spots;
    private BitmapDrawable mDefaultBitmap;
    public SpotAdapter(Context context, TwoWayView listview, ArrayList<Spot> spots){
    	this.context = context;
    	mLayoutInflater = ((Activity) context).getLayoutInflater();
    	mListView = listview;
    	this.spots = spots;
    	mDefaultBitmap = (BitmapDrawable) context
                .getResources().getDrawable(R.drawable.default_avatar);
    }
    public void setData(ArrayList<Spot> spots){
    	if(this.spots == null)
    		this.spots = spots;
    	else 
    		this.spots.addAll(spots);
    	this.notifyDataSetChanged();
    }
    public void clearData(){
    	this.spots = null;
    }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(this.spots != null)
			return this.spots.size();
		return 0;
	}

	@Override
	public Spot getItem(int position) {
		// TODO Auto-generated method stub
		if(spots != null)
			return spots.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("NewApi") @Override
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
		Spot spot = spots.get(position);
		holder.name.setText(spot.getSpot_name());
		if(spot.getCover_url() == null)
			spot.setCover_url("");
		holder.coverRequest = RequestManager.loadImage(spot.getCover_url(),
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
