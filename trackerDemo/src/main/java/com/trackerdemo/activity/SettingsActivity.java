package com.trackerdemo.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import org.w3c.dom.Text;

public class SettingsActivity extends Activity {

    Switch switchSound, switchVibration, switchFlash;
    TextView tv;
    int checkValS, checkValV, checkValF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchSound = (Switch)findViewById(R.id.switch1);
        switchVibration = (Switch)findViewById(R.id.switch2);
        switchFlash = (Switch)findViewById(R.id.switch3);
        tv = (TextView)findViewById(R.id.tv1);

        if(switchSound.isChecked()){
            checkValS = 1;
        }else{
            checkValS = 0;
        }

        if(switchVibration.isChecked()){
            checkValV = 1;
        }else{
            checkValV = 0;
        }

        if(switchFlash.isChecked()){
            checkValF = 1;
        }else{
            checkValF = 0;
        }

        if(checkValS == 1){
            tv.setText("1");
            switchSound.setChecked(true);
        }else{
            tv.setText("0");
            switchSound.setChecked(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("Sound", checkValS);
        editor.putInt("Vibration", checkValV);
        editor.putInt("Flash", checkValF);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        checkValS = prefs.getInt("Sound", 0);
        checkValV = prefs.getInt("Vibration", 0);
        checkValF = prefs.getInt("Flash", 0);
    }
}
