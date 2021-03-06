package com.example.systemsetting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    static String TAG = "MainActivity";
    int time = 0;
    int brightness = 0;

    private final SettingObserver mObserver = new SettingObserver();

    private final class SettingObserver extends ContentObserver {
        public SettingObserver() {
            super(new Handler(Looper.getMainLooper()));
        }
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Log.d(TAG, "uri:" + uri);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
        registerOB();

        Button btn = (Button) this.findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Settings.System.putInt(getApplicationContext().getContentResolver(),"screen_off_timeout", time);
                    time += 1000;
                    Log.d(TAG, "screen_off_timeout: " + Settings.System.getInt(getContentResolver(), "screen_off_timeout"));

                    brightness += 10;
                    if(brightness > 100)
                        brightness = 0;
                    Settings.System.putInt(getApplicationContext().getContentResolver(),"screen_brightness", brightness);
                    Log.d(TAG, "screen_brightness: " + Settings.System.getInt(getContentResolver(), "screen_brightness"));

                }
                catch (Exception e){
                    Log.d(TAG, "onClick: " + e.toString());
                }


            }
        });

    }

    public  void registerOB() {
        getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), false,
                mObserver);
        getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.SCREEN_OFF_TIMEOUT), false,
                mObserver);
    }

    public  void unRegisterOB() {
        getContentResolver().unregisterContentObserver(mObserver);
    }
}