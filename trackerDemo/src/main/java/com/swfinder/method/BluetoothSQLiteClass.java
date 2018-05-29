package com.swfinder.method;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.swfinder.entity.Bluetooth;
import com.swfinder.helper.SwalleLightSQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class BluetoothSQLiteClass{

	Context context;
	SQLiteDatabase db;
	SwalleLightSQLiteHelper helper;

	public BluetoothSQLiteClass(Context context) {
		this.context = context;
	}

	/**
	 * Insert bluetooth device record
	 * @param bluetooth
	 */
	public void InsertBluetooth(Bluetooth bluetooth) {
		ContentValues values = new ContentValues();
		values.put("bluetooth_name", bluetooth.getName());
		values.put("bluetooth_address", bluetooth.getAddress());
		values.put("bluetooth_toux", bluetooth.getToux());
		
		helper = new SwalleLightSQLiteHelper(context, "SwalleLight.db", null, 1);
		db = helper.getWritableDatabase();
		// Insert data
		db.insert("bluetooth", null, values);
		System.out.println(bluetooth.getName()+" inserted successfully");
	}

	/**
	 * Delete selected Bluetooth device record
	 */
	public void DeleteBluetooth(String bluetooth_address){
		helper = new SwalleLightSQLiteHelper(context, "SwalleLight.db", null, 1);
		db = helper.getWritableDatabase();
		db.delete("bluetooth", "bluetooth_address = ?", new String[]{bluetooth_address});
		System.out.println(bluetooth_address+" deleted successfully");
	}

	/**
	 * Update selected bluetooth device record
	 */
	public void UpdateBluetooth(Bluetooth bluetooth) {
		ContentValues values = new ContentValues();
		values.put("bluetooth_name", bluetooth.getName());
		values.put("bluetooth_address", bluetooth.getAddress());
		values.put("bluetooth_toux", bluetooth.getToux());
		
		helper = new SwalleLightSQLiteHelper(context, "SwalleLight.db", null, 1);
		db = helper.getWritableDatabase();
		// Update data
		db.update("bluetooth", values, "bluetooth_address = ?", new String[] { bluetooth.getAddress() });
		System.out.println(bluetooth.getName()+" updated successfully");
	}

	/**
	 * Search for bluetooth device records
	 */
	public List<Bluetooth> SelectBluetooth() {
		List<Bluetooth> list = new ArrayList<Bluetooth>();
		helper = new SwalleLightSQLiteHelper(context, "SwalleLight.db", null, 1);
		db = helper.getReadableDatabase();
		Cursor cursor = db.query("bluetooth", null, null, null, null, null, null);
		while (cursor.moveToNext()) {
			Bluetooth bluetooth = new Bluetooth();
			String bluetooth_name = cursor.getString(cursor.getColumnIndex("bluetooth_name"));
			String bluetooth_address = cursor.getString(cursor.getColumnIndex("bluetooth_address"));
			
			if(!bluetooth_address.equals("QQQQQQQQ")){
				bluetooth.setName(bluetooth_name);
				bluetooth.setAddress(bluetooth_address);
				
				list.add(bluetooth);
			}
		}
		System.out.println(list.size()+"---");
		cursor.close();
		return list;
	}
	
	/**
	 * Search for selected bluetooth device record
	 */
	public Bluetooth SelectBluetooth(String address) {
		Bluetooth bluetooth = null;
		helper = new SwalleLightSQLiteHelper(context, "SwalleLight.db", null, 1);
		db = helper.getReadableDatabase();
		Cursor cursor = db.query("bluetooth", null, "bluetooth_address = ?", new String[]{address}, null, null, null);
		while (cursor.moveToNext()) {
			bluetooth = new Bluetooth();
			String bluetooth_name = cursor.getString(cursor.getColumnIndex("bluetooth_name"));
			String bluetooth_address = cursor.getString(cursor.getColumnIndex("bluetooth_address"));
			String bluetooth_toux = cursor.getString(cursor.getColumnIndex("bluetooth_toux"));
			
			bluetooth.setName(bluetooth_name);
			bluetooth.setAddress(bluetooth_address);
			bluetooth.setToux(bluetooth_toux);
			
			Log.e("SelectBluetooth", bluetooth_name+"--" + bluetooth_address+ "--" +bluetooth_toux );
		}
		cursor.close();
		return bluetooth;
	}
}
