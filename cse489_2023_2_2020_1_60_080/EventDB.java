package edu.ewubd.cse489_2023_2_2020_1_60_080;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EventDB extends SQLiteOpenHelper {

	public EventDB(Context context) {
		super(context, "EventDB.db", null, 1);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out.println("DB@OnCreate");
		String sql = "CREATE TABLE events  ("
								+ "ID TEXT PRIMARY KEY,"
								+ "title TEXT,"
								+ "place TEXT,"
								+ "datetime INT,"
								+ "capacity INT,"
								+ "budget REAL,"
								+ "email TEXT,"
								+ "phone TEXT,"
								+ "des TEXT,"
								+ "eventtype TEXT"
								+ ")";
		db.execSQL(sql);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("Write code to modify database schema here");
		// db.execSQL("ALTER table my_table  ......");
	}
	public void insertEvent(String ID, String title, String place, long datetime, int capacity,
							double budget, String email, String phone, String des, String type) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cols = new ContentValues();
		cols.put("ID", ID);
		cols.put("title", title);
		cols.put("place", place);
		cols.put("datetime", datetime);
		cols.put("capacity", capacity);
		cols.put("budget", budget);
		cols.put("email", email);
		cols.put("phone", phone);
		cols.put("des", des);
		cols.put("eventType", type);

		db.insert("events", null ,  cols);
		db.close();
	}
	public void updateEvent(String ID, String title, String place, long datetime, int capacity,
							double budget, String email, String phone, String des, String type) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cols = new ContentValues();
		cols.put("ID", ID);
		cols.put("title", title);
		cols.put("place", place);
		cols.put("datetime", datetime);
		cols.put("capacity", capacity);
		cols.put("budget", budget);
		cols.put("email", email);
		cols.put("phone", phone);
		cols.put("des", des);
		cols.put("eventType", type);
  		db.update("events", cols, "ID=?", new String[ ] {ID} );
		db.close();
	}
	public void deleteEvent(String ID) {
		SQLiteDatabase db = this.getWritableDatabase();
  		db.delete("events", "ID=?", new String[ ] {ID} );
		db.close();
	}
	public Cursor selectEvents(String query) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor res = null;
		try {
			res = db.rawQuery(query, null);
		} catch (Exception e){
			e.printStackTrace();
		}
		return res;
	}
}