package com.droidev.personaltrainer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
    private ArrayList<String> workoutSets;
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson(); // Instância do Gson para salvar/carregar JSON

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_sets);

        setsRecyclerView = findViewById(R.id.setsRecyclerView);
        addSetButton = findViewById(R.id.addSetButton);

        sharedPreferences = getSharedPreferences("WorkoutPrefs", Context.MODE_PRIVATE);
        loadWorkoutSets(); // Carregar os exercícios salvos

        setsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WorkoutSetsAdapter(this, workoutSets);
        setsRecyclerView.setAdapter(adapter);

        // Configurar Swipe para remover ou editar
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                String workoutSet = workoutSets.get(position);

                if (direction == ItemTouchHelper.LEFT) {
                    // Remover item
                    workoutSets.remove(position);
                    adapter.removeItem(position);
                    saveWorkoutSets(); // Salvar alterações
                    Toast.makeText(WorkoutSetsActivity.this, "Conjunto removido: " + workoutSet, Toast.LENGTH_SHORT).show();
                } else if (direction == ItemTouchHelper.RIGHT) {
                    // Editar item
                    Intent intent = new Intent(WorkoutSetsActivity.this, ConfigActivity.class);
                    intent.putExtra("workoutSet", workoutSet);
                    startActivityForResult(intent, 2);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                Paint paint = new Paint();

                if (dX > 0) {
                    paint.setColor(Color.parseColor("#388E3C"));
                    c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom(), paint);
                } else {
                    paint.setColor(Color.parseColor("#D32F2F"));
                    c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom(), paint);
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
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
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        workoutSets = gson.fromJson(json, type);

        if (workoutSets == null) {
            workoutSets = new ArrayList<>();
        }
    }

    private void saveWorkoutSets() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("workoutSets", gson.toJson(workoutSets));
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadWorkoutSets();
            adapter.updateWorkoutSets(workoutSets);
            saveWorkoutSets();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWorkoutSets();
        adapter.updateWorkoutSets(workoutSets);
    }
}
