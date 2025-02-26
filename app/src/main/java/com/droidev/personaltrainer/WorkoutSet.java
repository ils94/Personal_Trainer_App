package com.droidev.personaltrainer;

public class WorkoutSet {
    private String exercises;
    private int exerciseTime;
    private int restTime;
    private int roundInterval;
    private int rounds;
    private boolean randomOrder;

    // Getters e Setters
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
}

