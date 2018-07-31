package com.trackerdemo.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sw.sdk.BluetoothClass;
import com.sw.sdk.BluetoothClass.BluetoothSearchible;
import com.sw.sdk.Query;
import com.swfinder.adapter.BluetoothDeviceIncreaseAdapter;
import com.swfinder.entity.Bluetooth;
import com.swfinder.method.BluetoothSQLiteClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Use for displaying the contents
 */
public class BluetoothFragment extends android.support.v4.app.Fragment implements OnItemClickListener, OnClickListener, BluetoothSearchible {

	public ListView lv_bluetooth_preserve;
	ListView lv_bluetooth_increase;
	Button bt_bluetooth_search;
	private Button bt_about;
	RelativeLayout rl_bluetooth_whole;
	FrameLayout fl_bluetooth_increase;
	public TextView tv_device_list;
	public TextView tv_search_list;
	Query query;
	Display display;
	Bluetooth bluetooth;
	public BluetoothClass bluetoothClass;
	public BluetoothSQLiteClass bluetoothSQLiteClass;
	public BluetoothDeviceIncreaseAdapter increaseAdapter;
	public BluetoothDevicePreserveAdapter preserveAdapter;
	private onCloseBluetoothFragment closeBluetoothFragment;
	List<Integer> list_isChecked;
	List<Integer> list_amn;
	List<Integer> list_ring;
	public List<BluetoothDevice> list_increase;
	List<BluetoothDevice> list_preserve;
	public static LayoutParams lp_lv_bluetooth_preserve;

	public View view;
	public int amn = 0;

	/** Whether to search for new device  */
	public boolean isIncrease = true;

	/** Check if sent  */
	public boolean isSend = false;

	/** Check if connected */
	public boolean isConnection = true;

	/** Check if pressed */
	public boolean isPrees = false;

	public Animation animation;

	/** Check if long pressed  */
	boolean isLong = false;
	SharedPreferences sp;
	static int screenWidth;   
    static int screenHeight;  
    
