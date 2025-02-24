package com.droidev.personaltrainer;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WorkoutSetsAdapter extends RecyclerView.Adapter<WorkoutSetsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> workoutSets;
    private SharedPreferences sharedPreferences;

    public WorkoutSetsAdapter(Context context, ArrayList<String> workoutSets, SharedPreferences sharedPreferences) {
        this.context = context;
        this.workoutSets = workoutSets;
        this.sharedPreferences = sharedPreferences;
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
        holder.setNameTextView.setText(workoutSet); // Exibe o nome do conjunto
    }

    @Override
    public int getItemCount() {
        return workoutSets.size();
    }

    // Método para atualizar a lista de conjuntos no Adapter
    public void updateWorkoutSets(ArrayList<String> newWorkoutSets) {
        this.workoutSets.clear(); // Limpa a lista atual
        this.workoutSets.addAll(newWorkoutSets); // Adiciona os novos dados
        notifyDataSetChanged(); // Notifica o RecyclerView sobre as mudanças
    }

    // Método para remover um item da lista
    public void removeItem(int position) {
        workoutSets.remove(position); // Remove o item da lista
        notifyItemRemoved(position); // Notifica o Adapter sobre a remoção
    }

    // ViewHolder para o RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView setNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            setNameTextView = itemView.findViewById(R.id.setNameTextView); // Referência ao TextView no layout do item
        }
    }
}