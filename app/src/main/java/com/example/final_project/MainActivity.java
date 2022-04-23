package com.example.final_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
  private ArrayList<String> hours = new ArrayList<>();
  private ArrayList<String> iconList = new ArrayList<>();
  private ArrayList<String> degList = new ArrayList<>();
  private RequestQueue queue;
  Gson gson = new Gson();
  CurrentWeather curWeather;
  DailyWeather[] dailyWeathers;
  HourlyWeather[] hourlyWeathers;

  private final String url = "https://api.openweathermap.org/data/2.5/onecall?lat=33.44&lon=-94.04&exclude=minutely&appid=d8288cede8b8b1149b236932c004dc6a";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    queue = Volley.newRequestQueue(this);
    initialData();
  }
  private void initialData(){
    //  1. Make a request to weather map api
    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
      response -> {
        // This is callback for successful response
        // Parse response into JSON Object for processing
        try{
          JSONObject fetchedData = new JSONObject(response);
          curWeather =  gson.fromJson( fetchedData.getJSONObject("current").toString(), CurrentWeather.class);
          dailyWeathers = gson.fromJson(fetchedData.getJSONArray("daily").toString(), DailyWeather[].class);
          hourlyWeathers = gson.fromJson(fetchedData.getJSONArray("hourly").toString(), HourlyWeather[].class);
        }
        catch (JSONException e){

        }
      },
      error -> {
        // This is callback for whenever something went wrong with fetch
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
      }
    );
    //  2. Send the request by adding it to the queue
    queue.add(stringRequest);
    hours.add("Now");
    iconList.add("https://openweathermap.org/img/wn/10d@2x.png");
    degList.add("17");
    initRecyclerView();
  }
  private void initRecyclerView(){
    RecyclerView recyclerView = findViewById(R.id.recyclerView);
    recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, hours, iconList, degList);
    recyclerView.setAdapter(adapter);
  }
}