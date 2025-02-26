package com.droidev.personaltrainer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ConfigActivity extends AppCompatActivity {

    private EditText exerciseType, exercisesInput, exerciseTimeInput, restTimeInput, roundIntervalInput, roundsInput;
    private CheckBox randomOrderCheckbox;

    private SharedPreferences sharedPreferences;
    private final Gson gson = new Gson(); // Instância do Gson para trabalhar com JSON
    private ArrayList<WorkoutSet> workoutSets; // Lista de WorkoutSet
    private int editPosition = -1; // Posição do conjunto sendo editado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        setTitle(getString(R.string.config_title));

        // Inicializa os componentes da UI
        exerciseType = findViewById(R.id.exerciseType);
        exercisesInput = findViewById(R.id.exercisesInput);
        exerciseTimeInput = findViewById(R.id.exerciseTimeInput);
        restTimeInput = findViewById(R.id.restTimeInput);
        roundIntervalInput = findViewById(R.id.roundIntervalInput);
        roundsInput = findViewById(R.id.roundsInput);
        randomOrderCheckbox = findViewById(R.id.randomOrderCheckbox);
        Button saveButton = findViewById(R.id.saveButton);

        sharedPreferences = getSharedPreferences("WorkoutPrefs", Context.MODE_PRIVATE);
        loadWorkoutSets(); // Carrega a lista de treinos salva

        // Verifica se há um conjunto sendo editado
        Intent intent = getIntent();
        if (intent.hasExtra("workoutSet")) {
            String workoutSetJson = intent.getStringExtra("workoutSet");
            // Conjunto que está sendo editado
            WorkoutSet workoutSetToEdit = gson.fromJson(workoutSetJson, WorkoutSet.class);

            // Encontra a posição do conjunto na lista
            for (int i = 0; i < workoutSets.size(); i++) {
                if (workoutSets.get(i).equals(workoutSetToEdit)) {
                    editPosition = i;
                    break;
                }
            }

            // Preenche os campos com os valores
            exerciseType.setText(workoutSetToEdit.getType()); // Preenche o tipo de treino
            exercisesInput.setText(workoutSetToEdit.getExercises());
            exerciseTimeInput.setText(String.valueOf(workoutSetToEdit.getExerciseTime()));
            restTimeInput.setText(String.valueOf(workoutSetToEdit.getRestTime()));
            roundIntervalInput.setText(String.valueOf(workoutSetToEdit.getRoundInterval()));
            roundsInput.setText(String.valueOf(workoutSetToEdit.getRounds()));
            randomOrderCheckbox.setChecked(workoutSetToEdit.isRandomOrder());
        }

        saveButton.setOnClickListener(v -> saveWorkoutSet());
    }

    private void loadWorkoutSets() {
        String json = sharedPreferences.getString("workoutSets", "[]");
        Log.d("DEBUG", "JSON salvo: " + json);

        // Verifica se o JSON está malformado (array de strings)
        if (json.startsWith("[\"") && json.endsWith("\"]")) {
            // Remove os colchetes e as aspas externas
            json = json.substring(2, json.length() - 2);
            // Substitui aspas escapadas por aspas normais
            json = json.replace("\\\"", "\"");
            // Converte para um array de JSON objects
            json = "[" + json + "]";
        }

        // Parse o JSON para ArrayList<WorkoutSet>
        Type type = new TypeToken<ArrayList<WorkoutSet>>() {}.getType();
        workoutSets = gson.fromJson(json, type);

        if (workoutSets == null) {
            workoutSets = new ArrayList<>();
        }
    }

    private void saveWorkoutSet() {
        String typeText = exerciseType.getText().toString().trim(); // Captura o tipo de treino
        String exercisesText = exercisesInput.getText().toString().trim();

        if (typeText.isEmpty()) {
            exerciseType.setError(getString(R.string.error_exercise_type));
            return;
        }

        if (exercisesText.isEmpty()) {
            exercisesInput.setError(getString(R.string.error_exercises_input));
            return;
        }

        try {
            int exerciseTime = Integer.parseInt(exerciseTimeInput.getText().toString());
            int restTime = Integer.parseInt(restTimeInput.getText().toString());
            int roundInterval = Integer.parseInt(roundIntervalInput.getText().toString());
            int rounds = Integer.parseInt(roundsInput.getText().toString());
            boolean randomOrder = randomOrderCheckbox.isChecked();

            if (exerciseTime <= 0 || restTime <= 0 || roundInterval < 0 || rounds <= 0) {
                throw new NumberFormatException();
            }

            // Cria um novo objeto WorkoutSet
            WorkoutSet workoutSet = new WorkoutSet();
            workoutSet.setType(typeText); // Define o tipo de treino
            workoutSet.setExercises(exercisesText);
            workoutSet.setExerciseTime(exerciseTime);
            workoutSet.setRestTime(restTime);
            workoutSet.setRoundInterval(roundInterval);
            workoutSet.setRounds(rounds);
            workoutSet.setRandomOrder(randomOrder);

            // Adiciona ou atualiza o conjunto na lista
            if (editPosition != -1) {
                // Atualiza o conjunto existente
                workoutSets.set(editPosition, workoutSet);
            } else {
                // Adiciona um novo conjunto
                workoutSets.add(workoutSet);
            }

            // Salva a lista atualizada no SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("workoutSets", gson.toJson(workoutSets));
            editor.apply();

            Toast.makeText(this, getString(R.string.toast_save_success), Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish(); // Fecha a tela de configuração

        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.toast_invalid_values), Toast.LENGTH_SHORT).show();
        }
    }
}