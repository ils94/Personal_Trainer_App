package com.droidev.personaltrainer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView timerTextView;
    private Button startButton, pauseButton, stopButton;
    private CountDownTimer countDownTimer, initialCountDownTimer;
    private TextToSpeech textToSpeech = null;
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
    private MediaPlayer mediaPlayer;
    private final Gson gson = new Gson(); // Instância do Gson para trabalhar com JSON
    MenuItem workoutSetsItem;

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

        // Configurar listeners
        setupListeners();
    }

    private void setupListeners() {
        startButton.setOnClickListener(v -> startWorkout());
        pauseButton.setOnClickListener(v -> togglePause());
        stopButton.setOnClickListener(v -> stopWorkout());
    }

    private void loadSettings() {
        // Carregar o JSON do conjunto selecionado
        String selectedSetJson = sharedPreferences.getString("selectedSet", "");

        if (!selectedSetJson.isEmpty()) {
            // Converter o JSON para o objeto WorkoutSet usando o Gson
            WorkoutSet selectedSet = gson.fromJson(selectedSetJson, WorkoutSet.class);

            // Agora usa os valores do objeto WorkoutSet para preencher as variáveis
            exercises = new ArrayList<>(Arrays.asList(selectedSet.getExercises().split(",")));
            exerciseTime = selectedSet.getExerciseTime();
            restTime = selectedSet.getRestTime();
            roundInterval = selectedSet.getRoundInterval();
            rounds = selectedSet.getRounds();
            randomOrder = selectedSet.isRandomOrder();

            // Se a ordem for aleatória, embaralha os exercícios
        } else {
            // Se não encontrar o conjunto, usar os valores padrão
            String exercisesText = sharedPreferences.getString("exercises", "Exercise 1, Exercise 2");
            exercises = new ArrayList<>(Arrays.asList(exercisesText.split(",")));
            exerciseTime = sharedPreferences.getInt("exerciseTime", 30);
            restTime = sharedPreferences.getInt("restTime", 10);
            roundInterval = sharedPreferences.getInt("roundInterval", 20);
            rounds = sharedPreferences.getInt("rounds", 3);
            randomOrder = sharedPreferences.getBoolean("randomOrder", false);

        }
        if (randomOrder) {
            Collections.shuffle(exercises);
        }
    }


    private void startWorkout() {
        if (countDownTimer != null) return;
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        currentRound = 0;
        currentExerciseIndex = 0;
        isPaused = false;
        timeRemaining = 0;
        workoutSetsItem.setEnabled(false);

        initialCountDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;

                // Update the timer text
                timerTextView.setText(String.valueOf(secondsLeft));

                // Beep during the last 5 seconds
                if (secondsLeft <= 5) {
                    playBeep();
                }
            }

            @Override
            public void onFinish() {
                startExercise();
            }
        }.start();

        speak(getString(R.string.starting_in_10_seconds));
    }

    @SuppressLint("SetTextI18n")
    private void startExercise() {
        if (currentRound >= rounds) {

            timerTextView.setText(getString(R.string.workout_finished));
            startButton.setEnabled(true);
            countDownTimer = null;
            initialCountDownTimer = null;

            speak(getString(R.string.workout_finished));

            return;
        }
        String currentExercise = exercises.get(currentExerciseIndex).trim();

        currentDisplayText = currentExercise;
        // Ao finalizar o exercício, inicia o descanso
        onFinishAction = this::startRest;
        countDownTimer = new CountDownTimer(exerciseTime * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                long secondsLeft = millisUntilFinished / 1000;

                // Update the timer text
                timerTextView.setText(currentDisplayText + "\n" + secondsLeft);

                // Beep during the last 5 seconds
                if (secondsLeft <= 5) {
                    playBeep();
                }
            }

            @Override
            public void onFinish() {
                startRest();
            }
        }.start();

        speak(currentExercise);
    }

    @SuppressLint("SetTextI18n")
    private void nextExercise() {
        currentExerciseIndex++;

        if (currentExerciseIndex >= exercises.size()) { // Se terminou os exercícios do round
            currentExerciseIndex = 0;
            currentRound++;

            if (currentRound < rounds) {
                startRoundInterval(); // Faz o intervalo entre rounds
            } else {

                timerTextView.setText(getString(R.string.workout_finished));
                startButton.setEnabled(true);
                countDownTimer = null;
                workoutSetsItem.setEnabled(true);

                speak(getString(R.string.workout_finished));
            }
        } else {
            startExercise(); // Próximo exercício normalmente
        }
    }

    private void startRest() {
        // Se este for o último exercício do round e ainda houver rounds, NÃO faz o descanso, vai direto para o intervalo
        if (currentExerciseIndex == exercises.size() - 1 && currentRound < rounds) {
            nextExercise(); // Vai direto para o intervalo do round
            return;
        }

        currentDisplayText = getString(R.string.rest);
        onFinishAction = this::nextExercise;

        countDownTimer = new CountDownTimer(restTime * 1000L, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                long secondsLeft = millisUntilFinished / 1000;
                timerTextView.setText(currentDisplayText + "\n" + secondsLeft);

                if (secondsLeft <= 5) {
                    playBeep();
                }
            }

            @Override
            public void onFinish() {
                nextExercise();
            }
        }.start();

        speak(getString(R.string.rest));
    }

    private void startRoundInterval() {

        currentDisplayText = getString(R.string.interval);

        // Ao finalizar o intervalo, inicia um novo exercício
        onFinishAction = this::startExercise;

        countDownTimer = new CountDownTimer(roundInterval * 1000L, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;

                long secondsLeft = millisUntilFinished / 1000;

                // Update the timer text
                timerTextView.setText(currentDisplayText + "\n" + secondsLeft);

                // Beep during the last 5 seconds
                if (secondsLeft <= 5) {
                    playBeep();
                }
            }

            @Override
            public void onFinish() {
                startExercise();
            }
        }.start();

        if (randomOrder) {
            Collections.shuffle(exercises);
        }

        speak(getString(R.string.next_round_soon));
    }

    @SuppressLint("SetTextI18n")
    private void togglePause() {

        if (isPaused) {
            // Retomar o timer
            isPaused = false;
            pauseButton.setText(getString(R.string.pause_button));
            resumeTimer();
        } else {
            if (countDownTimer != null) {
                // Pausar o timer
                isPaused = true;
                pauseButton.setText(getString(R.string.continue_button));

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
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;

                long secondsLeft = millisUntilFinished / 1000;

                // Update the timer text
                timerTextView.setText(currentDisplayText + "\n" + secondsLeft);

                // Beep during the last 5 seconds
                if (secondsLeft <= 5) {
                    playBeep();
                }
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

    @SuppressLint("SetTextI18n")
    private void stopWorkout() {
        // Cria um AlertDialog para confirmar se o usuário realmente deseja parar o treino
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.stop_workout))
                .setMessage(getString(R.string.stop_workout_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    // Se o usuário confirmar, executa o código para parar o treino

                    cleanResources();

                    currentRound = 0;
                    currentExerciseIndex = 0;
                    timeRemaining = 0;
                    timerTextView.setText("0");
                    startButton.setEnabled(true);
                    pauseButton.setText(getString(R.string.pause_button));
                    isPaused = false;
                    workoutSetsItem.setEnabled(true);
                })
                .setNegativeButton(getString(R.string.no), (dialog, which) -> {
                    // Se o usuário cancelar, não faz nada e fecha o diálogo
                    dialog.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void speak(String text) {

        if (textToSpeech == null) {
            textToSpeech = new TextToSpeech(this, status -> {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.getDefault());
                    // Speak the text once initialization is complete
                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    Log.e("TextToSpeech", "Erro ao inicializar o TextToSpeech.");
                }
            });
        } else {
            // Check if TTS is ready and engines are available
            if (!textToSpeech.getEngines().isEmpty()) {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                Log.e("TextToSpeech", "Nenhum motor TTS disponível.");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cleanResources();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadSettings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        workoutSetsItem = menu.findItem(R.id.action_workout_sets);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_workout_sets) {
            startActivity(new Intent(this, WorkoutSetsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadSettings(); // Recarrega as configurações ao retornar
        }
    }

    private void cleanResources() {

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }

        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        if (initialCountDownTimer != null) {
            initialCountDownTimer.cancel();
            initialCountDownTimer = null;
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // Helper method to play a beep sound
    private void playBeep() {
        try {
            if (mediaPlayer == null) {
                // Initialize MediaPlayer with the custom beep sound
                mediaPlayer = MediaPlayer.create(this, R.raw.beep); // Ensure 'beep.mp3' is in the 'res/raw' folder
            }
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start(); // Play the beep sound
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}