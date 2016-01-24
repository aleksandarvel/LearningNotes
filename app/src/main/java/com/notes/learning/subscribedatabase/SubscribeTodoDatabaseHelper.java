package com.notes.learning.subscribedatabase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SubscribeTodoDatabaseHelper extends SQLiteOpenHelper {

  private static final String DATABASE_NAME = "subscribetodotable.db";
  private static final int DATABASE_VERSION = 1;
  SQLiteDatabase dba;
  SubscribeTodoDatabaseHelper d;

  public SubscribeTodoDatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  // Method is called during creation of the database
  @Override
  public void onCreate(SQLiteDatabase database) {
    SubscribeTodoTable.onCreate(database);
  }

  // Method is called during an upgrade of the database,
  // e.g. if you increase the database version
  @Override
  public void onUpgrade(SQLiteDatabase database, int oldVersion,
      int newVersion) {
    SubscribeTodoTable.onUpgrade(database, oldVersion, newVersion);
  }
  
  
  public boolean ContactExisting(String title)
  {
  	dba=d.getWritableDatabase();
  			
  	Cursor c = dba.rawQuery("SELECT * FROM " + SubscribeTodoTable.TABLE_TODO+ " WHERE " + SubscribeTodoTable.COLUMN_TOPIC+ "= '" + title + "'",null);
  if(c.getCount()==0)
  {
  //doesn't exists therefore insert record.
  	return false;
  }
  dba.close();
  return true;
  }
  
}
 