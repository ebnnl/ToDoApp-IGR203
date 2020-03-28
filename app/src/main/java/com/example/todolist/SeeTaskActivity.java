package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class SeeTaskActivity extends AppCompatActivity {

    // Activité pour voir une tâche

    private DAOBase dataBase; // Base de données sur laquelle agir
    private GroupsList groupsList; // Ensemble des groupes
    private Task taskToSee; // Tâche que l'on veut afficher

    private TextView taskNameTextView; // TextView pour afficher le nom de la tâche
    private TextView priorityTextView; // TextView pour afficher la priorité (valeur numérique)

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

        // Récupérer la tâche sur laquelle on a cliqué pour ouvrir cette activity (la
        // tâche à afficher, et son groupe)
        Bundle b = getIntent().getExtras();
        if(b != null) {
            String taskName = b.getString("taskName");
            String groupName = b.getString("groupName");
            taskToSee = groupsList.getGroup(groupName).getTask(taskName);
        }

        loadData();
    }

    @Override
    public void onRestart() {
        loadData();
    }

    public void loadData() {
        dataBase = new DAOBase(this);
        dataBase.open();
        this.groupsList = dataBase.getGroupsList();

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
        priorityTextView.setText(String.valueOf(taskToSee.getPriority()));

        // A compléter pour afficher les autres informations de la tâche
    }
}
