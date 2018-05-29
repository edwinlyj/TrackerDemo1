package com.trackerdemo.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnCloseListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.sw.sdk.BluetoothClass;
import com.sw.sdk.BluetoothClass.BluetoothConnectible;
import com.swfinder.entity.Bluetooth;
import com.swfinder.entity.TipHelp;
import com.swfinder.method.BluetoothSQLiteClass;
import com.trackerdemo.activity.BluetoothFragment.onCloseBluetoothFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends SlidingFragmentActivity implements OnOpenListener, OnCloseListener,
		OnClickListener, BluetoothConnectible, onCloseBluetoothFragment {

    private static final String TAG = "MainActivity";

	public Button bt_menu;
	public Button bt_tubiao;
    public TextView tv_state; // Connection status
    public TextView tv_dis; // Distance (Far, Mid, Near)
    public TextView tv_rssi; // Signal (Strong, Mid, Weak)
    public ImageView iv_electricity; // Power

    public MediaPlayer player = null; // Alarm music
    public MediaPlayer player_search = null; // Find music
    public MediaPlayer player_dis = null; // Exceed range music
    public MediaPlayer player_connect = null; // Connect music

    public BluetoothClass bluetoothClass;
    public boolean isSend = true;
    public Timer timer = new Timer(); // Read signal timer

	public Handler handler = new Handler();
	public Animation animation;

    public List<BluetoothDevice> listConnectedDevice = new ArrayList<BluetoothDevice>(); // Connected Bluetooth Devices
    public static MainActivity getInstance() { return mainActivity; }

    public double longitude = 0;
    public double latitude = 0;
    public String address;

    private SharedPreferences sp;
    private BluetoothDevice lost_device;
    private BluetoothDevice connectDevice; // Connect device
    private BluetoothDevice disconnectDevice; // Disconnect device
    private String lost_address;
    private List<BluetoothDevice> lost_list = new ArrayList<BluetoothDevice>();

	static MainActivity mainActivity;
    Display display;
    Bluetooth bluetooth;
    BluetoothSQLiteClass bluetoothSQLiteClass;
    BluetoothFragment bluetoothFragment;

    boolean isrun = false; // Whether app is finding device
    boolean isStart = true;
    boolean isLeftOpen = false; // Whether left menu is opened
    boolean isActivity = false; // Whether to quit program

    int x=0;
    int y=0;
    int z=0;
    int rssi = 0; // Distance
    long mExitTime = 0;


    /**
     * Play alarm music
     */
    private Runnable task = new Runnable() {
        public void run() {
            if (player != null) {
                play(sp.getString("name", "alarm_fire.mp3"));
            }
        }
    };

    /**
     * Play searched music
     */
    private Runnable task_search = new Runnable() {
        public void run() {
            if (player_search != null) {
                play_search("connect3.mp3");
            }
        }
    };

    /***
     * Reconnection
     */
    public Runnable runnableReconnect = new Runnable() {
        @Override
        public void run() {
            if (bluetoothFragment.preserveAdapter.getList_state().contains(1) || bluetoothFragment.preserveAdapter.getList_state().contains(0)) {
                handler.postDelayed(this, 13 * 1000);
                bluetoothClass.searchBluetooth();
            }
        }
    };

    @Override
	public void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mainActivity = this;
		WindowManager m = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		display = m.getDefaultDisplay();

		initSlidingMenu(); // initiate the sliding menu
		initView(); // initiate the main
		initData();
	}

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

	@SuppressWarnings("deprecation")
	private void initSlidingMenu() {

		getSlidingMenu().setMode(SlidingMenu.LEFT);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		getSlidingMenu().setBehindWidth((int) (display.getWidth() * 0.8));
		getSlidingMenu().toggle();

		bluetoothFragment = new BluetoothFragment();

        // Set the left view of the sliding menu
		setBehindContentView(R.layout.bluetooth);
		getSupportFragmentManager().beginTransaction().replace(R.id.bluetooth1, bluetoothFragment).commit();
		getSlidingMenu().setOnOpenListener(this);
		getSlidingMenu().setOnCloseListener(this);
	}

	private void initView() {

		tv_dis = (TextView) findViewById(R.id.tv_dis); // distance (near, mid, far)
		tv_state = (TextView) findViewById(R.id.tv_state); // connectivity state (connected, disconnected)
		tv_rssi = (TextView) findViewById(R.id.tv_rssi); // signal strength (weak, mid, strong)
		iv_electricity = (ImageView) findViewById(R.id.iv_electricity); // battery power
		bt_menu = (Button) findViewById(R.id.bt_menu); // menu button (top)
		bt_tubiao = (Button) findViewById(R.id.bt_tubiao); // paging button (bottom)
		bt_menu.setOnClickListener(this);
		bt_tubiao.setOnClickListener(this);
	}

	private void initData() {

        sp = getSharedPreferences("setting", Context.MODE_PRIVATE);

		bluetoothFragment.setonCloseBluetoothFragment(this);
		bluetoothSQLiteClass = new BluetoothSQLiteClass(this);
		bluetoothClass = BluetoothClass.Initialize(this);
		bluetoothClass.setBluetoothConnectible(this);

		player = new MediaPlayer();
		player_search = new MediaPlayer();
		player_connect = new MediaPlayer();

		player_search.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				play_search("connect3.mp3");
			}
		});

		if (bluetoothClass.isBluetoothOpen()) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					//getSlidingMenu().showMenu();
				}
			}, 100);
		}
	}

	/**
	 * Play alarm sound
	 * 
	 * @param pat
	 */
	public void play(String pat) {

	    try {
			player.pause();
			player.stop();
			player.reset();
			AssetFileDescriptor afd = this.getAssets().openFd(pat);
			player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			player.prepare();

			player.start();
			player.seekTo(0);

		} catch (Exception e) {

		}
	}

	/**
	 * Play search sound
	 * 
	 * @param pat
	 */
	public void play_search(String pat) {
		Log.e("play_search", "play_search");

		try {
			player_search.pause();
			player_search.stop();
			player_search.reset();
			AssetFileDescriptor afd = this.getAssets().openFd(pat);
			player_search.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),afd.getLength());
			player_search.prepare();
			player_search.start();
			player_search.seekTo(0);
		} catch (Exception e) {

		}
	}

	/**
	 * Play exceed distance sound
	 * 
	 * @param pat
	 */
	public void play_dis(String pat) {
		Log.e("play_search", "play_search");

		try {
			player_dis.pause();
			player_dis.stop();
			player_dis.reset();
			AssetFileDescriptor afd = this.getAssets().openFd(pat);
			player_dis.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			player_dis.prepare();
			player_dis.start();
			player_dis.seekTo(0);
		} catch (Exception e) {

		}
	}
	
	/**
	 * Play connected successfully sound
	 * 
	 * @param pat
	 */
	public void play_connect(String pat) {

	    try {
			player_connect.pause();
			player_connect.stop();
			player_connect.reset();
			AssetFileDescriptor afd = this.getAssets().openFd(pat);
			player_connect.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			player_connect.prepare();
			player_connect.start();

		} catch (Exception e) {

		}
	}

	@Override
	protected void onStart() {
        Log.d(TAG, "onStart");
		super.onStart();
	}

	@Override
	protected void onRestart() {
        Log.d(TAG, "onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		if (!bluetoothClass.isBluetoothOpen()) {
			displayOpenBluetooth();
		}
		super.onResume();
	}

    @Override
    public void onOpen() {

	    if(bluetoothFragment.preserveAdapter!=null) {
            bluetoothFragment.preserveAdapter.notifyDataSetChanged();
        }

        isLeftOpen = true;
        if (bluetoothClass.isBluetoothOpen()) {
            if (bluetoothSQLiteClass.SelectBluetooth().size() > 0) {
                bluetoothFragment.isIncrease = false;
            }
            if (bluetoothSQLiteClass.SelectBluetooth().size() != listConnectedDevice
                    .size()
                    && bluetoothSQLiteClass.SelectBluetooth().size() != 0) {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        bluetoothFragment.isIncrease = false;

                        bluetoothClass.searchBluetooth();
                    }
                }, 500);
            }
        } else {

        }
    }

    @Override
    public void onClose() {

	    if (isLeftOpen) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    if(bluetoothFragment.preserveAdapter!=null) {
                        bluetoothFragment.preserveAdapter.initData();
                    }

                    if (bluetoothFragment.increaseAdapter.getCount() > 0) {
                        bluetoothFragment.increaseAdapter.initDat();
                    }
                }
            }, 500);

            if (bluetoothClass.isBluetoothOpen()) {
                bluetoothClass.stopSearchBluetooth();
            }
            if (bluetoothClass.getBluetoothConnectedState() == BluetoothClass.STATE_CONNECTED) {
                if (bluetoothClass.isConnectedAll()) {
                    bluetooth = bluetoothSQLiteClass.SelectBluetooth("QQQQQQQQ");
                } else {
                    bluetooth = bluetoothSQLiteClass.SelectBluetooth(bluetoothClass.getDevice().getAddress());
                }

            }
            isLeftOpen = false;
        }
    }

    @Override
    protected void onPause() {
        System.out.println("MainActivity---onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        System.out.println("MainActivity---onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {

	    System.out.println("MainActivity---onDestroy");
        isActivity = true;
        if (player != null && player.isPlaying()) {
            player.stop();
            player.reset();
        }
        if (player_search != null && player_search.isPlaying()) {
            player_search.stop();
            player_search.reset();
        }
        if (player_connect != null && player_connect.isPlaying()) {
            player_connect.stop();
            player_connect.reset();
        }
        if (timer != null) {
            timer.cancel();
        }

        handler.removeCallbacks(task);
        bluetoothClass.disconnectBluetooth();

        handler.removeCallbacks(runnableReconnect);

        System.exit(0);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK && !isLeftOpen) {

            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, R.string.main_quit, Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();

            } else {
                finish();
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /***
     * Action after disconnected
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.bt_menu:
                getSlidingMenu().showMenu();
                break;

            case R.id.bt_tubiao:
                BluetoothDevice current_device  = bluetoothClass.getDevice();

                if(current_device!=null) {
                    int postion  = bluetoothFragment.preserveAdapter.getList_BluetoothDevice().indexOf(current_device);

                    if(postion!=-1){
                        int state = bluetoothFragment.preserveAdapter.getList_state().get(postion);

                        if(state!=3) {
                            bt_tubiao.clearAnimation();
                            bluetoothFragment.preserveAdapter.setAm(0, postion);

                            if(player!=null && player.isPlaying()) {
                                player.stop();
                                player.reset();
                            }

                        } else {
                            final byte[] bs = new byte[1];

                            if (isrun) {
                                isrun = false;
                                if(isSend){

                                    bs[0] = 0x00;

                                    bt_tubiao.clearAnimation();
                                    handler.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            bluetoothClass.sendData(bs);
                                        }
                                    });
                                }

                                bs[0] = 0x00;

                                int postion1 = bluetoothFragment.preserveAdapter.getList_BluetoothDevice().indexOf(bluetoothClass.getDevice());
                                if (postion1!=-1) {
                                    ImageView iv = (ImageView) bluetoothFragment.lv_bluetooth_preserve.getChildAt(postion1).findViewById(R.id.iv);

                                    if(iv!=null) {
                                        iv.clearAnimation();// Remove left interface blinking image
                                        bluetoothFragment.preserveAdapter.setAm(0, postion1);
                                    }
                                }

                                List<Integer> am = new ArrayList<Integer>();
                                List<BluetoothDevice> list1 = bluetoothFragment.preserveAdapter.getList_BluetoothDevice();

                                for (int i=0;i<list1.size();i++) {
                                    if(bluetoothFragment.preserveAdapter.getList_state().get(i)==3 ){
                                        int isam = bluetoothFragment.preserveAdapter.getList_am().get(i);
                                        am.add(isam);
                                    }
                                }

                                if (!am.contains(1)) {
                                    if (player_search != null && player_search.isPlaying()) {
                                        player_search.stop();
                                        player_search.reset();
                                    }

                                    TipHelp.stop();
                                }

                            } else {
                                if(isSend){
                                    isrun = true;
                                    setFlickerAnimation(bt_tubiao);

                                    bs[0] = 0x02;

                                    handler.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            bluetoothClass.sendData(bs);

                                        }
                                    });
                                }
                            }
                        }
                    }
                }

                break;
        }
    }

    /***
	 * Display connection status
	 */
	@SuppressLint("SimpleDateFormat")
	@Override
	public void ConnectState(int state, final BluetoothDevice device) {

		int id;
		switch (state) {
			case BluetoothClass.STATE_CONNECTION:
				break;

			case BluetoothClass.STATE_CONNECTED:
				isStart = true;
				isrun = false;
				connectDevice = device;
				bt_tubiao.clearAnimation();

				if(!bluetoothFragment.isPrees) {
					bluetoothClass.stopSearchBluetooth();
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							bluetoothClass.searchBluetooth();
						}
					}, 1000);
				}

				tv_state.setText(R.string.connect);

				if (!listConnectedDevice.contains(device)) {
					listConnectedDevice.add(device);
				}

				if (bluetoothFragment.preserveAdapter!=null) {
					bluetoothClass.setDevice(device);
				}

				bluetooth = bluetoothSQLiteClass.SelectBluetooth(device.getAddress());
				if (bluetooth == null) {
					bluetoothFragment.preserveAdapter.add(device, 3, 0,0);
					bluetoothFragment.lv_bluetooth_preserve.setVisibility(View.VISIBLE);
					bluetooth = new Bluetooth();
					bluetooth.setName(device.getName());
					bluetooth.setAddress(device.getAddress());
					bluetoothSQLiteClass.InsertBluetooth(bluetooth);
				} else {
					id = bluetoothFragment.preserveAdapter.getList_BluetoothDevice().indexOf(device);
					bluetoothFragment.preserveAdapter.setState(3, id, 0);
				}

				int postion = bluetoothFragment.preserveAdapter.getList_BluetoothDevice().indexOf(device);
				bluetoothFragment.preserveAdapter.setCurrentDevice(postion);
				Bluetooth bluetooth = bluetoothSQLiteClass.SelectBluetooth(device.getAddress());
				Log.d("postion", postion + "---" +bluetooth);
				Log.d("STATE_CONNECTED", postion + "---" + sp.getString("set_open", "true"));

                if (timer != null) {
					timer.cancel();
					timer = new Timer();
					timer.schedule(new TimerTask() {

						@Override
						public void run() {
							bluetoothClass.getRssi();
						}
					}, 50, 1000);
				} else {
					timer = new Timer();
					timer.schedule(new TimerTask() {

						@Override
						public void run() {
							bluetoothClass.getRssi();

						}
					}, 50, 1000);
				}

				handler.post(new Runnable() {
					@Override
					public void run() {
						isSend = false;
						bluetoothClass.readEle();
					}
				});

				Log.d("ConnectState", rssi + "");
				play_connect("link.mp3");
				getSlidingMenu().showContent();
                Log.d(TAG, device.getName() + " connected successfully");
				break;

			case BluetoothClass.STATE_DISCONNECT:
				listConnectedDevice.remove(device);
				System.out.println(listConnectedDevice.size());

				if (listConnectedDevice.size() == 0) {

					tv_state.setText(R.string.disconnect);

					iv_electricity.setVisibility(View.INVISIBLE);
					tv_rssi.setText("--");
					tv_dis.setText("--");
				}

				id = bluetoothFragment.preserveAdapter.getList_BluetoothDevice().indexOf(device);
				if (id != -1) {
					bluetoothFragment.preserveAdapter.setState(1, id, 0);
				}

				Log.d(TAG, device.getName() + " disconnected");
				break;

		case BluetoothClass.STATE_CONNECTION_FAILED:
			id = bluetoothFragment.preserveAdapter.getList_BluetoothDevice().indexOf(device);
			Log.e("STATE_CONNECTION_FAILED", id+"");

			if (id != -1) {
				bluetoothFragment.preserveAdapter.setState(1, id, 0);
				tv_state.setText(R.string.disconnect);
			} else {
				Toast.makeText(MainActivity.this, R.string.disconnect, Toast.LENGTH_SHORT).show();
			}

			isStart = true;
			break;

		case BluetoothClass.STATE_LOSE_CONNECTION:
			isStart = true;
			Log.d(TAG, "STATE_LOSE_CONNECTION" + device.getName() + "--" + device.getAddress());
			id = bluetoothFragment.preserveAdapter.getList_BluetoothDevice().indexOf(device);

			if (id != -1) {
				bluetoothFragment.isIncrease = false;
				bluetoothClass.searchBluetooth();
				handler.removeCallbacks(runnableReconnect );
	            handler.postDelayed(runnableReconnect , 13 * 1000);
				
			} else {
				Toast.makeText(MainActivity.this, R.string.disconnect, Toast.LENGTH_SHORT).show();
			}
		
			listConnectedDevice.remove(device);
			
			Log.d(TAG, "list:" + listConnectedDevice.size() + device.getName());
			id = bluetoothFragment.preserveAdapter.getList_BluetoothDevice().indexOf(device);
			Log.d("STATE_CONNECTION_FAILED", id+"");

			if (device.getName() != null && bluetoothClass.getDevice()!=null &&  bluetoothClass.getDevice().getAddress() != null && device.getAddress().equals(bluetoothClass.getDevice().getAddress())) {
				tv_state.setText(R.string.disconnect);
				iv_electricity.setVisibility(View.INVISIBLE);
				tv_rssi.setText("--");
				tv_dis.setText("--");
				setFlickerAnimation(bt_tubiao);
			}
			
			final int currentDevic = bluetoothFragment.preserveAdapter.getList_BluetoothDevice().indexOf(device);

			if (currentDevic != -1) {
				bluetoothFragment.preserveAdapter.notifyDataSetChanged();
			}

			id = bluetoothFragment.preserveAdapter.getList_BluetoothDevice().indexOf(device);
			if (id != -1) {
				bluetoothFragment.preserveAdapter.setState(1, id, 0);
			}

			Log.d(TAG, device.getName() + " disconnected");
			break;
		}
	}

	/**
	 * Blinking image
	 * @param iv_chat_head
	 */
	public void setFlickerAnimation(Button iv_chat_head) {

	    animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
		animation.setDuration(500); // duration - half a second
		animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
		animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
		animation.setRepeatMode(Animation.REVERSE);
		iv_chat_head.startAnimation(animation);
	}

	/**
     * Dynamically display the status of the currently connected device
     * */
	public void initBluetooth(Bluetooth bluetooth) {
		if (bluetoothClass.isConnectedAll()) {

		} else {

		}
	}

    /**
     * Open bluetooth dialog
     */
    public void displayOpenBluetooth(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage(R.string.main_bluetooth_open);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.main_open, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);

            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
            }
        });
        builder.show();
    }

    @Override
	public void onCloseBluetooth(Bluetooth bluetooth) { initBluetooth(bluetooth); }

    @Override
    public void sendData(byte[] data, BluetoothDevice bluetoothDevice) {

        Log.d("sendData", data[0] + "--" + bluetoothDevice.getName());
        //If current device
        if (bluetoothClass.getDevice()!= null && bluetoothDevice.getAddress().equals(bluetoothClass.getDevice().getAddress())) {
            int postion1 = bluetoothFragment.preserveAdapter.getList_BluetoothDevice().indexOf(bluetoothDevice);
            ImageView iv = (ImageView) bluetoothFragment.lv_bluetooth_preserve.getChildAt(postion1).findViewById(R.id.iv);
            if (data[0] == 1) {
                if (isrun) { //Phone is playing a search sound
                    isrun = false;

                    bt_tubiao.clearAnimation();
                    if(iv!=null){
                        iv.clearAnimation();
                    }

                    byte[] bs = new byte[1];
                    if (isrun) {
                        bs[0] = 0x02;
                    } else {
                        bs[0] = 0x00;
                    }

                    bluetoothFragment.preserveAdapter.setAm(0, postion1);
                    bluetoothClass.sendData(bs);

                    List<Integer> am = new ArrayList<Integer>();
                    List<BluetoothDevice> list = bluetoothFragment.preserveAdapter.getList_BluetoothDevice();

                    for (int i=0;i<list.size();i++) {
                        if (bluetoothFragment.preserveAdapter.getList_state().get(i)==3) {
                            int isam = bluetoothFragment.preserveAdapter.getList_am().get(i);
                            am.add(isam);
                        }
                    }

                    if (!am.contains(1)) {
                        if (player_search != null && player_search.isPlaying()) {
                            player_search.stop();
                            player_search.reset();
                        }
                    }

                } else { //Phone is not playing a search sound
                    setFlickerAnimation(bt_tubiao);
                    bluetoothFragment.preserveAdapter.setAm(1, postion1);

                    if (sp.getString("set_open", "true").equals("true")) {
                        handler.removeCallbacks(task_search);

                        if (player_search != null && player_search.isPlaying()) {

                        } else {
                            if (sp.getString("set_open", "true").equals("true")) {
                                play_search("connect3.mp3");
                            }
                        }
                    }

                    isrun = true;
                    if (sp.getString("set_vibrate", "false").equals("true")) {
                        long[] pattern={500,500,500,500};
                        TipHelp.Vibrate(mainActivity, pattern, 0);
                    }

                    bluetoothFragment.setFlickerAnimation(iv);
                }
            }
        }

        //Not current device
        else {
            int postion1 = bluetoothFragment.preserveAdapter.getList_BluetoothDevice().indexOf(bluetoothDevice);
            ImageView iv = (ImageView) bluetoothFragment.lv_bluetooth_preserve.getChildAt(postion1).findViewById(R.id.iv);
            int isam = bluetoothFragment.preserveAdapter.getList_am().get(postion1);
            Log.d("isam", isam + "");

            if (data[0] == 1) {
                if (isam == 1) { //Image is blinking
                    if (iv!=null) {
                        iv.clearAnimation();
                    }

                    isam = 0;
                    bluetoothFragment.preserveAdapter.setAm(isam, postion1);
                    List<Integer> am1 = new ArrayList<Integer>();
                    List<BluetoothDevice> list = bluetoothFragment.preserveAdapter.getList_BluetoothDevice();

                    for (int i=0;i<list.size();i++) {
                        if(bluetoothFragment.preserveAdapter.getList_state().get(i)==3 ) {
                            int isam1 = bluetoothFragment.preserveAdapter.getList_am().get(i);
                            am1.add(isam1);
                        }
                    }

                    if (!am1.contains(1)) {
                        if (player_search != null && player_search.isPlaying()) {
                            player_search.stop();
                            player_search.reset();
                        }

                        TipHelp.stop();
                    }

                } else {  //Image is not blinking
                    bluetoothFragment.setFlickerAnimation(iv);
                    isam = 1;
                    bluetoothFragment.preserveAdapter.setAm(isam, postion1);

                    if (player_search != null && player_search.isPlaying()) {

                    } else {

                        if (sp.getString("set_open", "true").equals("true")) {
                            play_search("connect3.mp3");
                        }

                        if (sp.getString("set_vibrate", "false").equals("true")) {
                            long[] pattern={500,500,500,500};
                            TipHelp.Vibrate(mainActivity, pattern, 0);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void getrssi(int rssi) {

        if (rssi >= -80) {
            x++;
            y = 0;
            z = 0;
        }

        if (rssi >= -90 && rssi < -80) {
            y++;
            x = 0;
            z = 0;
        }

        if (rssi < -90) {
            z++;
            x = 0;
            y = 0;
        }

        if (rssi >= -80 && x >= 3) {
            tv_dis.setText(R.string.near);
            tv_rssi.setText(R.string.strong);

        } else if (rssi >= -90 && rssi <= -80 && y >= 3) {
            tv_dis.setText(R.string.middle);
            tv_rssi.setText(R.string.middle);

        } else if (rssi < -90 && z >= 3) {
            tv_dis.setText(R.string.far);
            tv_rssi.setText(R.string.weak);
        }
    }

    @Override
    public void getEle(String ele) {

        Log.d("getEle", ele); // ele stands for electricity
        isSend = true;

        int electricity = Integer.valueOf(ele);
        if (electricity >= 40) {
            iv_electricity.setBackgroundResource(R.drawable.one);
            iv_electricity.setVisibility(View.VISIBLE);

        } else if(electricity <40 && electricity >=30) {
            iv_electricity.setBackgroundResource(R.drawable.two);
            iv_electricity.setVisibility(View.VISIBLE);

        } else if(electricity <30 && electricity >=20){
            iv_electricity.setBackgroundResource(R.drawable.three);
            iv_electricity.setVisibility(View.VISIBLE);

        } else {
            iv_electricity.setBackgroundResource(R.drawable.four);
            iv_electricity.setVisibility(View.VISIBLE);
        }
    }
}
