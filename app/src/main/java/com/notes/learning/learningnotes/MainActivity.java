package com.notes.learning.learningnotes;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.notes.learning.database.AreaCursorAdapter;
import com.notes.learning.database.MyTodoContentProvider;
import com.notes.learning.database.TodoTable;
import com.notes.learning.subscribedatabase.SubscribeTodoContentProvider;
import com.notes.learning.subscribedatabase.SubscribeTodoTable;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    AreaCursorAdapter _adapter;
    TextView primer;
    ContentValues values;
    MqttAndroidClient client;
    String topic_reconnect, currentDateTimeString;

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

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView tv = (TextView) view.findViewById(R.id.topic_name);
                Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                intent.putExtra("topic", tv.getText().toString());
                startActivity(intent);

            }
        });

        primer = (TextView)findViewById(R.id.primer);
        SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String mess = sharedpreferences.getString("message", "message123");
        primer.setText(mess);
        String uri = sharedpreferences.getString("uri", "uri");
        String port = sharedpreferences.getString("port", "1883");
        String clientId = sharedpreferences.getString("clientId", "id");

        Toast.makeText(getApplicationContext(),"url: " +uri+" port "+port+ " clientId "+ clientId ,Toast.LENGTH_LONG).show();

        client = new MqttAndroidClient(getApplicationContext(), "tcp://"+uri+":"+port,
                clientId);

        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        String[] projection = new String[]{TodoTable.COLUMN_TOPIC};
        Cursor cursor = getContentResolver().query(MyTodoContentProvider.CONTENT_URI,projection,null,null,null);
        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                topic_reconnect = cursor.getString(0);

                try {

                    IMqttToken subToken = client.connect();
                    subToken.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // The message was published
                            try {
                                IMqttToken subToken = client.subscribe(topic_reconnect, 0);
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
                                                SharedPreferences.Editor editor = sharedpreferences.edit();

                                                editor.putString("message", messageBody);

                                                editor.commit();


                                            }

                                            @Override
                                            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                                            }
                                        });
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
                            Toast.makeText(getApplicationContext(), "Please connect to the broker!", Toast.LENGTH_SHORT).show();

                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }


                cursor.moveToNext();
            }
        }

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
                MyTodoContentProvider.CONTENT_URI, projection, null, null, TodoTable.COLUMN_DATE+ " DESC");
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
