package com.example.final_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonParser;

public class LoadingActivity extends AppCompatActivity implements LocationListener {

  // variables
  private RequestQueue queue;
  private double longitude = 0.0;
  private double latitude = 0.0;
  private boolean firstRender = true;
  String weatherResponse="", cityNameResponse="";
  StringRequest weatherRequest, cityNameRequest;
  LocationManager locationManager;

  // Methods
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_loading);
    queue = Volley.newRequestQueue(this);
    startGPS();
    new PrefetchData().execute();
  }
  @Override
  protected void onPause(){
    super.onPause();
    locationManager.removeUpdates(this); // Unregister location listener
  }

  //Todo: Return (String) : URL of weather API
  private String urlWeather() {
    return String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%.4f&lon=%.4f&exclude=minutely&appid=d8288cede8b8b1149b236932c004dc6a", this.latitude, this.longitude);
  }
  //Todo: Return (String) : URL name of city API
  private String urlCityName() {
    return String.format("https://api.openweathermap.org/geo/1.0/reverse?lat=%.4f&lon=%.4f&limit=1&appid=d8288cede8b8b1149b236932c004dc6a", this.latitude, this.longitude) ;
  }

  //Todo: Make REST Request (synchronously)
  private void initialDataLoading(){
    this.weatherRequest = new StringRequest(Request.Method.GET, urlWeather(), response -> {
      try{
        this.weatherResponse = response;
      }
      catch (Exception e){}
    }, error -> {
      Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
    });
    this.cityNameRequest = new StringRequest(Request.Method.GET, urlCityName(),response -> {
      try{
        this.cityNameResponse = response;
      }
      catch (Exception e){}
    }, error -> {
      Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
    });
  }


  public void startGPS() {
    //Todo: Start listening to user's location through Location Manager
    //Todo: - location permission specified in android manifest file
    //Todo: - ask user's permission run-time before accessing GPS
    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    try {
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        // TODO: Check if the user has granted permission for GPS
        // Todo: if not, let's prompt and ask the user to give it
        ActivityCompat.requestPermissions(this,
          new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},0
        );
        return;
      }
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }
    catch (Exception e){
      Log.d("Error", e.toString());
    }
  }
  @Override
  public void onLocationChanged(@NonNull Location location) {
    this.longitude = location.getLongitude();
    this.latitude = location.getLatitude();
    Log.d("Lat Splash: ", String.valueOf(this.latitude));
    Log.d("Long Splash: ", String.valueOf(this.longitude));
    initialDataLoading();
    queue.add(weatherRequest);
    queue.add(cityNameRequest);
  }

  private class PrefetchData extends AsyncTask<Void, Void, Void> {

    @Override
    protected synchronized Void doInBackground(Void... arg0) {
      Log.d("DO IN BACKGROUND", "called");
      // Wait for fetch weather Request complete
      while (weatherResponse.length() == 0){}
      // Wait for fetch city name Request complete
      while (cityNameResponse.length() == 0){}
      return null;
    }
    @Override
    protected synchronized void onPostExecute(Void result){
      Log.d("ON POST EXECUTE", "called");
      super.onPostExecute(result);
      Log.d("WeatherRes", weatherResponse);
      Log.d("CityNameRes", cityNameResponse);
      Intent i = new Intent(LoadingActivity.this, MainActivity.class);
      //Todo: Send data to main activity
      i.putExtra("Weather", weatherResponse);
      i.putExtra("CityName", cityNameResponse);
      i.putExtra("Long",  String.valueOf(longitude));
      i.putExtra("Lat", String.valueOf(latitude));
      startActivity(i);
      //Todo: Finish Loading Activity
      finish();
    }
  }
}