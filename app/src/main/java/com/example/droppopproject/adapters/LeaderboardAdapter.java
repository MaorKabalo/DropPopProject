package com.example.droppopproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.droppopproject.R;

import java.util.List;

/**
 * Adapter class for displaying leaderboard scores in a RecyclerView.
 * Responsible for inflating leaderboard score item views and binding data to them.
 */
public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardViewHolder> {

    private final List<Integer> scores;

    /**
     * Constructor for the LeaderboardAdapter.
     *
     * @param scores The list of scores to be displayed in the leaderboard.
     */
    public LeaderboardAdapter(List<Integer> scores) {
        this.scores = scores;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_score_item, parent, false);
        return new LeaderboardViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        holder.rankTextView.setText(String.valueOf(position + 1));
        holder.scoreTextView.setText(String.valueOf(scores.get(position)));
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in the data set held by the adapter.
     */
    @Override
    public int getItemCount() {
        return scores.size();
    }
}

