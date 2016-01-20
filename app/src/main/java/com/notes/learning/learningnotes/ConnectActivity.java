package com.notes.learning.learningnotes;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Created by Aleksandar on 1/19/2016.
 */
public class ConnectActivity extends AppCompatActivity {

    String uri, port1, username, password;
    EditText server_edit, port_edit, username_edit, password_edit;
    Button connect;
    public static MqttClient client;
    MqttConnectOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.show();

        client = null;



        connect = (Button)findViewById(R.id.connect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try
                {
                    server_edit = (EditText)findViewById(R.id.edit_server);
                    port_edit = (EditText)findViewById(R.id.edit_port);

                    uri = server_edit.getText().toString();
                    port1 = port_edit.getText().toString();

                    client = new MqttClient("tcp://"+uri+":"+"1883", MqttClient.generateClientId(), null);
                    client.setCallback(new ExampleCallback());
                }
                catch (MqttException e1)
                {
                    e1.printStackTrace();
                }

                options = new MqttConnectOptions();
                try
                {
                    client.connect(options);
                }
                catch (MqttException e)
                {
                    Log.d(getClass().getCanonicalName(), "Connection attempt failed with reason code = " + e.getReasonCode() + ":" + e.getCause());
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
