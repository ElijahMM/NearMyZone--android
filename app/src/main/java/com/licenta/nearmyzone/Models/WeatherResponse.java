package com.licenta.nearmyzone.Models;

/**
 * Created by Morgenstern on 08/24/2017.
 */

public class WeatherResponse {

    private Integer temp_C =0;
    private String weatherIconUrl = "";
    private String weatherDesc = "";
    private String windspeedKmph = "";
    private String humidity = "";
    private String FeelsLikeC = "";

    public Integer getTemp_C() {
        return temp_C;
    }

    public void setTemp_C(Integer temp_C) {
        this.temp_C = temp_C;
    }

    public String getWeatherIconUrl() {
        return weatherIconUrl;
    }

    public void setWeatherIconUrl(String weatherIconUrl) {
        this.weatherIconUrl = weatherIconUrl;
    }

    public String getWeatherDesc() {
        return weatherDesc;
    }

    public void setWeatherDesc(String weatherDesc) {
        this.weatherDesc = weatherDesc;
    }

    public String getWindspeedKmph() {
        return windspeedKmph;
    }

    public void setWindspeedKmph(String windspeedKmph) {
        this.windspeedKmph = windspeedKmph;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getFeelsLikeC() {
        return FeelsLikeC;
    }

    public void setFeelsLikeC(String feelsLikeC) {
        FeelsLikeC = feelsLikeC;
    }
}
