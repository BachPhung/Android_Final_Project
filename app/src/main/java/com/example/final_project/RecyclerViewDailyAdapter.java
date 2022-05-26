package com.example.final_project;

import android.content.Context;
import android.graphics.Color;
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
  final private ArrayList<String> dateList;
  final private ArrayList<String> iconList;
  final private ArrayList<String> highestDegList;
  final private ArrayList<String> lowestDegList;
  final private Context context;

  //TODO: Takes the data set which has to be displayed to the user in RecyclerView
  public RecyclerViewDailyAdapter(Context context, ArrayList<String> dateList, ArrayList<String> iconList, ArrayList<String> highestDegList, ArrayList<String> lowestDegList) {
    this.dateList = dateList;
    this.context = context;
    this.iconList = iconList;
    this.highestDegList = highestDegList;
    this.lowestDegList = lowestDegList;
  }

  @NonNull
  @Override
  //TODO: Create a new RecyclerView.ViewHolder and initializes some private fields to be used by RecyclerView
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listiem_daily,parent,false);
    return new ViewHolder(view);
  }

  @Override
  //TODO: Update the RecyclerView.ViewHolder contents with the item at the given position and also sets up
  // some private fields to be used by RecyclerView
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Glide.with(context)
      .asBitmap()
      .load(iconList.get(position))
      .into(holder.weatherIcon);
    holder.date.setText(dateList.get(position));
    holder.date.setTextColor(Color.parseColor("#16003B"));
    holder.hi_deg.setTextColor(Color.parseColor("#16003B"));
    holder.low_deg.setTextColor(Color.parseColor("#16003B"));
    holder.hi_deg.setText(highestDegList.get(position));
    holder.low_deg.setText(lowestDegList.get(position));
  }

  @Override
  //TODO: return the total amount of items in the data set held by the adapter
  public int getItemCount() {
    return dateList.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder{
    TextView date;
    TextView hi_deg;
    TextView low_deg;
    ImageView weatherIcon;
    //Constructor for create ViewHolder
    public ViewHolder(View itemView){
      super(itemView);
      date = itemView.findViewById(R.id.dateWeather);
      weatherIcon = itemView.findViewById(R.id.weatherIconDaily);
      hi_deg = itemView.findViewById(R.id.high_daily_deg);
      low_deg = itemView.findViewById(R.id.low_daily_deg);
    }
  }
}
