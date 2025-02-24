package com.droidev.personaltrainer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ConfigActivity extends AppCompatActivity {

    private EditText exercisesInput, exerciseTimeInput, restTimeInput, roundIntervalInput, roundsInput;
    private CheckBox randomOrderCheckbox;
    private Button saveButton;

    private SharedPreferences sharedPreferences;

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

        // Inicializa o SharedPreferences
        sharedPreferences = getSharedPreferences("WorkoutPrefs", Context.MODE_PRIVATE);

        // Carrega as configurações salvas
        exercisesInput.setText(sharedPreferences.getString("exercises", ""));
        exerciseTimeInput.setText(String.valueOf(sharedPreferences.getInt("exerciseTime", 30)));
        restTimeInput.setText(String.valueOf(sharedPreferences.getInt("restTime", 10)));
        roundIntervalInput.setText(String.valueOf(sharedPreferences.getInt("roundInterval", 20)));
        roundsInput.setText(String.valueOf(sharedPreferences.getInt("rounds", 3)));
        randomOrderCheckbox.setChecked(sharedPreferences.getBoolean("randomOrder", false));

        // Salva as configurações
        saveButton.setOnClickListener(v -> {
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

                // Salva os valores no SharedPreferences
                editor.putString("exercises", exercisesText);
                editor.putInt("exerciseTime", exerciseTime);
                editor.putInt("restTime", restTime);
                editor.putInt("roundInterval", roundInterval);
                editor.putInt("rounds", rounds);
                editor.putBoolean("randomOrder", randomOrderCheckbox.isChecked());
                editor.apply();

                Toast.makeText(this, "Configurações salvas!", Toast.LENGTH_SHORT).show();
                finish(); // Fecha a tela de configuração

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Insira valores válidos!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}