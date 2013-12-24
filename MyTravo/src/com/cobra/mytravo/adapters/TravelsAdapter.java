package com.cobra.mytravo.adapters;

import com.cobra.mytravo.R;
import com.cobra.mytravo.models.Travel;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class TravelsAdapter extends CursorAdapter {

	private LayoutInflater mLayoutInflater;
	private ListView mListView;
	
	public TravelsAdapter(Context context, ListView listview)
	{
		super(context, null, false);
		mLayoutInflater = ((Activity) context).getLayoutInflater();
        mListView = listview;
	}
	
	@Override
	public Travel getItem(int position) {
		mCursor.moveToPosition(position);
		return Travel.fromCursor(mCursor);
	}
	

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		return mLayoutInflater.inflate(R.layout.listitem_travel, null);
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Holder holder = getHolder(view);
		
		view.setEnabled(!mListView.isItemChecked(cursor.getPosition()
                + mListView.getHeaderViewsCount()));
		
		Travel travel = Travel.fromCursor(cursor);
		holder.title.setText(travel.getTitle());
	}
	
	private Holder getHolder(final View view) {
        Holder holder = (Holder) view.getTag();
        if (holder == null) {
            holder = new Holder(view);
            view.setTag(holder);
        }
        return holder;
    }

	private class Holder
	{
		public TextView title;
		
		public Holder(View view)
		{
			title = (TextView)view.findViewById(R.id.travel_title);
		}
	}
}
