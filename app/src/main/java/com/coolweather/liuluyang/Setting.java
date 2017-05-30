package com.coolweather.liuluyang;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.coolweather.liuluyang.gson.Weather;
import com.coolweather.liuluyang.util.Utility;

public class Setting extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener{

    private WeatherActivity weatherActivity;
    private String mWeatherId;
    private ImageView more;
    private SwitchCompat switchCompat;
    private SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        SharedPreferences isCheck = getSharedPreferences("isCheck", MODE_PRIVATE);
        boolean index = isCheck.getBoolean("index", false);
        edit = isCheck.edit();

        switchCompat = (SwitchCompat) findViewById(R.id.theme_switch);
        more = (ImageView) findViewById(R.id.more);

       switchCompat.setChecked(index);
        switchCompat.setOnCheckedChangeListener(this);
        more.setOnClickListener(this);

    }

    /**
     * SwitchCompat这个开关控制通知栏的出现与关闭
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);

        if (isChecked) {
            edit.putBoolean("index",true);
            edit.commit();
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            mNotification(weather);
        } else {
            edit.putBoolean("index",false);
            edit.commit();
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(1);
        }

    }

    /**
     * 创建通知
     * @param weather
     */
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.more:
                morePopUp(view);
                break;
            default:
                break;
        }
    }

    /**
     * 更换通知栏背景时的弹出的PopUpWindow
     * @param view
     */
    private void morePopUp(View view) {
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.setting_more,null);
        view1.setFocusable(false);//Setting this to false will also ensure that this view is not focusable in touch mode.
        view1.setFocusableInTouchMode(true);//true, this view can receive the focus while in touch mode.
        final PopupWindow popupWindow = new PopupWindow(view1, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);

        final TextView white = (TextView) view1.findViewById(R.id.white);
        TextView blue = (TextView) view1.findViewById(R.id.blue);

        white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    onCheckedChanged((CompoundButton) v ,true);
            }
        });

        blue.setOnClickListener(new View.OnClickListener() {
            SharedPreferences prefs0 = PreferenceManager.getDefaultSharedPreferences(Setting.this);
            String weatherString = prefs0.getString("weather",null);
            @Override
            public void onClick(View v) {

            //先取消原来的通知栏
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.cancel(1);

                Weather weather = Utility.handleWeatherResponse(weatherString);
                mWeatherId = weather.basic.weatherId;
                mNotification2(weather);
            }
        });
        //popupWindow.showAsDropDown(view);
        popupWindow.showAtLocation(view, Gravity.LEFT,1100,-800);
    }
    //蓝色背景通知栏
    public void mNotification2(Weather weather) {
        //ImageView mImageView = (ImageView) findViewById(R.id.my_notification_weather);
        TextView mTextView = (TextView) findViewById(R.id.my_notification_info);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        mTextView.setText(weatherInfo);

        RemoteViews mRemoteViews = new RemoteViews(this.getPackageName(),R.layout.blue);


        Notification notification = new NotificationCompat.Builder(this)


                .setSmallIcon(R.mipmap.small_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.weather_sunny))
                .setCustomContentView(mRemoteViews)
                .build();
        //notification.flags = Notification.FLAG_ONGOING_EVENT;
            manager.notify(1,notification);
    }
}
