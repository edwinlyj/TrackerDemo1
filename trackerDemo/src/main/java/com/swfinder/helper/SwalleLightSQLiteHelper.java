package com.swfinder.helper;



import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SwalleLightSQLiteHelper extends SQLiteOpenHelper{
	
	ContentValues values;
	Context context;

	public SwalleLightSQLiteHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		this.context = context;
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table bluetooth(id integer primary key autoincrement, bluetooth_name varchar(12),"
				+ " bluetooth_address varchar(20), bluetooth_toux varchar(20),bluetooth_white integer, bluetooth_color integer, "
				+ "bluetooth_red integer, bluetooth_green integer, bluetooth_blue integer, state integer)");
		db.execSQL("create table lamp(id integer primary key autoincrement, lamp_name varchar(12), lamp_mode integer,"
				+ " lamp_white integer, lamp_color integer, lamp_red integer, lamp_green integer, lamp_blue integer, state integer)");
		values = new ContentValues();
		values.put("bluetooth_name", "aaa");
		values.put("bluetooth_address", "QQQQQQQQ");
		values.put("bluetooth_toux", "ttt");
		
		db.insert("bluetooth", null, values);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
}
