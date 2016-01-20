package com.notes.learning.learningnotes;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Aleksandar on 1/20/2016.
 */
public class MessageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        String topic_title = getIntent().getExtras().getString("topic");

        ActionBar actionbar= getSupportActionBar();
        actionbar.setTitle(topic_title);
        actionbar.show();

    }
}
