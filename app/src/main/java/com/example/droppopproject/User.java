package com.example.droppopproject;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Exclude;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.Timestamp;

import org.checkerframework.checker.units.qual.Time;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String username;
    private HashMap<String, Integer> scores;
    private String documentId;

    public User() {}

    public User(String username, String documentId) {
        this.username = username;
        this.documentId = documentId;
        this.scores = new HashMap<>();
    }

    public void addNewScore(int score){
        String time = Calendar.getInstance().getTime().toString();
        Log.d("banana", time);
        scores.put(time, score); // TODO: here
    }

    public User(User other) {
        this.username = other.username;
        this.documentId = other.documentId;
        this.scores = new HashMap<>(other.scores);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public HashMap<String, Integer> getScores(){
        return scores;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", documentId='" +documentId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username) && documentId.equals(user.documentId);
    }


    public static User fromFirebaseUser(FirebaseUser firebaseUser) {
        if (firebaseUser == null) {
            return null;
        }
        return new User(firebaseUser.getDisplayName(), firebaseUser.getUid());
    }


}

