package com.example.final_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
  }

  private void initialData() {
    //  1. Make a request to weather map api
    StringRequest stringRequest = new StringRequest(Request.Method.GET, urlWeather(),
      response -> {
        // This is callback for successful response
        // Parse response into JSON Object for processing
        try {
          JSONObject fetchedData = new JSONObject(response);
          curWeather = gson.fromJson(fetchedData.getJSONObject("current").toString(), CurrentWeather.class);
          dailyWeathers = gson.fromJson(fetchedData.getJSONArray("daily").toString(), DailyWeather[].class);
          hourlyWeathers = gson.fromJson(fetchedData.getJSONArray("hourly").toString(), HourlyWeather[].class);
          hours.clear();
          iconList.clear();
          degList.clear();
          for (int i = 0; i < 24; i++) {
            if (i == 0) {
              this.hours.add("Now");
            } else {
              Date tempDate = parseUnixToDate(hourlyWeathers[i].getDt());
              String tempHour = getHour(tempDate);
              this.hours.add(tempHour);
            }
            this.iconList.add(String.format("https://openweathermap.org/img/wn/%s@2x.png", (hourlyWeathers[i].getWeather().get(0).getIcon())));
            this.degList.add(String.format("%d\u00B0", Math.round(hourlyWeathers[i].getTemp() - 273.15)));
          }
          dateList.clear();
          iconDailyList.clear();
          highestList.clear();
          lowestList.clear();
          for (int j = 0; j < 7; j++) {
            Date date = parseUnixToDate(dailyWeathers[j + 1].getDt());
            String weekday = getDate(date);
            this.dateList.add(weekday);
            Log.d("Day: ", dateList.get(j));

            this.iconDailyList.add(String.format("https://openweathermap.org/img/wn/%s@2x.png", dailyWeathers[j + 1].getWeather().get(0).getIcon()));
            this.highestList.add(String.valueOf(Math.round(dailyWeathers[j + 1].getTempMax() - 273.15)));
            this.lowestList.add(String.valueOf(Math.round(dailyWeathers[j + 1].getTempMin() - 273.15)));
          }
          initRecyclerView();
          TextView curWeatherDesc = findViewById(R.id.weatherDesc);
          TextView curWeatherDeg = findViewById(R.id.weatherDeg);
          TextView curWeatherHi_Li = findViewById(R.id.high_low);

          curWeatherDeg.setText(String.format("%d\u00B0", Math.round(curWeather.getTemp() - 273.15)));
          curWeatherDesc.setText(curWeather.getWeather().get(0).getMain());
          curWeatherHi_Li.setText(String.format("H:%d\u00B0 L:%d\u00B0",Math.round(dailyWeathers[0].getTempMax() - 273.15), Math.round(dailyWeathers[0].getTempMin() - 273.15) ));
        } catch (JSONException e) {
        }
      },
      error -> {
        // This is callback for whenever something went wrong with fetch
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
      }
    );
    StringRequest getCityNameRequest = new StringRequest(Request.Method.GET, urlCityName(),
      response -> {
        try{
          JSONArray fetchedDataArr = new JSONArray(response);
          JSONObject fetchedData = fetchedDataArr.getJSONObject(0);
          String cityName =  fetchedData.getString("name");
          TextView cityNameTextView = findViewById(R.id.cityName);
          cityNameTextView.setText(cityName);
        }
        catch (Exception e){
          Log.d ("City Name: ", e.toString());
        }
      },
      error -> {

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
    initialData();
  }
}