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
    private ArrayList<WorkoutSet> workoutSets;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public WorkoutSetsAdapter(Context context, ArrayList<WorkoutSet> workoutSets, OnItemClickListener listener) {
        this.context = context;
        this.workoutSets = workoutSets;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_workout_set, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutSet workoutSet = workoutSets.get(position);

        holder.setNameTextView.setText("Treino " + (position + 1));
        holder.exercisesTextView.setText("Exercícios: " + workoutSet.getExercises());
        holder.exerciseTimeTextView.setText("Tempo por exercício: " + workoutSet.getExerciseTime() + "s");
        holder.restTimeTextView.setText("Descanso: " + workoutSet.getRestTime() + "s");
        holder.roundsTextView.setText("Rodadas: " + workoutSet.getRounds());
        holder.roundIntervalTextView.setText("Intervalo entre rodadas: " + workoutSet.getRoundInterval() + "s");
        holder.randomOrderTextView.setText("Ordem aleatória: " + (workoutSet.isRandomOrder() ? "Sim" : "Não"));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return workoutSets.size();
    }

    public void updateWorkoutSets(ArrayList<WorkoutSet> newWorkoutSets) {
        this.workoutSets.clear();
        this.workoutSets.addAll(newWorkoutSets);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        workoutSets.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView setNameTextView, exercisesTextView, exerciseTimeTextView, restTimeTextView,
                roundsTextView, roundIntervalTextView, randomOrderTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            setNameTextView = itemView.findViewById(R.id.setNameTextView);
            exercisesTextView = itemView.findViewById(R.id.exercisesTextView);
            exerciseTimeTextView = itemView.findViewById(R.id.exerciseTimeTextView);
            restTimeTextView = itemView.findViewById(R.id.restTimeTextView);
            roundsTextView = itemView.findViewById(R.id.roundsTextView);
            roundIntervalTextView = itemView.findViewById(R.id.roundIntervalTextView);
            randomOrderTextView = itemView.findViewById(R.id.randomOrderTextView);
        }
    }
}
