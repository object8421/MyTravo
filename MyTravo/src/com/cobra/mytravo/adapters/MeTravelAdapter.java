package com.cobra.mytravo.adapters;

import com.cobra.mytravo.R;
import com.cobra.mytravo.helpers.TimeUtils;
import com.cobra.mytravo.models.Travel;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MeTravelAdapter extends CursorAdapter{
	private LayoutInflater mLayoutInflater;
	private ListView mListView;
	private Drawable mDefaultImageDrawable;
	public MeTravelAdapter(Context context, ListView listView) {
		super(context, null, false);
		mLayoutInflater = ((Activity) context).getLayoutInflater();
        mListView = listView;
		mDefaultImageDrawable = context.getResources().getDrawable(R.drawable.me_default_image);
	}
	@Override
	public Travel getItem(int position) {
		mCursor.moveToPosition(position);
		return Travel.fromCursor(mCursor);
	}
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Holder holder = getHolder(view);
		view.setEnabled(!mListView.isItemChecked(cursor.getPosition()
                + mListView.getHeaderViewsCount()));
		Travel travel = Travel.fromCursor(cursor);
		holder.titleTextView.setText(travel.getTitle());
		holder.timeTextView.setText(TimeUtils.getListTime(travel.getCreated_time()));
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		return mLayoutInflater.inflate(R.layout.me_listview_item, null);
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
		private TextView titleTextView;
		private TextView timeTextView;
		public Holder(View view){
			imageView = (ImageView) view.findViewById(R.id.me_image);
			titleTextView = (TextView) view.findViewById(R.id.me_title);
			timeTextView = (TextView) view.findViewById(R.id.me_time);
		}
	}
}