package com.example.final_project;

import java.util.List;

public class DailyWeather {
  private long dt;
  private static class temp{
    private double min;
    private double max;
  };
  private int pressure;
  private int humidity;
  private double wind_speed;
  private int clouds;
  private List<Weather> weather;

  public List<Weather> getWeather() {
    return weather;
  }

  public void setWeather(List<Weather> weather) {
    this.weather = weather;
  }

  public long getDt() {
    return dt;
  }

  public void setDt(long dt) {
    this.dt = dt;
  }

  public int getPressure() {
    return pressure;
  }

  public void setPressure(int pressure) {
    this.pressure = pressure;
  }

  public int getHumidity() {
    return humidity;
  }

  public void setHumidity(int humidity) {
    this.humidity = humidity;
  }

  public double getWind_speed() {
    return wind_speed;
  }

  public void setWind_speed(double wind_speed) {
    this.wind_speed = wind_speed;
  }

  public int getClouds() {
    return clouds;
  }

  public void setClouds(int clouds) {
    this.clouds = clouds;
  }

}
