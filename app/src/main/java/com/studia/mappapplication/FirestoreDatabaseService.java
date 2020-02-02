package com.studia.mappapplication;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.util.Consumer;

import java.util.ArrayList;
import java.util.List;


public class FirestoreDatabaseService {

    private static final String DB_SERVICE_TAG = "DB_SERVICE";
    private static final String USERS_COLLECTION = "users";
    private static final String SHOPS_COLLECTION = "shops";
    private static final String USER_SPECIFIC_SHOPS_COLLECTION = "user_specific_shops";
    private List<Shop> myAllShops = new ArrayList<>();

    private String loggedInUserUid;
    private FirebaseFirestore firestoreDb;

    public FirestoreDatabaseService() {
        loggedInUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestoreDb = FirebaseFirestore.getInstance();
        initShops();
    }

    void insertUser(User user) {
        firestoreDb.collection(USERS_COLLECTION)
                .document(loggedInUserUid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(DB_SERVICE_TAG, "User added to database!");
                    }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(DB_SERVICE_TAG, "Error when adding user to database", e);
                    }
                });
    }


    public void insertShop(Shop shop) {
        firestoreDb.collection(SHOPS_COLLECTION)
                .document(loggedInUserUid)
                .collection(USER_SPECIFIC_SHOPS_COLLECTION)
                .document(shop.getId())
                .set(shop)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(DB_SERVICE_TAG, "Shop added to database!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(DB_SERVICE_TAG, "Error when adding shop to database.", e);
                    }
                });
    }

    public void updateShop(Shop shop) {
        firestoreDb.collection(SHOPS_COLLECTION)
                .document(loggedInUserUid)
                .collection(USER_SPECIFIC_SHOPS_COLLECTION)
                .document(shop.getId())
                .set(shop)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(DB_SERVICE_TAG, "Shop updated in database!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(DB_SERVICE_TAG, "Error when updating shop in database.", e);
                    }
                });
    }

    public void deleteShop(String shopId) {
        firestoreDb.collection(SHOPS_COLLECTION)
                .document(loggedInUserUid)
                .collection(USER_SPECIFIC_SHOPS_COLLECTION)
                .document(shopId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(DB_SERVICE_TAG, "Shop removed from database!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(DB_SERVICE_TAG, "Error when removing shop from database.", e);
                    }
                });
    }

    public List<Shop> getAllShops() {
        return myAllShops;
    }

    private void initShops() {
        myAllShops.add(new Shop("asdsad", "asdsad", "desc1", 100000.0, 37.4219983, 50.575));
        myAllShops.add(new Shop("Empik", "Empik", "desc2", 100000.0, 46.6463027, 19.7050208));
        myAllShops.add(new Shop("Empik", "saddsa", "desc2", 100000.0, 37.4219983, 50.575));
        myAllShops.add(new Shop("Polska", "Polska", "Pooooo", 100000.0, 52.1626417, 21.2190483));
    }


    public List<Shop> getAllShops(final Consumer<List<Shop>> shopsList) {
        final List<Shop> allShops = new ArrayList<>();
        firestoreDb.collection(SHOPS_COLLECTION)
                .document(loggedInUserUid)
                .collection(USER_SPECIFIC_SHOPS_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                allShops.add(document.toObject(Shop.class));
                            }
                            shopsList.accept(allShops);
                        } else {
                            Log.w(DB_SERVICE_TAG, "Error when getting all shops.", task.getException());
                        }
                    }
                });

        return allShops;
    }


}