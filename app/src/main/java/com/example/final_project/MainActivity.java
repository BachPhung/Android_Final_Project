package com.example.final_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {
  //hourly vars
  final private ArrayList<String> hours = new ArrayList<>();
  final private ArrayList<String> iconList = new ArrayList<>();
  final private ArrayList<String> degList = new ArrayList<>();
  //daily vars
  final private ArrayList<String> dateList = new ArrayList<>();
  final private ArrayList<String> iconDailyList = new ArrayList<>();
  final private ArrayList<String> highestList = new ArrayList<>();
  final private ArrayList<String> lowestList = new ArrayList<>();
  private String intentWeatherResponse;
  private String intentCityName;
  private String intentLong;
  private String intentLat;
  private RequestQueue queue;
  private double longitude = 0.0;
  private double latitude = 0.0;
  Gson gson = new Gson();
  CurrentWeather curWeather;
  DailyWeather[] dailyWeathers;
  HourlyWeather[] hourlyWeathers;
  LocationManager locationManager;


  @SuppressLint("DefaultLocale")
  //TODO: RETURN THE URL FROM https://openweathermap.org/ TO FETCH WEATHER DATA RELY ON LAT & LONG
  private String urlWeather() {
    return String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%.4f&lon=%.4f&exclude=minutely&appid=d8288cede8b8b1149b236932c004dc6a", this.latitude, this.longitude);
  }
  @SuppressLint("DefaultLocale")
  //TODO: RETURN THE URL FROM https://openweathermap.org/ TO GET CITY NAME RELY ON LAT & LONG
  private String urlCityName() {
    return String.format("https://api.openweathermap.org/geo/1.0/reverse?lat=%.4f&lon=%.4f&limit=1&appid=d8288cede8b8b1149b236932c004dc6a", this.latitude, this.longitude) ;
  }

  @Override
  //When application is first open
  //Todo 1: Turn on GPS to get long & lat values
  //Todo 2: Get value through Intent from Splash Screen (avoid white screen while waiting fetching)
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    queue = Volley.newRequestQueue(this);
    startGPS();
    intentWeatherResponse = getIntent().getStringExtra("Weather");
    intentCityName = getIntent().getStringExtra("CityName");
    intentLong = getIntent().getStringExtra("Long");
    intentLat = getIntent().getStringExtra("Lat");

    try {
      //TODO 3: Process the data and render UI
      parseWeatherData(intentWeatherResponse);
      parseCityNameData(intentCityName);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void onPause(){
    super.onPause();
    //TODO: Stop GPS Device
    locationManager.removeUpdates(this);
  }

  @Override
  protected void onResume(){
    super.onResume();
    //TODO: Re-start GPS Device again
    startGPS();
  }

  //TODO: Make GET requests to fetch data from openweatherapi URL
  private void initialData() {
    //  1. Make a request to weather map api
    StringRequest stringRequest = new StringRequest(Request.Method.GET, urlWeather(),
      response -> {
        try { parseWeatherData(response);}
        catch (JSONException e) {e.printStackTrace();}
      },
      Throwable::printStackTrace
    );
    StringRequest getCityNameRequest = new StringRequest(Request.Method.GET, urlCityName(),
      response -> {
        try{
          parseCityNameData(response);
        }
        catch (JSONException e) {e.printStackTrace();}
      },
      Throwable::printStackTrace
      );

    //  2. Send the request by adding it to the queue
    queue.add(stringRequest);
    queue.add(getCityNameRequest);
  }



  private void initRecyclerView() {
    // Hourly
    //TODO: Set up recyclerView for hourly weather forecast
    RecyclerView recyclerView = findViewById(R.id.recyclerView);
    //TODO: Set layout scroll as horizontal direction and how the items are displayed (reverse or not)
    recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    // TODO: Create an adapter provide a binding from an app-specific data set to views what are display in RecyclerView
    RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, hours, iconList, degList);
    recyclerView.setAdapter(adapter);
    // Daily
    //TODO: Set up an another recyclerView for daily weather forecast
    RecyclerView recyclerViewDaily = findViewById(R.id.recyclerViewDaily);
    recyclerViewDaily.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    RecyclerViewDailyAdapter adapter2 = new RecyclerViewDailyAdapter(this, dateList, iconDailyList, highestList, lowestList);
    recyclerViewDaily.setAdapter(adapter2);
  }
  @SuppressLint("DefaultLocale")
  //TODO: Change JSON -> GSON for easier processing the data
  private void parseWeatherData(String response) throws JSONException {
    JSONObject fetchedWeatherData = new JSONObject(response);
    curWeather = gson.fromJson(fetchedWeatherData.getJSONObject("current").toString(), CurrentWeather.class);
    dailyWeathers = gson.fromJson(fetchedWeatherData.getJSONArray("daily").toString(), DailyWeather[].class);
    hourlyWeathers = gson.fromJson(fetchedWeatherData.getJSONArray("hourly").toString(), HourlyWeather[].class);
    //TODO: Clean the array lists for next render
    hours.clear();
    iconList.clear();
    degList.clear();
    dateList.clear();
    iconDailyList.clear();
    highestList.clear();
    lowestList.clear();

    // TODO: Processing the data and push data to the array lists for RecyclerView rendering
    for (int i = 0; i < 24; i++) {
      if (i == 0) {
        this.hours.add("Now");
      }
      else {
          this.hours.add(getHour(parseUnixToDate(hourlyWeathers[i].getDt())));
      }
      if(i <7){
        Date date = parseUnixToDate(dailyWeathers[i + 1].getDt());
        String weekday = getDate(date);
        this.dateList.add(weekday);
        Log.d("Day: ", dateList.get(i));

        this.iconDailyList.add(String.format("https://openweathermap.org/img/wn/%s@2x.png", dailyWeathers[i + 1].getWeather().get(0).getIcon()));
        this.highestList.add(String.valueOf(Math.round(dailyWeathers[i + 1].getTempMax() - 273.15)));
        this.lowestList.add(String.valueOf(Math.round(dailyWeathers[i + 1].getTempMin() - 273.15)));
      }
      this.iconList.add(String.format("https://openweathermap.org/img/wn/%s@2x.png", (hourlyWeathers[i].getWeather().get(0).getIcon())));
      this.degList.add(String.format("%d\u00B0", Math.round(hourlyWeathers[i].getTemp() - 273.15)));
    }

    initRecyclerView(); // TODO: RecyclerView start render UI when data is ready
    TextView curWeatherDesc = findViewById(R.id.weatherDesc);
    TextView curWeatherDeg = findViewById(R.id.weatherDeg);
    TextView curWeatherHi_Li = findViewById(R.id.high_low);
    ConstraintLayout layouts = findViewById(R.id.background);

    //TODO: Set the background image base on weather
    switch (curWeather.getWeather().get(0).getMain()){
      case "Clouds":{
        layouts.setBackground(ContextCompat.getDrawable(this, R.drawable.clouds));
        break;
      }
      case "Clear":{
        layouts.setBackground(ContextCompat.getDrawable(this, R.drawable.clear));
        break;
      }
      case "Drizzle":{
        layouts.setBackground(ContextCompat.getDrawable(this, R.drawable.drizzle));
        break;
      }
      case "Rain":{
        layouts.setBackground(ContextCompat.getDrawable(this, R.drawable.rain));

        break;
      }
      case "Snow":{
        layouts.setBackground(ContextCompat.getDrawable(this, R.drawable.snow));
        break;
      }
      case "Thunderstorm":{
        layouts.setBackground(ContextCompat.getDrawable(this, R.drawable.thunderstorm));
        break;
      }
      default:{
        layouts.setBackground(ContextCompat.getDrawable(this, R.drawable.defaultweather));
      }
    }

    curWeatherDeg.setText(String.format("%d\u00B0", Math.round(curWeather.getTemp() - 273.15)));
    curWeatherDesc.setText(curWeather.getWeather().get(0).getMain());
    curWeatherHi_Li.setText(String.format("H:%d\u00B0 L:%d\u00B0",Math.round(dailyWeathers[0].getTempMax() - 273.15), Math.round(dailyWeathers[0].getTempMin() - 273.15) ));
  }

  //TODO: Parse function to get name of a city from the response
  private void parseCityNameData (String response) throws JSONException{
    JSONArray fetchedDataArr = new JSONArray(response);
    JSONObject fetchedData = fetchedDataArr.getJSONObject(0);
    String cityName =  fetchedData.getString("name");
    TextView cityNameTextView = findViewById(R.id.cityName);
    cityNameTextView.setText(cityName);
  }
  private Date parseUnixToDate(long dateUnix) {
    return Date.from(Instant.ofEpochSecond(dateUnix));
  }

  private String getDate(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
  }

  private String getHour(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar.get(Calendar.HOUR_OF_DAY) + "";
  }

  public void startGPS() {
    //Todo: Start listening to user's location through Location Manager
    //Todo: - location permission specified in android manifest file
    //Todo: - ask user's permission run-time before accessing GPS
    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    Log.d("START_GPS", "Start GPS called");
    try {
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        // TODO: Check if the user has granted permission for GPS
        // Todo: if not, let's prompt and ask the user to give it
        ActivityCompat.requestPermissions(this,
          new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
          0
          );
        return;
      }
      // Only update the new location when moving 10 kilometers (10.000 meters) away
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10000, this);
    }
    catch (Exception e){
      Log.d("Error", e.toString());
    }
  }

  @Override
  public void onLocationChanged(@NonNull Location location) {
    // We now can read the latitude and longitude from "location" parameter
    this.longitude = location.getLongitude();
    this.latitude = location.getLatitude();
    Log.d("Long: ", String.valueOf(this.longitude) );
    Log.d("Lat: ", String.valueOf(this.latitude));
    /*
    * Todo: Because we have already fetched data on Splash Screen, to the if statement below to
    *  avoid unnecessary fetching data for the first time.
    *  The else statement to fetch and update data for the next time*/
    if(String.valueOf(this.latitude).equals(this.intentLat)  || String.valueOf(this.longitude).equals(this.intentLong) ){
      Log.d ("DON'T_FETCH", "called");
      Log.d ("Long: ", intentLong);
      Log.d ("Lat: ", intentLat);
      // For the
      try {
        parseWeatherData(intentWeatherResponse);
        parseCityNameData(intentCityName);
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    else{
      Log.d ("RE_FETCH", "called");
      Log.d ("INTEBT_Long: ", intentLong);
      Log.d ("INTENT_Lat: ", intentLat);
      initialData();
    }
  }
}