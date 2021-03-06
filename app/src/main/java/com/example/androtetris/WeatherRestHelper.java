//Copyright 2021 Andrey S. Ionisyan (anserion@gmail.com, asion@mail.ru)
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.

//====================================================
// Simple tetris game with SQLite and openweather REST
//====================================================
// REST interaction routines for ITSchool pleasure
// retrofit API interafce and factory with GSon transform inside
// query for Stavropol city was compiled.
// http://api.openweathermap.org/data/2.5/weather?q=Stavropol,Russia&appid=KEY
// Select another cities interaction was left to shine future
//====================================================
package com.example.androtetris;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

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
