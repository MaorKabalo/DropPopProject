package com.example.droppopproject;


// Java program implementing Singleton class
// with using getInstance() method

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Singleton class for handling Firebase operations such as fetching users and saving user data.
 * This class provides methods to interact with Firebase Firestore database and Firebase Authentication.
 */
public class FirebaseControl {

    private static FirebaseControl single_instance = null;

    private final FirebaseFirestore db;

    /**
     * Callback interface for handling user fetch operation.
     */
    public interface UserFetchCallback {
        /**
         * Called when a user is fetched successfully.
         *
         * @param user The fetched user object.
         * @throws ParseException if there is an error parsing user data.
         */
        void onUserFetched(User user) throws ParseException;
    }


    /**
     * Private constructor to prevent instantiation from outside the class.
     */
    private FirebaseControl() {
        db = FirebaseFirestore.getInstance();
    }


    /**
     * Returns the singleton instance of FirebaseControl.
     *
     * @return The singleton instance of FirebaseControl.
     */
    public static FirebaseControl getInstance() {
        if (single_instance == null)
            single_instance = new FirebaseControl();

        return single_instance;
    }


    /**
     * Retrieves the current user's data from Firestore.
     *
     * @param listener Callback to be executed when user data is fetched.
     */
    public void getCurrentUser(final UserFetchCallback listener) {


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            return;
        }

        String userId = user.getUid();

        db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = null;
                if (documentSnapshot.exists()) {
                    currentUser = documentSnapshot.toObject(User.class);
                    assert currentUser != null;
                    currentUser.setDocumentId(userId);
                }

                try {
                    listener.onUserFetched(currentUser);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                try {
                    listener.onUserFetched(null); // Notify listener about failure
                } catch (ParseException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }


    /**
     * Saves user data to Firestore.
     *
     * @param user The user object to be saved.
     */
    public void saveUser(User user) {
        db.collection("users").document(user.getDocumentId()).set(user)
                .addOnSuccessListener(v -> {

                }).addOnFailureListener(e -> {

                });
    }


    /**
     * Retrieves a Firestore collection reference.
     *
     * @param id The ID of the collection.
     * @return A reference to the Firestore collection.
     */
    public CollectionReference getCollection(String id) {
        return db.collection(id);
    }


}


