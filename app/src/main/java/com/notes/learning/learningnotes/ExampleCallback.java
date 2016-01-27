package com.notes.learning.learningnotes;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.notes.learning.subscribedatabase.SubscribeTodoContentProvider;
import com.notes.learning.subscribedatabase.SubscribeTodoTable;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Aleksandar on 1/20/2016.
 */
public class ExampleCallback extends AppCompatActivity implements MqttCallback {

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
}
