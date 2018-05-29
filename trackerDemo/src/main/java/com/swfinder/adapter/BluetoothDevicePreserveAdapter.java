package com.swfinder.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.sw.sdk.BluetoothClass;
import com.swfinder.entity.Bluetooth;
import com.swfinder.method.BluetoothSQLiteClass;
import com.trackerdemo.activity.R;

import java.util.List;

/**
 * Saved Bluetooth Device Adapter
 */
public class BluetoothDevicePreserveAdapter extends BaseAdapter{

	Display display;
	Context context;
	List<Integer> list_state;
	List<BluetoothDevice> list_BluetoothDevice;
	
	BluetoothClass bluetoothClass;
	BluetoothSQLiteClass sqLiteClass;
	
	public void add(BluetoothDevice device, int isChecked) {
		list_BluetoothDevice.add(device);
		list_state.add(isChecked);
		
		this.notifyDataSetChanged();
	}
	
	public void remove(int index){
		list_BluetoothDevice.remove(index);
		list_state.remove(index);
		
		this.notifyDataSetChanged();
	}

	public List<BluetoothDevice> getList_BluetoothDevice() {
		return list_BluetoothDevice;
	}

	public List<Integer> getList_state() {
		return list_state;
	}

	/**
	 * Set the current status
	 * @param state 0-Not exist 1-Exist 2-Connecting 3-Connect Successfully
	 */
	public boolean setState(int state, int id) {
		list_state.set(id, state);
		this.notifyDataSetChanged();
		if(list_state.indexOf(0) == -1){
			return true;
		}
		return false;
	}
	
	public void initData(){
		for (int i = 0; i < list_state.size(); i++) {
			if(list_state.get(i) == 1){
				list_state.set(i, 0);
			}
		}
		this.notifyDataSetChanged();
	}
	
	public BluetoothDevicePreserveAdapter(Context context, List<BluetoothDevice> list_BluetoothDevice, List<Integer> list_state) {
		this.context = context;
		this.list_state = list_state;
		this.list_BluetoothDevice = list_BluetoothDevice;
		bluetoothClass = BluetoothClass.getBluetoothClass();
		sqLiteClass = new BluetoothSQLiteClass(context);
		WindowManager m = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		display = m.getDefaultDisplay();
	}
	
	@Override
	public int getCount() {
		return list_BluetoothDevice.size();
	}

	@Override
	public Object getItem(int position) {
		return list_BluetoothDevice.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.bluetooth_preserve_listview, null);

		Button bt_bluetooth_disconnect = (Button) view.findViewById(R.id.bt_bluetooth_disconnect);

		ImageView iv_toux = (ImageView) view.findViewById(R.id.iv_bluetooth_listview_item);
		WindowManager m = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		display = m.getDefaultDisplay();
		
		BluetoothDevice device = list_BluetoothDevice.get(position);
		Bluetooth bluetooth = sqLiteClass.SelectBluetooth(device.getAddress());
		
		if(list_state.get(position) == 0){
			bt_bluetooth_disconnect.setVisibility(View.GONE);
		}else if (list_state.get(position) == 1) {
			bt_bluetooth_disconnect.setVisibility(View.GONE);
		}else if (list_state.get(position) == 2) {
			bt_bluetooth_disconnect.setVisibility(View.GONE);
		}else if (list_state.get(position) == 3){
			bt_bluetooth_disconnect.setVisibility(View.VISIBLE);
		}
		
		bt_bluetooth_disconnect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setState(1, position);
				bluetoothClass.stopSearchBluetooth();
				bluetoothClass.disconnectBluetooth(list_BluetoothDevice.get(position));
			}
		});
		((ViewGroup)view).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		return view;
	}
}
