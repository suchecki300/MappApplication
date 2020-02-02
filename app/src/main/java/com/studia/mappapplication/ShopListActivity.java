package com.studia.mappapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.util.Consumer;

import java.util.List;


public class ShopListActivity extends AppCompatActivity {

    private Button addShopButton;
    private Button homepageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_list_activity);

        final RecyclerView shopList = findViewById(R.id.shop_list);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        shopList.setLayoutManager(linearLayoutManager);

        Consumer<List<Shop>> consumer = shops -> {

            handlePreferredColorOptions();
            registerButtonsListeners();

            DividerItemDecoration decoration = new DividerItemDecoration(shopList.getContext(), linearLayoutManager.getOrientation());
            shopList.addItemDecoration(decoration);
            ShopAdapter shopAdapter = new ShopAdapter(shops);
            shopAdapter.notifyDataSetChanged();
            shopList.setAdapter(shopAdapter);
        };

        getShopsList(consumer);
    }

    private void handlePreferredColorOptions() {
        addShopButton = findViewById(R.id.add_shop_button);
        homepageButton = findViewById(R.id.go_to_homepage_button);

        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesService
                .COLOR_PREFERENCES, Context.MODE_PRIVATE);
    }

    private void registerButtonsListeners() {
        registerAddShopButtonListener();
        registerGoHomepageButtonListener();
    }

    private void registerAddShopButtonListener() {
        addShopButton.setOnClickListener(view -> navigateToAddShopActivity());
    }

    private void registerGoHomepageButtonListener() {
        homepageButton.setOnClickListener(view -> navigateToMainActivity());
    }

    private List<Shop> getShopsList(Consumer<List<Shop>> shopsList) {
        FirestoreDatabaseService databaseService = new FirestoreDatabaseService();
        return databaseService.getAllShops(shopsList);
    }

    private void navigateToAddShopActivity() {
        Intent intent = new Intent(this, AddShopActivity.class);
        startActivity(intent);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}