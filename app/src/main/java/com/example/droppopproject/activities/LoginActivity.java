package com.example.droppopproject.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.droppopproject.BallsSharedPreferences;
import com.example.droppopproject.FirebaseControl;
import com.example.droppopproject.R;
import com.example.droppopproject.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

/**
 * Activity responsible for handling user login.
 */
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private FirebaseControl firebaseControl;
    private Intent intent;
    private EditText emailEditText, passwordEditText;
    private ProgressBar progressBar;

    private BallsSharedPreferences mBallsSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase authentication and Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        intent = new Intent(this, HomeActivity.class);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        emailEditText = findViewById(R.id.editTextEmailAddress);
        passwordEditText = findViewById(R.id.editTextPassword);
        firebaseControl = FirebaseControl.getInstance();

        mBallsSharedPreferences = BallsSharedPreferences.getInstance(this);
        //mBallsSharedPreferences.setEnableCustomBalls(false);

        //mBallsSharedPreferences.resetScore();
        //mBallsSharedPreferences.resetSharedPreferences();
        //firebaseAuth.signOut();

        if(checkUserStatus()) {
            finish();
            return;
        }

        mBallsSharedPreferences.resetSharedPreferences(true);
        mBallsSharedPreferences.setIsGuest(false);

        HomeActivity.mScores = null;

        // Set click listeners for sign-up and login buttons
        findViewById(R.id.signupButton).setOnClickListener(view -> showSignUpDialog());
        findViewById(R.id.loginButton).setOnClickListener(view -> loginUser());
        findViewById(R.id.guestButton).setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            mBallsSharedPreferences.setIsGuest(true);
            startActivity(intent);
        });

    }

    /**
     * Displays a dialog for user signup.
     */
    private void showSignUpDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.signup_dialog);
        dialog.findViewById(R.id.confirmUsernameButton).setOnClickListener(v -> {
            EditText usernameEditText = dialog.findViewById(R.id.usernameEditText);
            String username = usernameEditText.getText().toString();
            if (username.length() >= 4)
                signup(username);
            else
                Toast.makeText(this, getString(R.string.DB_ERROR_AT_LEAST_4_CHARACTERS), Toast.LENGTH_SHORT).show();
        });
        dialog.show();
    }


    /**
     * Signs up a new user with the provided username.
     * @param username The username chosen by the user.
     */
    private void signup(String username) {

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(LoginActivity.this, getString(R.string.DB_ERROR_EMAIL_PASSWORD_IS_EMPTY), Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String id = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

                    // Set display name
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build();

                    firebaseAuth.getCurrentUser().updateProfile(profileUpdates)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("TestUsers", "User profile updated.");
                                }
                            });

                    User user = new User(username, id);
                    //firebaseControl.setCurrentUser(user);
                    firebaseControl.saveUser(user);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    if (e.getMessage() != null && e.getMessage().contains("email address is already in use")) {
                        Toast.makeText(LoginActivity.this, getString(R.string.DB_ERROR_EMAIL_ALREADY_TAKEN), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.DB_ERROR_EMAIL_OR_PASSWORD_IS_INVALID), Toast.LENGTH_SHORT).show();
                    }
                });
    }




    /**
     * Logs in an existing user.
     */
    private void loginUser() {


        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(LoginActivity.this, getString(R.string.DB_ERROR_EMAIL_PASSWORD_IS_EMPTY), Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    assert user != null;
                    startActivity(intent);
                    finish();

                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, getString(R.string.DB_ERROR_LOG_IN), Toast.LENGTH_SHORT).show();
                });
    }



    /**
     * Checks the status of the user to determine the appropriate action to take.
     *
     * If the user exited a game midway, the method starts the GameActivity to allow
     * the user to resume the game.
     *
     * If the user is already authenticated (logged in), the method starts the HomeActivity
     * to proceed with the application's main functionality.
     *
     * If neither condition is met, no action is taken, and the method returns false.
     *
     * @return True if the user status check resulted in an action being taken, false otherwise.
     */
    private boolean checkUserStatus() {
        if (mBallsSharedPreferences.isInMidGame()) {  // Check if user exited in the middle of the game
            Intent gameIntent = new Intent(this, GameActivity.class);
            startActivity(gameIntent);
            return true;
        }

        if (firebaseAuth.getCurrentUser() != null) { // Check if user is already connected
            Intent homeIntent = new Intent(this, HomeActivity.class);
            startActivity(homeIntent);
            return true;
        }
        return false;
    }





}
