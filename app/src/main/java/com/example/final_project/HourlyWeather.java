package com.example.final_project;

import java.util.List;

public class HourlyWeather {
  private long dt;
  private double temp;
  private int pressure;
  private double wind_speed;
  private int wind_deg;
  private List<Weather> weather;

  public long getDt() {
    return dt;
  }

  public void setDt(long dt) {
    this.dt = dt;
  }

  public double getTemp() {
    return temp;
  }

  public void setTemp(double temp) {
    this.temp = temp;
  }



  public int getPressure() {
    return pressure;
  }

  public void setPressure(int pressure) {
    this.pressure = pressure;
  }


  public double getWind_speed() {
    return wind_speed;
  }

  public void setWind_speed(double wind_speed) {
    this.wind_speed = wind_speed;
  }

  public int getWind_deg() {
    return wind_deg;
  }

  public void setWind_deg(int wind_deg) {
    this.wind_deg = wind_deg;
  }

  public List<Weather> getWeather() {
    return weather;
  }

  public void setWeather(List<Weather> weather) {
    this.weather = weather;
  }
}
