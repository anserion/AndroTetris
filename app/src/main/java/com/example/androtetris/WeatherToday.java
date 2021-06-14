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
// openweather data model
// intentionally simplified for school use in future
// de-facto was left only current temperature value
//====================================================
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