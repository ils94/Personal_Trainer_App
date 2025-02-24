package com.droidev.personaltrainer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView timerTextView;
    private Button startButton, pauseButton, stopButton;
    private CountDownTimer countDownTimer;
    private TextToSpeech textToSpeech;
    private ArrayList<String> exercises;
    private int exerciseTime, restTime, roundInterval, rounds;
    private boolean randomOrder;
    private int currentRound = 0;
    private int currentExerciseIndex = 0;
    private boolean isPaused = false;
    private long timeRemaining = 0; // Tempo restante do timer
    private Runnable onFinishAction;
    private String currentDisplayText = "";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar componentes da interface
        timerTextView = findViewById(R.id.timerTextView);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);

        // Carregar configurações
        sharedPreferences = getSharedPreferences("WorkoutPrefs", Context.MODE_PRIVATE);
        loadSettings();

        // Configurar TextToSpeech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.getDefault());
            } else {
                Log.e("TextToSpeech", "Erro ao inicializar o TextToSpeech.");
            }
        });

        // Configurar listeners
        setupListeners();
    }

    private void setupListeners() {
        startButton.setOnClickListener(v -> startWorkout());
        pauseButton.setOnClickListener(v -> togglePause());
        stopButton.setOnClickListener(v -> stopWorkout());
    }

    private void loadSettings() {
        String exercisesText = sharedPreferences.getString("exercises", "Exercício 1, Exercício 2");
        exercises = new ArrayList<>(Arrays.asList(exercisesText.split(",")));
        exerciseTime = sharedPreferences.getInt("exerciseTime", 30);
        restTime = sharedPreferences.getInt("restTime", 10);
        roundInterval = sharedPreferences.getInt("roundInterval", 20);
        rounds = sharedPreferences.getInt("rounds", 3);
        randomOrder = sharedPreferences.getBoolean("randomOrder", false);
        if (randomOrder) {
            Collections.shuffle(exercises);
        }
    }

    private void startWorkout() {
        if (countDownTimer != null) return; // Evita múltiplos timers
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        currentRound = 0;
        currentExerciseIndex = 0;
        isPaused = false;
        timeRemaining = 0;

        speak("Iniciando em 10 segundos!");

        // Contagem regressiva inicial (10 segundos)
        new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Iniciando em " + (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                startExercise();
            }
        }.start();
    }

    private void startExercise() {
        if (currentRound >= rounds) {
            speak("Treino Finalizado!");

            timerTextView.setText("Treino Finalizado!");
            startButton.setEnabled(true);
            countDownTimer = null;
            return;
        }

        String currentExercise = exercises.get(currentExerciseIndex).trim();

        speak(currentExercise);

        currentDisplayText = currentExercise;

        // Ao finalizar o exercício, inicia o descanso
        onFinishAction = this::startRest;

        countDownTimer = new CountDownTimer(exerciseTime * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                timerTextView.setText(currentDisplayText + "\n" + (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                startRest();
            }
        }.start();
    }

    private void startRest() {
        speak("Descanso");

        currentDisplayText = "Descanso";

        // Ao finalizar o descanso, passa para o próximo exercício
        onFinishAction = this::nextExercise;

        countDownTimer = new CountDownTimer(restTime * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                timerTextView.setText(currentDisplayText + "\n" + (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                nextExercise();
            }
        }.start();
    }

    private void nextExercise() {
        currentExerciseIndex++;
        if (currentExerciseIndex >= exercises.size()) {
            currentExerciseIndex = 0;
            currentRound++;
            if (currentRound < rounds) {
                startRoundInterval();
            } else {
                speak("Treino Finalizado!");

                timerTextView.setText("Treino Finalizado!");
                startButton.setEnabled(true);
                countDownTimer = null;
            }
        } else {
            startExercise();
        }
    }

    private void startRoundInterval() {
        speak("Próxima rodada em breve");

        currentDisplayText = "Intervalo";

        // Ao finalizar o intervalo, inicia um novo exercício
        onFinishAction = this::startExercise;

        countDownTimer = new CountDownTimer(roundInterval * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                timerTextView.setText(currentDisplayText + "\n" + (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                startExercise();
            }
        }.start();
    }

    private void togglePause() {

        if (isPaused) {
            // Retomar o timer
            isPaused = false;
            pauseButton.setText("Pausar");
            resumeTimer();
        } else {
            if (countDownTimer != null) {
                // Pausar o timer
                isPaused = true;
                pauseButton.setText("Continuar");

                countDownTimer.cancel();
                countDownTimer = null;
            }
        }
    }

    private void resumeTimer() {
        if (timeRemaining <= 0) {
            Log.e("ResumeTimer", "Tempo restante inválido: " + timeRemaining);
            return;
        }

        countDownTimer = new CountDownTimer(timeRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                timerTextView.setText(currentDisplayText + "\n" + (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                if (onFinishAction != null) {
                    onFinishAction.run();
                }
            }
        }.start();

        Log.d("ResumeTimer", "Timer retomado com " + timeRemaining + "ms restantes.");
    }

    private void stopWorkout() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        currentRound = 0;
        currentExerciseIndex = 0;
        timeRemaining = 0;
        timerTextView.setText("0");
        startButton.setEnabled(true);
        pauseButton.setText("Pausar");
        isPaused = false;
    }

    private void speak(String text) {
        if (textToSpeech != null && textToSpeech.getEngines().size() > 0) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadSettings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, ConfigActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}