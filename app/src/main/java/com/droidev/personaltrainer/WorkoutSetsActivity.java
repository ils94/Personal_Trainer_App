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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class WorkoutSetsActivity extends AppCompatActivity {

    private RecyclerView setsRecyclerView;
    private Button addSetButton;
    private WorkoutSetsAdapter adapter;
    private ArrayList<String> workoutSets;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_sets);

        setsRecyclerView = findViewById(R.id.setsRecyclerView);
        addSetButton = findViewById(R.id.addSetButton);

        sharedPreferences = getSharedPreferences("WorkoutPrefs", Context.MODE_PRIVATE);

        // Carregar conjuntos de exercícios salvos
        loadWorkoutSets();

        // Configurar o RecyclerView
        setsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WorkoutSetsAdapter(this, workoutSets, sharedPreferences);
        setsRecyclerView.setAdapter(adapter);

        // Configurar o ItemTouchHelper para swipe left/right
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // Não é necessário implementar o drag
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                String workoutSet = workoutSets.get(position);

                if (direction == ItemTouchHelper.LEFT) {
                    // Swipe para a esquerda: Remover
                    adapter.removeItem(position); // Remove o item da lista e notifica o Adapter

                    // Atualizar o SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Set<String> sets = new HashSet<>(workoutSets); // Cria um novo Set com a lista atualizada
                    editor.putStringSet("workoutSets", sets); // Salva o Set no SharedPreferences
                    editor.apply(); // Aplica as mudanças

                    Toast.makeText(WorkoutSetsActivity.this, "Conjunto removido: " + workoutSet, Toast.LENGTH_SHORT).show();
                } else if (direction == ItemTouchHelper.RIGHT) {
                    // Swipe para a direita: Editar
                    Intent intent = new Intent(WorkoutSetsActivity.this, ConfigActivity.class);
                    intent.putExtra("workoutSet", workoutSet);
                    startActivityForResult(intent, 2); // Usar requestCode 2 para edição
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // Personalizar a aparência do swipe (opcional)
                View itemView = viewHolder.itemView;
                Paint paint = new Paint();

                if (dX > 0) {
                    // Swipe para a direita (Editar)
                    paint.setColor(Color.parseColor("#388E3C")); // Verde
                    c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom(), paint);
                } else {
                    // Swipe para a esquerda (Remover)
                    paint.setColor(Color.parseColor("#D32F2F")); // Vermelho
                    c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom(), paint);
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(setsRecyclerView);

        // Listener para adicionar novo conjunto
        addSetButton.setOnClickListener(v -> {
            Intent intent = new Intent(WorkoutSetsActivity.this, ConfigActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    private void loadWorkoutSets() {
        Set<String> sets = sharedPreferences.getStringSet("workoutSets", new HashSet<>());
        workoutSets = new ArrayList<>(sets);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // Recarregar os conjuntos após adicionar ou editar
            loadWorkoutSets();
            adapter.updateWorkoutSets(workoutSets); // Atualiza o Adapter com a nova lista
        }
    }
}