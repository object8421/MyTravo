package com.cobra.mytravo.adapters;

import java.util.ArrayList;

import com.android.volley.toolbox.ImageLoader;
import com.cobra.mytravo.R;
import com.cobra.mytravo.data.RequestManager;
import com.cobra.mytravo.models.Comment;
import com.cobra.mytravo.models.Travel;
import com.cobra.mytravo.models.User;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FollowersAdapter extends BaseAdapter{
	private Context context;
    private LayoutInflater mLayoutInflater;

    private ListView mListView;
    private ArrayList<User> users;
    private BitmapDrawable mDefaultAvatarBitmap ;

	public FollowersAdapter(Context context, ListView listView, ArrayList<User> users){
		this.context = context;
    	mLayoutInflater = ((Activity) context).getLayoutInflater();
    	mListView = listView;
    	this.users = users;
    	mDefaultAvatarBitmap = (BitmapDrawable) context
                .getResources().getDrawable(R.drawable.default_avatar);
	}
    public void setData(ArrayList<User> users){
    	if(this.users == null)
    		this.users = users;
    	else 
    		this.users.addAll(users);
    	this.notifyDataSetChanged();
    }
    public void clearData(){
    	this.users = null;
    }
    @Override
	public int getCount() {
		// TODO Auto-generated method stub
    	if(users != null)
    		return users.size();
		return 0;
	}

	@Override
	public User getItem(int position) {
		// TODO Auto-generated method stub
		if(users != null)
			return users.get(position);
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
			convertView = mLayoutInflater.inflate(R.layout.listitem_followers, null);
			holder = new Holder();
			holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
			holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
			holder.signature = (TextView) convertView.findViewById(R.id.signature);
			
			convertView.setTag(holder);
		}else{
			holder = (Holder) convertView.getTag();
		}
		if (holder.avartarRequest != null) {
            holder.avartarRequest.cancelRequest();
        }
		User user = users.get(position);
		holder.nickname.setText(user.getNickname());
		holder.signature.setText(user.getSignature());
		
		holder.avartarRequest = RequestManager.loadImage("http://travo-user-avatar.oss-cn-hangzhou.aliyuncs.com/"+user.getFace_path(),
                RequestManager.getImageListener(holder.avatar, mDefaultAvatarBitmap,
                        mDefaultAvatarBitmap));
		return convertView;
	}
	private class Holder{
		public ImageView avatar;
		public TextView nickname;
		public TextView signature;
		
		public ImageLoader.ImageContainer avartarRequest;
	}
	
}
