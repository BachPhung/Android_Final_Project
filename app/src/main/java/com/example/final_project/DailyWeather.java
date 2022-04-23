package com.example.final_project;

public class DailyWeather {
  public long dateTime;
  public static class temp{
    public double min;
    public double max;
  };
  public int pressure;
  public int humidity;
  public double wind_speed;
  public int clouds;
  public static class weather{
    public int id;
    public String main;
    public String description;
    public String icon;
  }
}
