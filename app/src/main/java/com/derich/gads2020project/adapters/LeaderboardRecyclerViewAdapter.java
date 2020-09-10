package com.derich.gads2020project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.derich.gads2020project.R;
import com.derich.gads2020project.model.Leaderboard;

import java.util.ArrayList;

public class LeaderboardRecyclerViewAdapter extends RecyclerView.Adapter<LeaderboardRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "LeaderboardRVAdapter";
    private Context mContext;
    private ArrayList<Leaderboard> mLeaderboards;

    public LeaderboardRecyclerViewAdapter(Context context, ArrayList<Leaderboard> leaderboards){
        this.mContext = context;
        this.mLeaderboards = leaderboards;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.leaderboard_list_item,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return mLeaderboards.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Leaderboard leaderboard = mLeaderboards.get(position);
        holder.mNameTextView.setText(leaderboard.getName());
        holder.mPointsCountryTextView.setText(leaderboard.getPointAndCountryConcat());
        //incase the API changes add a conditional statement and load mImageBadge using the badge images in the drawable
        Glide.with(mContext).load(leaderboard.getBadgeUrl()).centerCrop().placeholder(leaderboard.getBadgePlaceholder()).into(holder.mImageBadge);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageBadge;
        public TextView mNameTextView;
        public TextView mPointsCountryTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageBadge = itemView.findViewById(R.id.image_badge);
            mNameTextView = itemView.findViewById(R.id.tv_name);
            mPointsCountryTextView = itemView.findViewById(R.id.tv_points_country);
        }
    }
}
