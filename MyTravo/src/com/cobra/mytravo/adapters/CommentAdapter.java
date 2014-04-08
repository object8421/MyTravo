package com.cobra.mytravo.adapters;

import java.util.ArrayList;

import com.android.volley.toolbox.ImageLoader;
import com.cobra.mytravo.R;
import com.cobra.mytravo.data.RequestManager;
import com.cobra.mytravo.models.Comment;
import com.cobra.mytravo.models.Travel;

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

public class CommentAdapter extends BaseAdapter{
	private Context context;
    private LayoutInflater mLayoutInflater;

    private ListView mListView;
    private ArrayList<Comment> comments;
    private BitmapDrawable mDefaultAvatarBitmap ;

    private Drawable mDefaultImageDrawable = new ColorDrawable(Color.argb(255, 201, 201, 201));
	public CommentAdapter(Context context, ListView listView, ArrayList<Comment> comments){
		this.context = context;
    	mLayoutInflater = ((Activity) context).getLayoutInflater();
    	mListView = listView;
    	this.comments = comments;
    	mDefaultAvatarBitmap = (BitmapDrawable) context
                .getResources().getDrawable(R.drawable.default_avatar);
	}
    public void setData(ArrayList<Comment> comments){
    	if(this.comments == null)
    		this.comments = comments;
    	else 
    		this.comments.addAll(comments);
    	this.notifyDataSetChanged();
    }
    public void clearData(){
    	this.comments = null;
    }
    @Override
	public int getCount() {
		// TODO Auto-generated method stub
    	if(comments != null)
    		return comments.size();
		return 0;
	}

	@Override
	public Comment getItem(int position) {
		// TODO Auto-generated method stub
		if(comments != null)
			return comments.get(position);
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
			convertView = mLayoutInflater.inflate(R.layout.listitem_comment, null);
			holder = new Holder();
			holder.avatar = (ImageView) convertView.findViewById(R.id.img_avatar);
			holder.nickname = (TextView) convertView.findViewById(R.id.tv_nickname);
			holder.content = (TextView) convertView.findViewById(R.id.tv_content);
			holder.time = (TextView) convertView.findViewById(R.id.tv_time);
			convertView.setTag(holder);
		}else{
			holder = (Holder) convertView.getTag();
		}
		if (holder.avartarRequest != null) {
            holder.avartarRequest.cancelRequest();
        }
		Comment comment = comments.get(position);
		holder.nickname.setText(comment.getCommenter().getNickname());
		holder.content.setText(comment.getContent());
		holder.time.setText(comment.getTime());
		holder.avartarRequest = RequestManager.loadImage("http://travo-user-avatar.oss-cn-hangzhou.aliyuncs.com/"+comment.getCommenter().getFace_path(),
                RequestManager.getImageListener(holder.avatar, mDefaultAvatarBitmap,
                        mDefaultAvatarBitmap));
		return convertView;
	}
	private class Holder{
		public ImageView avatar;
		public TextView nickname;
		public TextView content;
		public TextView time;
		public ImageLoader.ImageContainer avartarRequest;
	}
	
}
