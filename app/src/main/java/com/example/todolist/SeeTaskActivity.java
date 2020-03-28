package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class SeeTaskActivity extends AppCompatActivity {

    // Activité pour voir une tâche

    private DAOBase dataBase; // Base de données sur laquelle agir
    private GroupsList groupsList; // Ensemble des groupes
    private Task taskToSee; // Tâche que l'on veut afficher

    private TextView taskNameTextView; // TextView pour afficher le nom de la tâche
    private TextView priorityTextView; // TextView pour afficher la priorité (valeur numérique)
    private DottedSeekBar prioritySeekBar; // Barre pour afficher la priorité

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_task);
        setTitle("Voir une tâche");

        dataBase = new DAOBase(this);
        dataBase.open();
        this.groupsList = dataBase.getGroupsList();

        taskNameTextView = findViewById(R.id.activity_see_task_task_name);
        priorityTextView = findViewById(R.id.activity_see_task_priority);
        prioritySeekBar = findViewById(R.id.activity_see_task_priority_bar);

        // Récupérer la tâche sur laquelle on a cliqué pour ouvrir cette activity (la
        // tâche à afficher, et son groupe)
        Bundle b = getIntent().getExtras();
        if(b != null) {
            String taskName = b.getString("taskName");
            String groupName = b.getString("groupName");
            taskToSee = groupsList.getGroup(groupName).getTask(taskName);
        }

        // Afficher le nom de la tâche
        taskNameTextView.setText(taskToSee.getName());

        // Afficher la priorité de la tâche
        priorityTextView.setText(String.valueOf(taskToSee.getPriority()));

        // Ajouter un marque sur la barre de priorité pour chaque tâche existante
        List<Task> tasks = groupsList.getGroup(taskToSee.getGroup().getName()).getTasks();
        int[] dots = new int[tasks.size()];
        for (int i=0; i<tasks.size(); i++){
            Task task = tasks.get(i);
            dots[i] = task.getPriority()+2;
        }
        prioritySeekBar.setDots(dots);
        prioritySeekBar.setDotsDrawable(android.R.drawable.radiobutton_off_background);
        prioritySeekBar.setProgress(taskToSee.getPriority());
    }

    @Override
    public void onBackPressed() {
        finish();
    }


}
