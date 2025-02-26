package com.droidev.personaltrainer;

import java.util.Objects;

public class WorkoutSet {
    private String type; // Novo campo: tipo de treino
    private String exercises;
    private int exerciseTime;
    private int restTime;
    private int roundInterval;
    private int rounds;
    private boolean randomOrder;

    // Getters e Setters
    public String getType() {
        return type;
    }

    public void setType(String type) { // Corrigido o setter para void
        this.type = type;
    }

    public String getExercises() {
        return exercises;
    }

    public void setExercises(String exercises) {
        this.exercises = exercises;
    }

    public int getExerciseTime() {
        return exerciseTime;
    }

    public void setExerciseTime(int exerciseTime) {
        this.exerciseTime = exerciseTime;
    }

    public int getRestTime() {
        return restTime;
    }

    public void setRestTime(int restTime) {
        this.restTime = restTime;
    }

    public int getRoundInterval() {
        return roundInterval;
    }

    public void setRoundInterval(int roundInterval) {
        this.roundInterval = roundInterval;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public boolean isRandomOrder() {
        return randomOrder;
    }

    public void setRandomOrder(boolean randomOrder) {
        this.randomOrder = randomOrder;
    }

    // Implementação do equals e hashCode atualizados para incluir o campo "type"
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkoutSet that = (WorkoutSet) o;

        if (exerciseTime != that.exerciseTime) return false;
        if (restTime != that.restTime) return false;
        if (roundInterval != that.roundInterval) return false;
        if (rounds != that.rounds) return false;
        if (randomOrder != that.randomOrder) return false;
        if (!Objects.equals(type, that.type)) return false; // Incluído o campo "type"
        return Objects.equals(exercises, that.exercises);
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0; // Incluído o campo "type"
        result = 31 * result + (exercises != null ? exercises.hashCode() : 0);
        result = 31 * result + exerciseTime;
        result = 31 * result + restTime;
        result = 31 * result + roundInterval;
        result = 31 * result + rounds;
        result = 31 * result + (randomOrder ? 1 : 0);
        return result;
    }
}