package com.example.final_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
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
  private ArrayList<String> hours = new ArrayList<>();
  private ArrayList<String> iconList = new ArrayList<>();
  private ArrayList<String> degList = new ArrayList<>();
  //daily vars
  private ArrayList<String> dateList = new ArrayList<>();
  private ArrayList<String> iconDailyList = new ArrayList<>();
  private ArrayList<String> highestList = new ArrayList<>();
  private ArrayList<String> lowestList = new ArrayList<>();
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


  private String urlWeather() {
    return String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%.4f&lon=%.4f&exclude=minutely&appid=d8288cede8b8b1149b236932c004dc6a", this.latitude, this.longitude);
  }
  private String urlCityName() {
    return String.format("https://api.openweathermap.org/geo/1.0/reverse?lat=%.4f&lon=%.4f&limit=1&appid=d8288cede8b8b1149b236932c004dc6a", this.latitude, this.longitude) ;
  }

  @Override
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
      parseWeatherData(intentWeatherResponse);
      parseCityNameData(intentCityName);
    } catch (JSONException e) {
      e.printStackTrace();
    }

  }


  private void initialData() {
    //  1. Make a request to weather map api
    StringRequest stringRequest = new StringRequest(Request.Method.GET, urlWeather(),
      response -> {
        try { parseWeatherData(response);}
        catch (JSONException e) {e.printStackTrace();}
      },
      error -> {
        error.printStackTrace();
      }
    );
    StringRequest getCityNameRequest = new StringRequest(Request.Method.GET, urlCityName(),
      response -> {
        try{
          parseCityNameData(response);
        }
        catch (JSONException e) {e.printStackTrace();}
      },
      error -> {
        error.printStackTrace();
      }
      );

    //  2. Send the request by adding it to the queue
    queue.add(stringRequest);
    queue.add(getCityNameRequest);
  }



  private void initRecyclerView() {
    // Hourly
    RecyclerView recyclerView = findViewById(R.id.recyclerView);
    recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, hours, iconList, degList);
    recyclerView.setAdapter(adapter);
    // Daily
    RecyclerView recyclerViewDaily = findViewById(R.id.recyclerViewDaily);
    recyclerViewDaily.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    RecyclerViewDailyAdapter adapter2 = new RecyclerViewDailyAdapter(this, dateList, iconDailyList, highestList, lowestList);
    recyclerViewDaily.setAdapter(adapter2);
  }
  private void parseWeatherData(String response) throws JSONException {
    JSONObject fetchedWeatherData = new JSONObject(response);
    curWeather = gson.fromJson(fetchedWeatherData.getJSONObject("current").toString(), CurrentWeather.class);
    dailyWeathers = gson.fromJson(fetchedWeatherData.getJSONArray("daily").toString(), DailyWeather[].class);
    hourlyWeathers = gson.fromJson(fetchedWeatherData.getJSONArray("hourly").toString(), HourlyWeather[].class);
    hours.clear();
    iconList.clear();
    degList.clear();
    dateList.clear();
    iconDailyList.clear();
    highestList.clear();
    lowestList.clear();
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
    initRecyclerView();
    TextView curWeatherDesc = findViewById(R.id.weatherDesc);
    TextView curWeatherDeg = findViewById(R.id.weatherDeg);
    TextView curWeatherHi_Li = findViewById(R.id.high_low);

    curWeatherDeg.setText(String.format("%d\u00B0", Math.round(curWeather.getTemp() - 273.15)));
    curWeatherDesc.setText(curWeather.getWeather().get(0).getMain());
    curWeatherHi_Li.setText(String.format("H:%d\u00B0 L:%d\u00B0",Math.round(dailyWeathers[0].getTempMax() - 273.15), Math.round(dailyWeathers[0].getTempMin() - 273.15) ));
  }
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
    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 2000, this);
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
    if(String.valueOf(this.latitude).equals(this.intentLat)  || String.valueOf(this.longitude).equals(this.intentLong) ){
      Log.d ("DONT_FETCH", "called");
      Log.d ("Long: ", intentLong);
      Log.d ("Lat: ", intentLat);
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