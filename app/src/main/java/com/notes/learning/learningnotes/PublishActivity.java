package com.notes.learning.learningnotes;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by Aleksandar on 1/19/2016.
 */
public class PublishActivity extends AppCompatActivity {

    EditText pub, mess;
    Button pubButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        pub = (EditText)findViewById(R.id.publish_topic);
        mess = (EditText)findViewById(R.id.publish_message);
        pubButton = (Button)findViewById(R.id.button_publish);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.show();

        pubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string_pub = pub.getText().toString();
                String string_mess = mess.getText().toString();

                try
                {
                    MqttMessage message = new MqttMessage();
                    message.setPayload(string_mess.getBytes());
                    ConnectActivity.client.publish(string_pub, message);
                }
                catch (MqttException e)
                {
                    Log.d(getClass().getCanonicalName(), "Publish failed with reason code = " + e.getReasonCode());
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
