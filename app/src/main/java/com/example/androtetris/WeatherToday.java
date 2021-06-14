package com.example.androtetris;

import com.google.gson.annotations.SerializedName;

public class WeatherToday {
    public class WeatherTemp {
        Double temp;
        Double temp_min;
        Double temp_max;
    }

    @SerializedName("main")
    private WeatherTemp temp;

    public WeatherToday(WeatherTemp temp) { this.temp = temp; }
    public String getTempWithDegree() { return String.valueOf(temp.temp.intValue()-273) + "\u00B0"; }
}