    public Handler handler = new Handler() {
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
    		default:
				break;
			}
    	};
    };
    
    public void setonCloseBluetoothFragment (onCloseBluetoothFragment closeBluetoothFragment) {
		this.closeBluetoothFragment = closeBluetoothFragment;
	}

	@Override
	public void onAttach(Activity activity) {
		System.out.println("BluetoothFragment---onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.e("BluetoothFragment", "onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		System.out.println("BluetoothFragment---onCreateView");
		
		initView();

		// Download Data
		initData();
		return null;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		System.out.println("BluetoothFragment---onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		System.out.println("BluetoothFragment---onStart");
		super.onStart();
	}

	@Override
	public void onResume() {
		System.out.println("BluetoothFragment---onResume");
		
		tv_device_list.setText(R.string.device_list);
		tv_search_list.setText(R.string.search_list);
		//MainActivity.getInstance().application.isToux = false;
		
		super.onResume();
	}
	
	@Override
	public void onPause() {
		System.out.println("BluetoothFragment---onPause");
		super.onPause();
	}

	@Override
	public void onStop() {
		System.out.println("BluetoothFragment---onStop");
		super.onStop();
	}

	@Override
	public void onDestroy() {
		System.out.println("BluetoothFragment---onDestroy");
		super.onDestroy();
	}

	@SuppressWarnings("deprecation")
	private void initView() {

		WindowManager m = (WindowManager) getActivity().getSystemService (Context.WINDOW_SERVICE);
		display = m.getDefaultDisplay();
		tv_device_list = (TextView) getActivity().findViewById(R.id.tv_device_list);
		tv_search_list = (TextView) getActivity().findViewById(R.id.tv_search_list);
		lv_bluetooth_preserve = (ListView) getActivity().findViewById(R.id.lv_bluetooth_preserve);
		lv_bluetooth_increase = (ListView) getActivity().findViewById(R.id.lv_bluetooth_increase);
		bt_bluetooth_search = (Button) getActivity().findViewById(R.id.bt_bluetooth_search);
		rl_bluetooth_whole = (RelativeLayout) getActivity().findViewById(R.id.fl_bluetooth_whole);

		LayoutParams lp_fl_bluetooth_whole = rl_bluetooth_whole.getLayoutParams();
		lp_fl_bluetooth_whole.height = (int) (display.getHeight() * 0.06);
		lp_lv_bluetooth_preserve = lv_bluetooth_preserve.getLayoutParams();

		lv_bluetooth_preserve.setOnItemClickListener(this);
		lv_bluetooth_increase.setOnItemClickListener(this);
		bt_bluetooth_search.setOnClickListener(this);
		
		bt_about = (Button) getActivity().findViewById(R.id.bt_about);
		bt_about.setOnClickListener(this);;
	}

	private void initData() {
		sp = MainActivity.getInstance().getSharedPreferences("setting", Context.MODE_PRIVATE);
		list_isChecked = new ArrayList<Integer>();
		list_amn = new ArrayList<Integer>();
		list_ring = new ArrayList<Integer>();
		list_increase = new ArrayList<BluetoothDevice>();
		list_preserve = new ArrayList<BluetoothDevice>();

		bluetoothClass = BluetoothClass.getBluetoothClass();
		bluetoothClass.setBluetoothSearchible(this);

		bluetoothSQLiteClass = new BluetoothSQLiteClass(MainActivity.getInstance());
		increaseAdapter = new BluetoothDeviceIncreaseAdapter(MainActivity.getInstance(),list_increase);
		lv_bluetooth_increase.setAdapter(increaseAdapter);

		if (bluetoothSQLiteClass.SelectBluetooth().size() > 0) {
			lv_bluetooth_preserve.setVisibility(View.VISIBLE);
			List<Bluetooth> list = bluetoothSQLiteClass.SelectBluetooth();

			for (int i = 0; i < list.size(); i++) {
				BluetoothDevice bluetoothDevice = bluetoothClass.bluetoothAdapter.getRemoteDevice(list.get(i).getAddress());
				list_preserve.add(bluetoothDevice);
				list_isChecked.add(1);
				list_amn.add(0);
				list_ring.add(0);
			}
			
			preserveAdapter = new BluetoothDevicePreserveAdapter(MainActivity.getInstance(),list_preserve, list_isChecked,list_amn,list_ring);
			lv_bluetooth_preserve.setAdapter(preserveAdapter);

			isIncrease =false;
			handler.removeCallbacks(MainActivity.getInstance().runnableReconnect );
            handler.postDelayed(MainActivity.getInstance().runnableReconnect , 2*1000);
		}
		
		preserveAdapter = new BluetoothDevicePreserveAdapter(MainActivity.getInstance(),list_preserve, list_isChecked,list_amn,list_ring);
		lv_bluetooth_preserve.setAdapter(preserveAdapter);
	}

	@Override
	public void SearchState(int state) {
		Log.e("SearchState", state+"");

		switch (state) {
		case BluetoothClass.STATE_SEARCH_IN:
			list_increase = new ArrayList<BluetoothDevice>();
			list_preserve = new ArrayList<BluetoothDevice>();
			break;

		case BluetoothClass.STATE_SEARCH_OVER:
			isConnection = false;
			break;
		}
	}

	@Override
	public void BluetoothDevices(final BluetoothDevice device) {
		 
		if (device.getName() != null) {
			if (device.getName().startsWith("All Tracker") || device.getName().startsWith("Biiken")||device.getName().startsWith("iTAG")) {
                if (bluetoothSQLiteClass.SelectBluetooth().size() > 0) {
                    boolean isSame = false;
                    List<Bluetooth> list = bluetoothSQLiteClass.SelectBluetooth();
                    List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();

                    for (int i = 0; i < list.size(); i++) {
                        String device_address = String.valueOf(device.getAddress()).replace(":", "").toLowerCase();
                        String list_address = String.valueOf(list.get(i).getAddress()).replace(":", "").toLowerCase();

                        if (device_address.equals(list_address)) {
                            if (isIncrease) {
                                isSame = true;
                                if (!MainActivity.getInstance().listConnectedDevice.contains(devices)) {
                                    bluetoothClass.connectBluetooth(device);
                                }
                                break;
                            }

                            devices.add(device);
                            preserveAdapter.setState(1, i,0);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (MainActivity.getInstance().isStart) {
                                                if (MainActivity.getInstance().listConnectedDevice.size() <5) {
                                                    MainActivity.getInstance().isStart = false;
                                                    bluetoothClass.connectBluetooth(device);
                                                }
										    }
                                        }
                                    }, 500);
                        }
                    }

                    if (isIncrease) {
                        if (!isSame) {
                            list_increase.add(device);
                            increaseAdapter.setList(list_increase);
                        }
                    }

                } else {
                    list_increase.add(device);
                    increaseAdapter.setList(list_increase);
                }
			}
        }
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_bluetooth_search:
			if (bluetoothClass.isBluetoothOpen()) {
				bt_bluetooth_search.clearAnimation();
				bt_bluetooth_search.startAnimation(AnimationUtils.loadAnimation(MainActivity.getInstance(), R.anim.xhym));
				bluetoothClass.stopSearchBluetooth();
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						isIncrease = true;
						isSend  = true;
						list_increase.clear();
						bluetoothClass.searchBluetooth();
					}
				}, 100);
			}
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		BluetoothDevice device = null;

		switch (parent.getId()) {
		case R.id.lv_bluetooth_increase:
			if (bluetoothClass.isConnectedAll()) {
				bluetoothClass.setConnectedAll(false);
			}

			isSend = false;
			bluetoothClass.stopSearchBluetooth();
			device = list_increase.get(position);
			if (MainActivity.getInstance().listConnectedDevice.size()<=4) {
				isPrees =true;
				bluetoothClass.connectBluetooth(device);
			}

			increaseAdapter.initDat();
			break;

		case R.id.lv_bluetooth_preserve:
			
			if (preserveAdapter.getList_state().get(position) == 3 && !isLong) {
				
				int isam = preserveAdapter.getList_am().get(position);
				BluetoothDevice bluetoothDevice = bluetoothClass.getDevice();
				if(bluetoothDevice!=null){
					if(!bluetoothDevice.equals(preserveAdapter.getList_BluetoothDevice().get(position))){
						byte[] bs = new byte[1];
						bs[0] = 0x00;
						
						if(isam==1 ){
							MainActivity.getInstance().setFlickerAnimation(MainActivity.getInstance().bt_tubiao);
							MainActivity.getInstance().isrun = true;
							 
						}else if(isam==0){
							MainActivity.getInstance().bt_tubiao.clearAnimation();
							MainActivity.getInstance().isrun = false;
						}
					}else{
						
						MainActivity.getInstance().showContent();
						
					}
					
				}
				
				device = preserveAdapter.getList_BluetoothDevice().get(position);

				closeBluetoothFragment.onCloseBluetooth(bluetoothSQLiteClass.SelectBluetooth(device.getAddress()));
				MainActivity.getInstance().getSlidingMenu().showContent();
				MainActivity.getInstance().tv_state.setText(R.string.connect);

				if (MainActivity.getInstance().timer != null) {
					MainActivity.getInstance().timer.cancel();
					MainActivity.getInstance().timer = new Timer();
					MainActivity.getInstance().timer.schedule(new TimerTask() {

						@Override
						public void run() {
							bluetoothClass.getRssi();

						}
					}, 50, 3000);
				} else {
					MainActivity.getInstance().timer = new Timer();
					MainActivity.getInstance().timer.schedule(new TimerTask() {

						@Override
						public void run() {
							bluetoothClass.getRssi();

						}
					}, 50, 3000);
				}

				handler.post(new Runnable() {
					
					@Override
					public void run() {
						MainActivity.getInstance().isSend = false;
						bluetoothClass.readEle();
					}
				});

				bluetoothClass.setDevice(device);
				int postion = preserveAdapter.getList_BluetoothDevice().indexOf(device);
				preserveAdapter.setCurrentDevice(postion);
				
			} else if(preserveAdapter.getList_state().get(position) ==1 && !isLong) {

				device = preserveAdapter.getList_BluetoothDevice().get(position);
				
				MainActivity.getInstance().getSlidingMenu().showContent();
				int postion = preserveAdapter.getList_BluetoothDevice().indexOf(device);
				bluetoothClass.setDevice(device);
				preserveAdapter.setCurrentDevice(postion);
				MainActivity.getInstance().tv_state.setText(R.string.disconnect);
				
				MainActivity.getInstance().iv_electricity.setVisibility(View.INVISIBLE);
				MainActivity.getInstance().tv_dis.setText("--");
				MainActivity.getInstance().tv_rssi.setText("--");
				isIncrease =false;
				bluetoothClass.searchBluetooth();
			}
			break;
		}
	}

	public interface onCloseBluetoothFragment {
        void onCloseBluetooth(Bluetooth bluetooth);
	}

	public static int getStatusHeight(Activity activity) {
		int statusHeight = 0;
		Rect localRect = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
		statusHeight = localRect.top;

        if (0 == statusHeight) {
			Class<?> localClass;
			try {
				localClass = Class.forName("com.android.internal.R$dimen");
				Object localObject = null;
				try {
					localObject = localClass.newInstance();
				} catch (java.lang.InstantiationException e) {
					e.printStackTrace();
				}
				int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
				statusHeight = activity.getResources().getDimensionPixelSize(i5);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		return statusHeight;
	}

	/**
	 * Animation
	 * @param iv_chat_head animation
	 */
	public void setFlickerAnimation(ImageView iv_chat_head) {
		
		Log.e("setFlickerAnimation", "setFlickerAnimation");
		animation = new AlphaAnimation(1, 0); // Change alpha from fully visible
												// to invisible
		animation.setDuration(500); // duration - half a second
		animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
		animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
		animation.setRepeatMode(Animation.REVERSE); //
		iv_chat_head.startAnimation(animation);
	}

	/**
	 *  Saved bluetooth device adapter
	 */
	public class BluetoothDevicePreserveAdapter extends BaseAdapter {
		private float x,ux;
		Display display;
		Context context;

		// Current bluetooth location
		int index = -1;

		// List of device status
		List<Integer> list_state;

        // List of image animations
		List<Integer> list_am;

        // List of sounds
		List<Integer> list_device_ring;
		List<BluetoothDevice> list_BluetoothDevice;
		List<Integer> current_device;
		BluetoothClass bluetoothClass;
		public BluetoothSQLiteClass sqLiteClass;

		public void add(BluetoothDevice device, int isChecked,int isam,int isring) {
			list_BluetoothDevice.add(device);
			list_state.add(isChecked);
			list_am.add(isam);
			list_device_ring.add(isring);
			this.notifyDataSetChanged();
		}

		public void remove(int index) {
			list_BluetoothDevice.remove(index);
			list_state.remove(index);
			list_am.remove(index);
			list_device_ring.remove(index);
			this.notifyDataSetChanged();
		}

		public List<BluetoothDevice> getList_BluetoothDevice() {
			return list_BluetoothDevice;
		}

		public List<Integer> getList_state() {
			return list_state;
		}
		public List<Integer> getList_am() {
			return list_am;
		}
		public List<Integer> getList_ring() {
			return list_device_ring;
		}
		public void setCurrentDevice(int position) {
			index = position;
			this.notifyDataSetChanged();
		}

		/**
		 * Set the current status
		 *
		 * @param state 0-Not exist 1-Exist 2-Connecting 3-Connected Successfully
		 */
		public boolean setState(int state, int id,int am) {
			amn = am;
			list_state.set(id, state);
			this.notifyDataSetChanged();
			if (list_state.indexOf(0) == -1) {
				return true;
			}
			return false;
		}

		/**
		 * Set the current animation
		 * @param am 0-Not in animation，1-In animation
		 */
		public void setAm(int am,int postion){
			list_am.set(postion, am);
			this.notifyDataSetChanged();
		}
		
		public void initData() {
			for (int i = 0; i < list_state.size(); i++) {
				if (list_state.get(i) == 1) {
					list_state.set(i, 1);
				}
			}
			this.notifyDataSetChanged();
		}

		/**
		 * Saved bluetooth device adapter constructor
		 * @param context context
		 * @param list_BluetoothDevice Bluetooth devices list
		 * @param list_state   Bluetooth device states list
		 * @param list_amn     Bluetooth device animations list
		 */
		public BluetoothDevicePreserveAdapter(Context context,
				List<BluetoothDevice> list_BluetoothDevice,
				List<Integer> list_state,List<Integer> list_amn,List<Integer> list_ring) {
			this.context = context;
			this.list_state = list_state;
			this.list_am = list_amn;
			this.list_device_ring = list_ring;
			this.list_BluetoothDevice = list_BluetoothDevice;
			bluetoothClass = BluetoothClass.getBluetoothClass();
			sqLiteClass = new BluetoothSQLiteClass(context);
			WindowManager m = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
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

		@SuppressLint({ "ViewHolder", "InflateParams" })
		@SuppressWarnings("deprecation")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			Log.e("getView", "getView");
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(R.layout.bluetooth_preserve_listview, null);

            ImageView iv  = (ImageView) view.findViewById(R.id.iv);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
		
			Button bt_bluetooth_disconnect = (Button) view.findViewById(R.id.bt_bluetooth_disconnect);
			final BluetoothDevice device = list_BluetoothDevice.get(position);
			final Bluetooth bluetooth = sqLiteClass.SelectBluetooth(device.getAddress());

			if (index!=-1) {
				if(index==position) {
					Log.e("index", MainActivity.getInstance().isrun+"");
					if (MainActivity.getInstance().isrun) {
						iv.clearAnimation();
						setFlickerAnimation(iv);
					} else {
						iv.clearAnimation();
					}
				
					tv_name.setTextColor(Color.parseColor("#ffa500"));
				} else {

				}
			}
			
			if (getList_am().get(position)==1) {
				iv.clearAnimation();
				setFlickerAnimation(iv);
			}

			if(getList_am().get(position)==0 ) {
				iv.clearAnimation();
			}

			if (list_state.get(position) == 0) {

			} else if (list_state.get(position) == 1) {
				
				bt_bluetooth_disconnect.setVisibility(View.GONE);
				
			} else if (list_state.get(position) == 2) {
				
				bt_bluetooth_disconnect.setVisibility(View.GONE);
				
			} else if (list_state.get(position) == 3) {
				
				if (MainActivity.getInstance().listConnectedDevice.size()==1) {
					
				} else {
					if (index!=position) {
						tv_name.setTextColor(Color.WHITE);
					}
				}

				BluetoothDevice bluetoothDevice  = bluetoothClass.getDevice();

                if (bluetoothDevice!=null) {
					int current = list_BluetoothDevice.indexOf(bluetoothDevice);
					if (current==position) {
						tv_name.setTextColor(Color.parseColor("#ffa500"));
					}
				}

				bt_bluetooth_disconnect.setVisibility(View.VISIBLE);
			}

			if (bluetooth == null) {
				tv_name.setText( device.getName());
			} else {
				tv_name.setText( bluetooth.getName());
			}

		((ViewGroup) view).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
			return view;
		}
	}
}