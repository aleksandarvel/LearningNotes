package com.notes.learning.learningnotes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

/**
 * Created by Aleksandar on 1/19/2016.
 */
public class PublishActivity extends AppCompatActivity {

    EditText pub, mess;
    Button pubButton;
    public static final String MYPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    MqttAndroidClient client;
    String topic, payload, clientId;
    byte[] encodedPayload;
    CheckBox retained_checkBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        pub = (EditText)findViewById(R.id.publish_topic);
        mess = (EditText)findViewById(R.id.publish_message);
        pubButton = (Button)findViewById(R.id.button_publish);
        retained_checkBox = (CheckBox)findViewById(R.id.retained_checkBox);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.show();

        final ConnectActivity connectActivity = new ConnectActivity();

        pubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string_pub = pub.getText().toString();
                String string_mess = mess.getText().toString();

                sharedpreferences = getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
                String uri = sharedpreferences.getString("uri", "uri");
                String port = sharedpreferences.getString("port", "1883");
                clientId = sharedpreferences.getString("clientId", "0");

                client = new MqttAndroidClient(getApplicationContext(), "tcp://"+uri+":"+port,
                        MqttClient.generateClientId());


                topic = string_pub;
                payload = string_mess;
                encodedPayload = new byte[0];

                try {
                    IMqttToken token = client.connect();
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // We are connected
                            Toast.makeText(getBaseContext(), "Successful connection", Toast.LENGTH_SHORT).show();
                            client.setCallback(new ExampleCallback());

                            try {
                                encodedPayload = payload.getBytes("UTF-8");
                                MqttMessage message = new MqttMessage(encodedPayload);
                                if (retained_checkBox.isChecked())
                                {
                                    message.setRetained(true);
                                }
                                client.publish(topic, message);
                            } catch (UnsupportedEncodingException | MqttException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            // Something went wrong e.g. connection timeout or firewall problems
                            Toast.makeText(getBaseContext(), "Connection failed", Toast.LENGTH_SHORT).show();

                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
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
