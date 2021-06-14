//===============================================
// REST interaction routines for ITSchool pleasure
//===============================================
package com.example.androtetris;

import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

//http://api.openweathermap.org/data/2.5/weather?q=Stavropol,Russia&appid=b34fcd2b5d42bc626e170a0d80f7a99f
//https://proft.me/2017/05/5/poluchenie-prognoza-pogody-android-retrofit/
public class WeatherRestHelper {
    public static String CITY_NAME = "Stavropol,Russia";
    public static String KEY = "b34fcd2b5d42bc626e170a0d80f7a99f";
    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private static Retrofit retrofit = null;

    public interface ApiInterface {
        @GET("weather")
        Call<WeatherToday> getWeatherToday(
                @Query("q") String city_name,
                @Query("appid") String appid
        );
    }

    public static Retrofit apiClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
