package com.notes.learning.learningnotes;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.notes.learning.database.MyTodoContentProvider;
import com.notes.learning.database.TodoTable;
import com.notes.learning.subscribedatabase.SubscribeTodoContentProvider;
import com.notes.learning.subscribedatabase.SubscribeTodoTable;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Aleksandar on 1/19/2016.
 */
public class SubscribeActivity extends AppCompatActivity{

    ContentValues values;
    String topic;
    EditText sub_edit;
    String currentDateTimeString;
    SharedPreferences mSharedPreferences;
    SharedPreferences sharedpreferences;
    String MYPREFERENCES="MyPrefs";
    MqttAndroidClient client;
    String clientId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);


        Button subscribe = (Button)findViewById(R.id.button_subscribe);

        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        sharedpreferences = getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        String uri = sharedpreferences.getString("uri", "uri");
        String port = sharedpreferences.getString("port", "1883");
        String clientId = sharedpreferences.getString("clientId", "id");


        client = new MqttAndroidClient(getApplicationContext(), "tcp://"+uri+":"+port,
                MqttClient.generateClientId());

        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sub_edit = (EditText)findViewById(R.id.subscribe_topic);
                topic = sub_edit.getText().toString();
                values = new ContentValues();
                values.put(TodoTable.COLUMN_TOPIC, topic);
                values.put(TodoTable.COLUMN_DATE, currentDateTimeString);
                getContentResolver().insert(MyTodoContentProvider.CONTENT_URI, values);




                try {
                    MqttConnectOptions options = new MqttConnectOptions();
                    options.setCleanSession(false);
                    IMqttToken subToken = client.connect(options);
                    subToken.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // The message was published
                            try {
                                IMqttToken subToken = client.subscribe(topic, 0);
                                subToken.setActionCallback(new IMqttActionListener() {
                                    @Override
                                    public void onSuccess(IMqttToken asyncActionToken) {
                                        // The message was published
                                        client.setCallback(new MqttCallback() {
                                            @Override
                                            public void connectionLost(Throwable throwable) {

                                            }

                                            @Override
                                            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

                                                String date;
                                                ContentValues values;
                                                String messageBody = new String(mqttMessage.getPayload());

                                                Log.d("message", messageBody);
                                                date = DateFormat.getDateTimeInstance().format(new Date());

                                                values = new ContentValues();
                                                values.put(SubscribeTodoTable.COLUMN_TOPIC, s);
                                                values.put(SubscribeTodoTable.COLUMN_MESSAGE, messageBody);
                                                values.put(SubscribeTodoTable.COLUMN_DATE, date);
                                                getContentResolver().insert(SubscribeTodoContentProvider.CONTENT_URI, values);

                                                SharedPreferences sharedpreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor= sharedpreferences.edit();

                                                editor.putString("message", messageBody);

                                                editor.commit();


                                            }

                                            @Override
                                            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                                            }
                                        });
                                        Toast.makeText(getApplicationContext(),"Successful subscription", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(IMqttToken asyncActionToken,
                                                          Throwable exception) {
                                        // The subscription could not be performed, maybe the user was not
                                        // authorized to subscribe on the specified topic e.g. using wildcards
                                        Toast.makeText(getApplicationContext(), "Unsuccessful subscription", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken,
                                              Throwable exception) {
                            // The subscription could not be performed, maybe the user was not
                            // authorized to subscribe on the specified topic e.g. using wildcards

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

        switch (item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);

            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
