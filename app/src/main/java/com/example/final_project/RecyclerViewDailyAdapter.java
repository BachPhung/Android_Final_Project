package com.example.final_project;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclerViewDailyAdapter extends RecyclerView.Adapter<RecyclerViewDailyAdapter.ViewHolder> {
  //vars
  private ArrayList<String> dateList = new ArrayList<>();
  private ArrayList<String> iconList = new ArrayList<>();
  private ArrayList<String> highestDegList = new ArrayList<>();
  private ArrayList<String> lowestDegList = new ArrayList<>();
  private Context context;

  public RecyclerViewDailyAdapter(Context context, ArrayList<String> dateList, ArrayList<String> iconList, ArrayList<String> highestDegList, ArrayList<String> lowestDegList) {
    this.dateList = dateList;
    this.context = context;
    this.iconList = iconList;
    this.highestDegList = highestDegList;
    this.lowestDegList = lowestDegList;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listiem_daily,parent,false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Glide.with(context)
      .asBitmap()
      .load(iconList.get(position))
      .into(holder.weatherIcon);
    holder.date.setText(dateList.get(position));
    holder.hi_deg.setText(highestDegList.get(position));
    holder.low_deg.setText(lowestDegList.get(position));
  }

  @Override
  public int getItemCount() {
    Log.d("Size", String.valueOf(dateList.size()));
    return dateList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder{
    TextView date;
    TextView hi_deg;
    TextView low_deg;
    ImageView weatherIcon;
    public ViewHolder(View itemView){
      super(itemView);
      date = itemView.findViewById(R.id.dateWeather);
      weatherIcon = itemView.findViewById(R.id.weatherIconDaily);
      hi_deg = itemView.findViewById(R.id.high_daily_deg);
      low_deg = itemView.findViewById(R.id.low_daily_deg);
    }
  }
}
