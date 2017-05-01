package com.coolweather.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;

import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.Utility;

public class Setting extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    private WeatherActivity weatherActivity;
    private String mWeatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        SwitchCompat switchCompat = (SwitchCompat) findViewById(R.id.theme_switch);
        switchCompat.setChecked(false);
        switchCompat.setOnCheckedChangeListener(this);

    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if (isChecked) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            mNotification(weather);
        } else {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(1);
        }
    }
    public void mNotification(Weather weather) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(degree)
                .setContentText(weatherInfo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.weather_sunny))
                .setSmallIcon(R.mipmap.small_icon)
                .build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        manager.notify(1,notification);
    }
}
