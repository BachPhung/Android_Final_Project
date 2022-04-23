package com.example.final_project;

public class CurrentWeather {
  public long dateTime;
  public long sunrise;
  public long sunset;
  public double temp;
  public int pressure;
  public int humidity;
  public int clouds;
  public double wind_speed;
  public static class weather{
    public int id;
    public String main;
    public String description;
    public String icon;
  }
}
