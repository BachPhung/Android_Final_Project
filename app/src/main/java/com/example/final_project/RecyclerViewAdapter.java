package com.example.final_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    //vars
    private ArrayList<String> hours = new ArrayList<>();
    private ArrayList<String> iconList = new ArrayList<>();
    private ArrayList<String> degList = new ArrayList<>();
    private Context context;

    public RecyclerViewAdapter(Context context, ArrayList<String> hourList, ArrayList<String> iconList, ArrayList<String> degList ) {
        this.iconList = iconList;
        this.context = context;
        this.hours = hourList;
        this.degList = degList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context)
                .asBitmap()
                .load(iconList.get(position))
                .into(holder.weatherIcon);
        holder.degree.setText(degList.get(position));
        holder.hour.setText(hours.get(position));
    }

    @Override
    public int getItemCount() {
        return hours.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView hour;
        TextView degree;
        ImageView weatherIcon;
        public ViewHolder(View itemView){
            super(itemView);
            hour = itemView.findViewById(R.id.hour);
            weatherIcon = itemView.findViewById(R.id.weatherIconHourly);
            degree = itemView.findViewById(R.id.weatherDeg_Recycle);
        }
    }
}
