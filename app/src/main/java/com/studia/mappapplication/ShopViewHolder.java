package com.studia.mappapplication;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


public class ShopViewHolder extends RecyclerView.ViewHolder{

    TextView name;
    TextView description;
    TextView range;
    TextView latitude;
    TextView longitude;

    ShopViewHolder(View view) {
        super(view);
        name = view.findViewById(R.id.shop_name);
        description = view.findViewById(R.id.shop_description);
        range = view.findViewById(R.id.shop_range);
        latitude = view.findViewById(R.id.shop_latitude);
        longitude = view.findViewById(R.id.shop_longitude);
    }
}