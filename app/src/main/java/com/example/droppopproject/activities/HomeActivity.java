package com.example.droppopproject.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.droppopproject.BallsSharedPreferences;
import com.example.droppopproject.FirebaseControl;
import com.example.droppopproject.R;
import com.example.droppopproject.fragments.TabItemFragmentAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

/**
 * The main activity of the application, responsible for displaying tabs for different functionalities
 * and managing user authentication.
 */
public class HomeActivity extends AppCompatActivity {

    private String[] tabTexts;
    private int[] tabIcons;

    /**
     * An array to store scores for today, all time, and this week.
     * Index 0 for Today, 1 for All time and 2 for week.
     * TreeSet is used for sorting from high to low.
     */
    public static TreeSet<Integer>[] mScores;

    public final static int TODAY_INDEX = 0;
    public final static int ALL_TIME_INDEX = 1;
    public final static int WEEK_INDEX = 2;

    private BallsSharedPreferences mSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize mScores array if null
        if (mScores == null) {
            mScores = new TreeSet[3];
            for (int i = 0; i < mScores.length; i++) {
                mScores[i] = new TreeSet<>(Comparator.reverseOrder());
            }
        }

        mSP = BallsSharedPreferences.getInstance(this);
        // Fetch scores only if the user is not a guest
        if (!mSP.getIsGuest()) {
            try {
                fetchScores();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        tabTexts = new String[]{getString(R.string.TAB_SETTINGS), getString(R.string.TAB_PLAY), getString(R.string.TAB_LEADERBOARD)};
        tabIcons = new int[]{R.drawable.baseline_settings_24, R.drawable.baseline_play_circle_24, R.drawable.crown_logo};

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        TabItemFragmentAdapter adapter = new TabItemFragmentAdapter(this);

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(tabTexts[position]);
            tab.setIcon(tabIcons[position]);
        }).attach();

       //setLogoutButton();
    }

    /**
     * Fetches the user's scores from Firebase and updates mScores array accordingly.
     *
     * @throws ParseException if there is an error parsing the date string
     */
    public void fetchScores() throws ParseException {
        FirebaseControl firebaseControl = FirebaseControl.getInstance();

        firebaseControl.getCurrentUser(currentUser -> {
            if (currentUser != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'XXX yyyy", Locale.ENGLISH);
                Calendar todayCalendar = Calendar.getInstance();
                Calendar scoreCalendar = Calendar.getInstance();

                HashMap<String, Integer> scores = currentUser.getScores();

                for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                    Date dateOfScore = dateFormat.parse(entry.getKey());
                    assert dateOfScore != null;
                    scoreCalendar.setTime(dateOfScore);

                    Integer score = entry.getValue();

                    boolean areInSameDay = todayCalendar.get(Calendar.YEAR) == scoreCalendar.get(Calendar.YEAR) &&
                            todayCalendar.get(Calendar.MONTH) == scoreCalendar.get(Calendar.MONTH) &&
                            todayCalendar.get(Calendar.DAY_OF_MONTH) == scoreCalendar.get(Calendar.DAY_OF_MONTH);

                    boolean areInSameWeek = todayCalendar.get(Calendar.WEEK_OF_YEAR) == scoreCalendar.get(Calendar.WEEK_OF_YEAR) &&
                            todayCalendar.get(Calendar.YEAR) == scoreCalendar.get(Calendar.YEAR);

                    if (areInSameDay) {
                        mScores[TODAY_INDEX].add(score);
                    }
                    if (areInSameWeek) {
                        mScores[WEEK_INDEX].add(score);
                    }

                    mScores[ALL_TIME_INDEX].add(score);
                }
                Log.d("NNN", Arrays.toString(mScores));
            } else {
                mScores = null;
            }
        });
    }





}
