package com.droidev.personaltrainer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WorkoutSetsAdapter extends RecyclerView.Adapter<WorkoutSetsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> workoutSets;

    public WorkoutSetsAdapter(Context context, ArrayList<String> workoutSets) {
        this.context = context;
        this.workoutSets = workoutSets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_workout_set, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String workoutSet = workoutSets.get(position);
        holder.setNameTextView.setText(workoutSet);
    }

    @Override
    public int getItemCount() {
        return workoutSets.size();
    }

    public void updateWorkoutSets(ArrayList<String> newWorkoutSets) {
        this.workoutSets.clear();
        this.workoutSets.addAll(newWorkoutSets);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        workoutSets.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView setNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            setNameTextView = itemView.findViewById(R.id.setNameTextView);
        }
    }
}
