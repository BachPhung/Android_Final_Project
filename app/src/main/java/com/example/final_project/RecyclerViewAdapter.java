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

//TODO: Takes the data set which has to be displayed to the user in RecyclerView
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    //vars
    final private ArrayList<String> hours;
    final private ArrayList<String> iconList;
    final private ArrayList<String> degList;
    final private Context context;

    public RecyclerViewAdapter(Context context, ArrayList<String> hourList, ArrayList<String> iconList, ArrayList<String> degList ) {
        this.iconList = iconList;
        this.context = context;
        this.hours = hourList;
        this.degList = degList;
    }

    @NonNull
    @Override
    //TODO: Create a new RecyclerView.ViewHolder and initializes some private fields to be used by RecyclerView
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem,parent,false);
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
        holder.degree.setText(degList.get(position));
        holder.degree.setTextColor(Color.parseColor("#16003B"));
        holder.hour.setTextColor(Color.parseColor("#16003B"));
        holder.hour.setText(hours.get(position));
    }

    @Override
    //TODO: return the total amount of items in the data set held by the adapter
    public int getItemCount() {
        return hours.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView hour;
        TextView degree;
        ImageView weatherIcon;
        //Constructor for create ViewHolder
        public ViewHolder(View itemView){
            super(itemView);
            hour = itemView.findViewById(R.id.hour);
            weatherIcon = itemView.findViewById(R.id.weatherIconHourly);
            degree = itemView.findViewById(R.id.weatherDeg_Recycle);
        }
    }
}
