package com.example.droppopproject.fragments;

import static com.example.droppopproject.activities.HomeActivity.ALL_TIME_INDEX;
import static com.example.droppopproject.activities.HomeActivity.TODAY_INDEX;
import static com.example.droppopproject.activities.HomeActivity.WEEK_INDEX;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.droppopproject.BallsSharedPreferences;
import com.example.droppopproject.activities.HomeActivity;
import com.example.droppopproject.R;
import com.example.droppopproject.adapters.LeaderboardAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

/**
 * A fragment to display the leaderboard in the application.
 * This fragment shows the scores of users in different time intervals (today, this week, all time).
 */
public class LeaderboardFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button btnAllTime;
    private Button btnWeek;
    private Button btnToday;
    private TextView guestTextView;
    private BallsSharedPreferences mSP;

    private ArrayList<Integer> scores;
    private LeaderboardAdapter adapter;

    /**
     * Default constructor for the LeaderboardFragment.
     */
    public LeaderboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scores = new ArrayList<>();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        setViews(view);

        hideViews(mSP.getIsGuest());

        if (!mSP.getIsGuest() && scores.isEmpty()) {
            guestTextView.setText(requireContext().getString(R.string.LEADERBOARD_NO_SCORES));
            guestTextView.setVisibility(View.VISIBLE);
        }

        return view;
    }

    /**
     * Sets up the views and their event listeners.
     *
     * @param view The root view of the fragment
     */
    @SuppressLint("NotifyDataSetChanged")
    private void setViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewLeaderboard);

        adapter = new LeaderboardAdapter(scores);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        scores.addAll(HomeActivity.mScores[ALL_TIME_INDEX]);
        guestTextView = view.findViewById(R.id.IsGuestTextView);
        guestTextView.setVisibility(View.GONE);

        btnAllTime = view.findViewById(R.id.btnAllTime);
        btnWeek = view.findViewById(R.id.btnWeek);
        btnToday = view.findViewById(R.id.btnToday);

        btnAllTime.setOnClickListener(v -> setScores(ALL_TIME_INDEX));
        btnWeek.setOnClickListener(v -> setScores(WEEK_INDEX));
        btnToday.setOnClickListener(v -> setScores(TODAY_INDEX));

        mSP = BallsSharedPreferences.getInstance(getContext());
    }

    /**
     * Sets the scores based on the selected time interval.
     *
     * @param index The index indicating the time interval (ALL_TIME_INDEX, TODAY_INDEX, WEEK_INDEX)
     */
    @SuppressLint("NotifyDataSetChanged")
    private void setScores(int index) {
        scores.clear();
        scores.addAll(HomeActivity.mScores[index]);
        if (!scores.isEmpty()) {
            guestTextView.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * Hides or shows views based on the user's authentication status.
     *
     * @param vis The visibility status. True to hide views, false to show views.
     */
    private void hideViews(boolean vis) {
        int visibility = vis ? View.GONE : View.VISIBLE;
        recyclerView.setVisibility(visibility);
        btnAllTime.setVisibility(visibility);
        btnWeek.setVisibility(visibility);
        btnToday.setVisibility(visibility);

        int guestVis = visibility == View.GONE ? View.VISIBLE : View.GONE;
        guestTextView.setVisibility(guestVis);
    }
}
