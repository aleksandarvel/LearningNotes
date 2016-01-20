package com.notes.learning.learningnotes;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.notes.learning.database.MyTodoContentProvider;
import com.notes.learning.database.TodoTable;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Aleksandar on 1/19/2016.
 */
public class SubscribeActivity extends AppCompatActivity {

    ContentValues values;
    String topic;
    EditText sub_edit;
    String currentDateTimeString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);


        Button subscribe = (Button)findViewById(R.id.button_subscribe);

        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());



        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sub_edit = (EditText)findViewById(R.id.subscribe_topic);
                topic = sub_edit.getText().toString();
                values = new ContentValues();
                values.put(TodoTable.COLUMN_TOPIC, topic);
                values.put(TodoTable.COLUMN_DATE, currentDateTimeString);
                getContentResolver().insert(MyTodoContentProvider.CONTENT_URI, values);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
