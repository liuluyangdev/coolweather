package com.coolweather.android.gson;

/**
 * Created by Cookier on 2017/4/23.
 */

public class AQI {
    public AQICity city;
    public class AQICity {
        public String aqi;
        public String pm25;
    }
}
