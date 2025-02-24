package com.droidev.personaltrainer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    private EditText exercisesInput, exerciseTimeInput, restTimeInput, roundIntervalInput, roundsInput;
    private CheckBox randomOrderCheckbox;
    private Button saveButton;

    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson(); // Instância do Gson para trabalhar com JSON
    private ArrayList<String> workoutSets;
    private String workoutSetToEdit; // Nome do conjunto que está sendo editado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        // Inicializa os componentes da UI
        exercisesInput = findViewById(R.id.exercisesInput);
        exerciseTimeInput = findViewById(R.id.exerciseTimeInput);
        restTimeInput = findViewById(R.id.restTimeInput);
        roundIntervalInput = findViewById(R.id.roundIntervalInput);
        roundsInput = findViewById(R.id.roundsInput);
        randomOrderCheckbox = findViewById(R.id.randomOrderCheckbox);
        saveButton = findViewById(R.id.saveButton);

        sharedPreferences = getSharedPreferences("WorkoutPrefs", Context.MODE_PRIVATE);
        loadWorkoutSets(); // Carrega a lista de treinos salva

        // Verifica se há um conjunto sendo editado
        Intent intent = getIntent();
        if (intent.hasExtra("workoutSet")) {
            workoutSetToEdit = intent.getStringExtra("workoutSet");
            exercisesInput.setText(workoutSetToEdit);
        }

        // Carregar configurações anteriores
        exerciseTimeInput.setText(String.valueOf(sharedPreferences.getInt("exerciseTime", 30)));
        restTimeInput.setText(String.valueOf(sharedPreferences.getInt("restTime", 10)));
        roundIntervalInput.setText(String.valueOf(sharedPreferences.getInt("roundInterval", 20)));
        roundsInput.setText(String.valueOf(sharedPreferences.getInt("rounds", 3)));
        randomOrderCheckbox.setChecked(sharedPreferences.getBoolean("randomOrder", false));

        saveButton.setOnClickListener(v -> saveWorkoutSet());
    }

    private void loadWorkoutSets() {
        String json = sharedPreferences.getString("workoutSets", "[]");
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        workoutSets = gson.fromJson(json, type);

        if (workoutSets == null) {
            workoutSets = new ArrayList<>();
        }
    }

    private void saveWorkoutSet() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String exercisesText = exercisesInput.getText().toString().trim();

        if (exercisesText.isEmpty()) {
            exercisesInput.setError("Insira pelo menos um exercício");
            return;
        }

        try {
            int exerciseTime = Integer.parseInt(exerciseTimeInput.getText().toString());
            int restTime = Integer.parseInt(restTimeInput.getText().toString());
            int roundInterval = Integer.parseInt(roundIntervalInput.getText().toString());
            int rounds = Integer.parseInt(roundsInput.getText().toString());

            if (exerciseTime <= 0 || restTime <= 0 || roundInterval < 0 || rounds <= 0) {
                throw new NumberFormatException();
            }

            // Remove o conjunto antigo se estiver editando
            if (workoutSetToEdit != null) {
                workoutSets.remove(workoutSetToEdit);
            }

            // Adiciona o novo conjunto
            workoutSets.add(exercisesText);

            // Salva lista atualizada no SharedPreferences
            editor.putString("workoutSets", gson.toJson(workoutSets));
            editor.putInt("exerciseTime", exerciseTime);
            editor.putInt("restTime", restTime);
            editor.putInt("roundInterval", roundInterval);
            editor.putInt("rounds", rounds);
            editor.putBoolean("randomOrder", randomOrderCheckbox.isChecked());

            editor.apply();

            Toast.makeText(this, "Configurações salvas!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish(); // Fecha a tela de configuração

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Insira valores válidos!", Toast.LENGTH_SHORT).show();
        }
    }
}
