package com.notes.learning.learningnotes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
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
    MqttConnectOptions options;
    public static final String MYPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    public MqttAndroidClient client;
    public String clientId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.show();

        connect = (Button)findViewById(R.id.connect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    server_edit = (EditText)findViewById(R.id.edit_server);
                    port_edit = (EditText)findViewById(R.id.edit_port);

                    uri = server_edit.getText().toString();
                    port1 = port_edit.getText().toString();



                          clientId = MqttClient.generateClientId();
                         client = new MqttAndroidClient(getApplicationContext(), "tcp://"+uri+":"+port1,
                                clientId);

                try {
                    IMqttToken token = client.connect();
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // We are connected
                            Toast.makeText(getBaseContext(), "Successful connection", Toast.LENGTH_SHORT).show();
                            client.setCallback(new ExampleCallback());

                            sharedpreferences = getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor= sharedpreferences.edit();

                            editor.putString("uri", uri);
                            editor.putString("port", port1);
                            editor.putString("clientId",clientId);
                            editor.commit();

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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public MqttAndroidClient GetClient()
    {
        return this.client;
    }
}
