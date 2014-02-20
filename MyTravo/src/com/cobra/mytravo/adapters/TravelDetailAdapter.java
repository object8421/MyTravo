package com.cobra.mytravo.adapters;

import com.cobra.mytravo.R;
import com.cobra.mytravo.helpers.BitmapManager1;
import com.cobra.mytravo.helpers.TimeUtils;
import com.cobra.mytravo.models.Note;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TravelDetailAdapter extends CursorAdapter{
	private LayoutInflater mLayoutInflater;
	private ListView mListView;
	private BitmapManager1 bitmapManager;
	public TravelDetailAdapter(Context context, ListView listView) {
		super(context, null, false);
		mLayoutInflater = ((Activity) context).getLayoutInflater();
        mListView = listView;
        bitmapManager = new BitmapManager1();
	}
	@Override
	public Note getItem(int position){
		mCursor.moveToPosition(position);
		return Note.fromCursor(mCursor);
	}
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		Holder holder = getHolder(view);
		view.setEnabled(!mListView.isItemChecked(cursor.getPosition()
                + mListView.getHeaderViewsCount()));
		Note note = Note.fromCursor(cursor);
		holder.descriptionTextView.setText(note.getDescription());
		holder.timeTextView.setText(TimeUtils.getListTime(note.getTime()));
		holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.me_default_image));
		if(note.getImage_url()!= null){
			bitmapManager.fetchBitmapOnThread(note.getImage_url(), holder.imageView);
		}
		//holder.locationTextView.setText(note.get)
	}
	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return mLayoutInflater.inflate(R.layout.listitem_note, null);
	}
	private Holder getHolder(final View view) {
        Holder holder = (Holder) view.getTag();
        if (holder == null) {
            holder = new Holder(view);
            view.setTag(holder);
        }
        return holder;
    }
	private class Holder{
		private ImageView imageView;
		private TextView descriptionTextView;
		private TextView timeTextView;
		private TextView locationTextView;
		public Holder(View view){
			imageView = (ImageView) view.findViewById(R.id.img_cover_note);
			descriptionTextView = (TextView) view.findViewById(R.id.tv_description_note);
			timeTextView = (TextView) view.findViewById(R.id.tv_time_note);
			locationTextView = (TextView) view.findViewById(R.id.tv_location_note);
		}
	}

}