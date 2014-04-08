package com.cobra.mytravo.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.cobra.mytravo.R;
import com.cobra.mytravo.data.RequestManager;
import com.cobra.mytravo.helpers.TimeUtils;
import com.cobra.mytravo.models.Travel;

public class HotTravelAdapter extends BaseAdapter {
	private Context context;
    private LayoutInflater mLayoutInflater;

    private ListView mListView;
    private ArrayList<Travel> travels;
    private BitmapDrawable mDefaultAvatarBitmap ;

    private Drawable mDefaultImageDrawable = new ColorDrawable(Color.argb(255, 201, 201, 201));
    
    
    public HotTravelAdapter (Context context, ListView listView, ArrayList<Travel> travels){
    	this.context = context;
    	mLayoutInflater = ((Activity) context).getLayoutInflater();
    	mListView = listView;
    	this.travels = travels;
    	mDefaultAvatarBitmap = (BitmapDrawable) context
                .getResources().getDrawable(R.drawable.default_avatar);
    }
    public void setData(ArrayList<Travel> travels){
    	if(this.travels == null)
    		this.travels = travels;
    	else 
    		this.travels.addAll(travels);
    	this.notifyDataSetChanged();
    }
    public void clearData(){
    	this.travels = null;
    }

    private class Holder {
        public ImageView image;

        public ImageView avatar;

        public TextView title;
        
        public TextView content;

        public TextView userName;

        public TextView text_view_count;

        public TextView text_comment_count;

        public TextView text_like_count;

        public TextView time;

        public ImageLoader.ImageContainer imageRequest;

        public ImageLoader.ImageContainer avartarRequest;

       
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(this.travels != null)
			return travels.size();
		else {
			return 0;
		}
	}

	@Override
	public Travel getItem(int index) {
		// TODO Auto-generated method stub
		if(this.travels != null)
			return travels.get(index);
		else{
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		Holder holder = null;
		if(convertView == null){
			convertView = mLayoutInflater.inflate(R.layout.listitem_shot, null);
			holder = new Holder();
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			holder.avatar = (ImageView) convertView.findViewById(R.id.userinfo_avatar);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.userName = (TextView) convertView.findViewById(R.id.userName);
			holder.text_view_count = (TextView) convertView.findViewById(R.id.text_view_count);
			holder.text_comment_count = (TextView) convertView.findViewById(R.id.text_comment_count);
			holder.text_like_count = (TextView) convertView.findViewById(R.id.text_like_count);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(holder);
		}
		else{
			holder = (Holder) convertView.getTag();
		}
		if (holder.imageRequest != null) {
            holder.imageRequest.cancelRequest();
        }

        if (holder.avartarRequest != null) {
            holder.avartarRequest.cancelRequest();
        }
        Travel travel = this.travels.get(position);
		holder.imageRequest = RequestManager.loadImage("http://travo-travel-cover.oss-cn-hangzhou.aliyuncs.com/"+travel.getCover_path(), RequestManager
                .getImageListener(holder.image, mDefaultImageDrawable, mDefaultImageDrawable));
        holder.avartarRequest = RequestManager.loadImage("http://travo-user-avatar.oss-cn-hangzhou.aliyuncs.com/"+travel.getUser().getFace_path(),
                RequestManager.getImageListener(holder.avatar, mDefaultAvatarBitmap,
                        mDefaultAvatarBitmap));
        holder.title.setText(travel.getTitle());
        holder.content.setText(travel.getDescription());
        holder.userName.setText(travel.getUser().getNickname());
        holder.text_view_count.setText(String.valueOf(travel.getView_qty()));
        holder.text_like_count.setText(String.valueOf(travel.getFavorite_qty()));
        holder.text_comment_count.setText(String.valueOf(travel.getComment_qty()));
        holder.time.setText(TimeUtils.getListTime(travel.getCreate_time()));
        return convertView;
	}
}