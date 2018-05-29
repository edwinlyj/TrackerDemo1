package com.swfinder.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trackerdemo.activity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Search Bluetooth Device Adapter
 */
public class BluetoothDeviceIncreaseAdapter extends BaseAdapter{

	Context context;
	List<BluetoothDevice> list;
	
	public void setList(List<BluetoothDevice> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public BluetoothDeviceIncreaseAdapter(Context context, List<BluetoothDevice> list) {
		this.context = context;
		this.list = list;
	}
	
	public void initDat(){
		list = new ArrayList<BluetoothDevice>();
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.bluetooth_increase_listview, null);
		TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
		//TextView tv_name = (TextView) view.findViewById(R.id.tv_bluetooth_listview_name);
		ImageView iv_bluetooth_listview_item = (ImageView) view.findViewById(R.id.iv_bluetooth_listview_item);
		
		WindowManager m = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE); 
		Display display = m.getDefaultDisplay();
		
		LayoutParams lp = iv_bluetooth_listview_item.getLayoutParams();
		lp.height = (int) (display.getHeight()*0.06);
		
		BluetoothDevice device = list.get(position);

		tv_name.setText(device.getName());
		return view;
	}
	
	public String macToNumber(String mac) {
		
		String [] result  = mac.split(":");
		StringBuilder builder = new StringBuilder();
		builder.append("4241");
		for(int i=0;i<result.length;i++) {
			builder.append(result[i]);
		}
		
		return builder.toString();
		
	}
}
