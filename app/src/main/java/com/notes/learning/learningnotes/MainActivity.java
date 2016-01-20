package com.notes.learning.learningnotes;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.notes.learning.database.AreaCursorAdapter;
import com.notes.learning.database.MyTodoContentProvider;
import com.notes.learning.database.TodoTable;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    AreaCursorAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar action_bar = getSupportActionBar();
        action_bar.setDisplayUseLogoEnabled(true);
        action_bar.setDisplayShowHomeEnabled(true);
        action_bar.setIcon(R.drawable.ic_action);
        action_bar.setLogo(R.drawable.ic_action);
        action_bar.show();

        ListView lv= (ListView)findViewById(R.id.topic_list);
        getLoaderManager().initLoader(0, null, this);
        Cursor c = getContentResolver().query(MyTodoContentProvider.CONTENT_URI, null, null, null, TodoTable.COLUMN_DATE + " DESC");

        _adapter = new AreaCursorAdapter(this,c);

        lv.setAdapter(_adapter);
        _adapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.connect: {
                Intent intent = new Intent(MainActivity.this, ConnectActivity.class);
                startActivity(intent);
                return true;
            }

                case R.id.publish: {
                    Intent intent_pub = new Intent(MainActivity.this, PublishActivity.class);
                    startActivity(intent_pub);
                    return true;
                }

            case R.id.subscribe: {
                Intent intent_sub = new Intent(MainActivity.this, SubscribeActivity.class);
                startActivity(intent_sub);
            }

            }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {TodoTable.COLUMN_ID, TodoTable.COLUMN_TOPIC, TodoTable.COLUMN_DATE};
        CursorLoader cursorLoader = new CursorLoader(this,
                MyTodoContentProvider.CONTENT_URI, projection, null, null, TodoTable.COLUMN_TOPIC+ " ASC");
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
