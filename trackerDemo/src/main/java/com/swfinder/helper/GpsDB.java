package com.swfinder.helper;

import java.util.List;

import com.swfinder.entity.Bluetooth;
import com.swfinder.entity.Gps;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GpsDB {
	/**
	 * Add bluetooth device
	 * @param context
	 *
	 * @param name	device name
	 * @param site 	last positioning
	 * @param time 	last timing
	 * @param jd 	Latitude and Longitude
	 */
	
	public static void addstu (Context context,String name,String site,String time,String address,String jd)throws Exception {
		
		Log.e("addstu", name + "--" + site + "--" +time + "--" + address+"--"+jd);
		SQLiteDatabase db=new Gpssqlite(context).getReadableDatabase();
		Cursor c = db.rawQuery("SELECT MAX(SID) FROM gpsa", null);
		int sid=1;
		if(c.moveToNext()){
			sid=c.getInt(0)+1;
		}
		Object[] v={sid,name,site,time,address,jd};
		db.execSQL("insert into gpsa(sid,name,site,time,address,jd) values(?,?,?,?,?,?)",v);
		
	
	}

	/**
	 * Delete record
	 * @param context
	 * @param i
	 */
	public static void delstu(Context context,String time){
		SQLiteDatabase db=new Gpssqlite(context).getReadableDatabase();
		
		String[] v={time};
		db.execSQL("delete from gpsa where time=?",v);
	}

	/**
	 * Delete all records
	 * @param context
	 * @param i
	 */
	public static void delAll(Context context){
		SQLiteDatabase db=new Gpssqlite(context).getReadableDatabase();
		
		String sql = "delete from gpsa";
		//db.execSQL("delete from gpsa ",null);
		db.execSQL(sql);
	}

	/**
	 * Retrieve all records
	 * @param context
	 */
	public static Cursor queryStudentAll(Context context) {
		SQLiteDatabase db=new Gpssqlite(context).getReadableDatabase();
		
		return db.rawQuery("SELECT * FROM gpsa", null);
		
	}

	/**
	 * Retrieve selected record
	 * @param context
	 * @param Address
	 */
	public static Cursor queryStudent(Context context) {
		SQLiteDatabase db=new Gpssqlite(context).getReadableDatabase();
		Cursor c = db.rawQuery("SELECT MAX(SID) FROM gpsa", null);
		int sid=0;
		if(c.moveToNext()){
			sid=c.getInt(0);
		}
		if(sid!=0){
			String[] v={(sid+"")};
			return db.rawQuery("SELECT * FROM gpsa where sid=?", v);
		}
		return null;
		
	}

	/**
	 * Retrieve selected record
	 * @param context
	 * @param Address
	 */
	public static List<Gps> queryStudent1(Context context,String address) {
		List<Gps> list =null;
		SQLiteDatabase db=new Gpssqlite(context).getReadableDatabase();
		
		Cursor cursor = db.query("gpsa", null, "address = ?", new String[]{address}, null, null, null);
		while (cursor.moveToNext()) {
			
			Log.e("cursor", cursor.getString(1)+"--"+cursor.getString(2)+"--"+cursor.getString(3)+"--"+cursor.getString(4)+"--");
			Gps gps = new Gps();
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String site = cursor.getString(cursor.getColumnIndex("site"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			String mac = cursor.getString(cursor.getColumnIndex("address"));
			String jindu = cursor.getString(cursor.getColumnIndex("jd"));
			gps.setDeviceName(name);
			gps.setLostAddress(site);
			gps.setTime(time);
			gps.setDeviceAddress(mac);
			gps.setJindu(jindu);
			list.add(gps);
		
		}
		cursor.close();
		return list;
		
	}
}
