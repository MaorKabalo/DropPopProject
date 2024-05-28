package com.example.droppopproject.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.droppopproject.R;

/**
 * ViewHolder class for holding views of leaderboard score items in a RecyclerView.
 * This class holds references to the views of each leaderboard score item.
 */
public class LeaderboardViewHolder extends RecyclerView.ViewHolder {

    /** TextView to display the rank of the score. */
    public TextView rankTextView;

    /** TextView to display the score value. */
    public TextView scoreTextView;

    /**
     * Constructor for the LeaderboardViewHolder.
     *
     * @param itemView The View object containing the layout for the leaderboard score item.
     */
    public LeaderboardViewHolder(@NonNull View itemView) {
        super(itemView);
        rankTextView = itemView.findViewById(R.id.rankTextView);
        scoreTextView = itemView.findViewById(R.id.scoreTextView);
    }
}

