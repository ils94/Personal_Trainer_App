package com.droidev.personaltrainer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WorkoutSetsAdapter extends RecyclerView.Adapter<WorkoutSetsAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<WorkoutSet> workoutSets;
    private final OnItemClickListener listener;

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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutSet workoutSet = workoutSets.get(position);

        // Formatar "Treino:" com negrito e cor preta
        SpannableString treino = new SpannableString("Treino: " + workoutSet.getType());
        treino.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Negrito
        treino.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Cor preta
        holder.setNameTextView.setText(treino);

        // Formatar "Exercícios:" com negrito e cor preta
        SpannableString exercicios = new SpannableString("Exercícios: " + workoutSet.getExercises());
        exercicios.setSpan(new StyleSpan(Typeface.BOLD), 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Negrito
        exercicios.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Cor preta
        holder.exercisesTextView.setText(exercicios);

        // Formatar "Tempo por exercício:" com negrito e cor preta
        SpannableString tempoPorExercicio = new SpannableString("Tempo por exercício: " + workoutSet.getExerciseTime() + "s");
        tempoPorExercicio.setSpan(new StyleSpan(Typeface.BOLD), 0, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Negrito
        tempoPorExercicio.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Cor preta
        holder.exerciseTimeTextView.setText(tempoPorExercicio);

        // Formatar "Descanso:" com negrito e cor preta
        SpannableString descanso = new SpannableString("Descanso: " + workoutSet.getRestTime() + "s");
        descanso.setSpan(new StyleSpan(Typeface.BOLD), 0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Negrito
        descanso.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Cor preta
        holder.restTimeTextView.setText(descanso);

        // Formatar "Rodadas:" com negrito e cor preta
        SpannableString rodadas = new SpannableString("Rodadas: " + workoutSet.getRounds());
        rodadas.setSpan(new StyleSpan(Typeface.BOLD), 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Negrito
        rodadas.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Cor preta
        holder.roundsTextView.setText(rodadas);

        // Formatar "Intervalo entre rodadas:" com negrito e cor preta
        SpannableString intervaloEntreRodadas = new SpannableString("Intervalo entre rodadas: " + workoutSet.getRoundInterval() + "s");
        intervaloEntreRodadas.setSpan(new StyleSpan(Typeface.BOLD), 0, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Negrito
        intervaloEntreRodadas.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Cor preta
        holder.roundIntervalTextView.setText(intervaloEntreRodadas);

        // Formatar "Ordem aleatória:" com negrito e cor preta
        SpannableString ordemAleatoria = new SpannableString("Ordem aleatória: " + (workoutSet.isRandomOrder() ? "Sim" : "Não"));
        ordemAleatoria.setSpan(new StyleSpan(Typeface.BOLD), 0, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Negrito
        ordemAleatoria.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Cor preta
        holder.randomOrderTextView.setText(ordemAleatoria);

        // Configuração do clique
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

    @SuppressLint("NotifyDataSetChanged")
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
