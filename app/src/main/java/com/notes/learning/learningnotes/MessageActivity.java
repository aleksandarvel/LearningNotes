package com.notes.learning.learningnotes;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.notes.learning.subscribedatabase.SubscribeAreaCursorAdapter;
import com.notes.learning.subscribedatabase.SubscribeTodoContentProvider;
import com.notes.learning.subscribedatabase.SubscribeTodoTable;

/**
 * Created by Aleksandar on 1/20/2016.
 */
public class MessageActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    SubscribeAreaCursorAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        String topic_title = getIntent().getExtras().getString("topic");

        ActionBar actionbar= getSupportActionBar();
        actionbar.setTitle(topic_title);
        actionbar.show();

        ListView lv= (ListView)findViewById(R.id.message_list);
        getLoaderManager().initLoader(0, null, this);
        Cursor c = getContentResolver().query(SubscribeTodoContentProvider.CONTENT_URI, null, null, null, SubscribeTodoTable.COLUMN_DATE + " DESC");

        _adapter = new SubscribeAreaCursorAdapter(this,c);

        lv.setAdapter(_adapter);
        _adapter.notifyDataSetChanged();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {SubscribeTodoTable.COLUMN_ID, SubscribeTodoTable.COLUMN_TOPIC, SubscribeTodoTable.COLUMN_MESSAGE ,SubscribeTodoTable.COLUMN_DATE};
        CursorLoader cursorLoader = new CursorLoader(this,
                SubscribeTodoContentProvider.CONTENT_URI, projection, null, null, SubscribeTodoTable.COLUMN_DATE+ " DESC");
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        _adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        _adapter.swapCursor(null);
    }
}
