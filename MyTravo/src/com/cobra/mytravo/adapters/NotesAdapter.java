package com.cobra.mytravo.adapters;

import java.util.ArrayList;

import com.android.volley.toolbox.ImageLoader;
import com.cobra.mytravo.R;
import com.cobra.mytravo.data.RequestManager;
import com.cobra.mytravo.helpers.TimeUtils;
import com.cobra.mytravo.models.Note;
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

public class NotesAdapter extends BaseAdapter{
	private Context context;
    private LayoutInflater mLayoutInflater;

    private ListView mListView;
    private ArrayList<Note> notes;


    private Drawable mDefaultImageDrawable = new ColorDrawable(Color.argb(255, 201, 201, 201));
	public NotesAdapter(Context context, ListView listView, ArrayList<Note> notes){
		this.context = context;
    	mLayoutInflater = ((Activity) context).getLayoutInflater();
    	mListView = listView;
    	this.notes = notes;
    	
	}
	public void setData(ArrayList<Note> notes){
    	if(this.notes == null)
    		this.notes = notes;
    	else 
    		this.notes.addAll(notes);
    	this.notifyDataSetChanged();
    }
    public void clearData(){
    	this.notes = null;
    }
    @Override
	public int getCount() {
		// TODO Auto-generated method stub
    	if(notes != null)
    		return notes.size();
		return 0;
	}

	@Override
	public Note getItem(int position) {
		// TODO Auto-generated method stub
		if(notes != null)
			return notes.get(position);
		return null;
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
			convertView = mLayoutInflater.inflate(R.layout.listitem_note, null);
			holder = new Holder();
			holder.image = (ImageView) convertView.findViewById(R.id.img_cover_note);
			holder.content = (TextView) convertView.findViewById(R.id.tv_description_note);		
			holder.time = (TextView) convertView.findViewById(R.id.tv_time_note);
			holder.location = (TextView) convertView.findViewById(R.id.tv_location_note);
			convertView.setTag(holder);
		}
		else{
			holder = (Holder) convertView.getTag();
		}
		if (holder.imageRequest != null) {
            holder.imageRequest.cancelRequest();
        }
        Note note = this.notes.get(position);
        if(note.getImage_path() != null){
        	holder.image.setVisibility(View.VISIBLE);
        	holder.imageRequest = RequestManager.loadImage("http://travo-note-pic.oss-cn-hangzhou.aliyuncs.com/"+note.getImage_path(), RequestManager
                    .getImageListener(holder.image, mDefaultImageDrawable, mDefaultImageDrawable));
        }
        else{
        	holder.image.setVisibility(View.GONE);
        }
        holder.content.setText(note.getDescription());
        holder.time.setText(TimeUtils.getListTime(note.getCreate_time()));
        if(note.getLocation() != null){
        	holder.location.setVisibility(View.VISIBLE);
        	holder.location.setText(note.getLocation().getAddress());
        }
        else{
        	holder.location.setVisibility(View.INVISIBLE);
        }
        return convertView;
	}
	private class Holder{
		public ImageView image;
		public TextView content;
		public TextView location;
		public TextView time;
		public ImageLoader.ImageContainer imageRequest;
	}
}
