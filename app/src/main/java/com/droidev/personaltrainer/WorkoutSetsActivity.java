package com.droidev.personaltrainer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class WorkoutSetsActivity extends AppCompatActivity {

    private RecyclerView setsRecyclerView;
    private Button addSetButton;
    private WorkoutSetsAdapter adapter;
    private ArrayList<WorkoutSet> workoutSets;
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson(); // Instância do Gson para salvar/carregar JSON

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_sets);

        setTitle("Conjunto de Exercícios");

        setsRecyclerView = findViewById(R.id.setsRecyclerView);
        addSetButton = findViewById(R.id.addSetButton);

        sharedPreferences = getSharedPreferences("WorkoutPrefs", Context.MODE_PRIVATE);
        loadWorkoutSets(); // Carregar os exercícios salvos

        setsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WorkoutSetsAdapter(this, workoutSets, position -> {
            WorkoutSet selectedSet = workoutSets.get(position); // Obtém o conjunto selecionado

            Toast.makeText(this, "Selecionado: " + selectedSet.getExercises(), Toast.LENGTH_SHORT).show();

            // Salva o conjunto selecionado no SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selectedSet", gson.toJson(selectedSet));
            editor.apply();

            // Retorna para a MainActivity
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        setsRecyclerView.setAdapter(adapter);

        // Configurar Swipe para remover ou editar
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final WorkoutSet workoutSet = workoutSets.get(position);

                if (direction == ItemTouchHelper.LEFT) {
                    // Mostrar caixa de diálogo de confirmação
                    new AlertDialog.Builder(WorkoutSetsActivity.this)
                            .setTitle("Remover Conjunto")
                            .setMessage("Tem certeza que deseja remover o conjunto?")
                            .setCancelable(false)
                            .setPositiveButton("Sim", (dialog, which) -> {
                                // Remover item após confirmação
                                workoutSets.remove(position);
                                adapter.removeItem(position);
                                saveWorkoutSets(); // Salvar alterações
                                Toast.makeText(WorkoutSetsActivity.this, "Conjunto removido", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Cancelar", (dialog, which) -> {
                                // Cancelar a ação e reverter o swipe
                                adapter.notifyItemChanged(position);
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert) // Ícone de alerta
                            .show();
                } else if (direction == ItemTouchHelper.RIGHT) {
                    // Editar item
                    Intent intent = new Intent(WorkoutSetsActivity.this, ConfigActivity.class);
                    intent.putExtra("workoutSet", gson.toJson(workoutSet));
                    startActivityForResult(intent, 2);
                }
            }
        };

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(setsRecyclerView);

        addSetButton.setOnClickListener(v -> {
            Intent intent = new Intent(WorkoutSetsActivity.this, ConfigActivity.class);
            startActivityForResult(intent, 1);
        });
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

    private void saveWorkoutSets() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("workoutSets", gson.toJson(workoutSets)); // Salva o JSON corretamente
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadWorkoutSets(); // Recarrega os dados
            adapter.updateWorkoutSets(workoutSets); // Atualiza o adapter
            saveWorkoutSets(); // Salva as alterações
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWorkoutSets(); // Recarrega os dados ao retornar à atividade
        adapter.updateWorkoutSets(workoutSets); // Atualiza o adapter
    }
}