package com.swfinder.entity;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

public class TipHelp {
	private static Vibrator vib;
	public static void Vibrate(final Activity activity, long milliseconds) {
		vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(milliseconds);
	}
	public static void Vibrate(final Activity activity, long[] pattern,boolean isRepeat) {
		vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(pattern, isRepeat ? 1 : -1);
	}
	/**
	 * To start vibration
	 * @param activity
	 * @param pattern
	 * @param isRepeat
	 */
	public static void Vibrate(final Activity activity, long[] pattern,int isRepeat) {

		vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(pattern, isRepeat);
		
	}
	/**
	 * To stop vibration
	 */
	public static void stop() {
		if(vib!=null && vib.hasVibrator()) {
			vib.cancel();
		}
	}
}

