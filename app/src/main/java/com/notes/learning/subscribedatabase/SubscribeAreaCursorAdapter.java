package com.notes.learning.subscribedatabase;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.notes.learning.learningnotes.R;

public class SubscribeAreaCursorAdapter extends CursorAdapter {
    private Context context;
    private int mSelectedPosition;
    LayoutInflater mInflater;

    public SubscribeAreaCursorAdapter(Context context, Cursor c) {
        // that constructor should be used with loaders.
        super(context, c, 0);
        mInflater = LayoutInflater.from(context);
    }

    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
        // something has changed.
        notifyDataSetChanged();
    }

   
	@Override
	public void bindView(View arg0, Context arg1, Cursor arg2) {
		// TODO Auto-generated method stub
		
		TextView topic_name = (TextView)arg0.findViewById(R.id.subscribetopic_name);
        TextView date = (TextView)arg0.findViewById(R.id.subscribedate);
        TextView message = (TextView)arg0.findViewById(R.id.subscribemessage_name);

		
		topic_name.setText(arg2.getString(arg2.getColumnIndex(SubscribeTodoTable.COLUMN_TOPIC)));
        date.setText(arg2.getString(arg2.getColumnIndex(SubscribeTodoTable.COLUMN_DATE)));
        message.setText(arg2.getString(arg2.getColumnIndex(SubscribeTodoTable.COLUMN_MESSAGE)));

	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		 View v = mInflater.inflate(R.layout.subscribelist_row, arg2, false);
	        // edit: no need to call bindView here. That's done automatically
	        return v;
	}

}