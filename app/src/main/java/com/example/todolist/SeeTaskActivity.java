package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class SeeTaskActivity extends AppCompatActivity {

    // Activité pour voir une tâche

    private DAOBase dataBase; // Base de données sur laquelle agir
    private GroupsList groupsList; // Ensemble des groupes
    private Task taskToSee; // Tâche que l'on veut afficher

    private EditText taskNameEditText; // TextView pour afficher le nom de la tâche
    private TextView priorityTextView; // TextView pour afficher la priorité (valeur numérique)
    private DottedSeekBar prioritySeekBar; // Barre pour afficher la priorité
    private Button postIt;
    private TextView priorityInfTextView; // Texte pour afficher la tâche de priorité inférieure
    private TextView prioritySupTextView; // Texte pour afficher la tâche de priorité inférieure
    private FloatingActionButton validateButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_task);
        setTitle("Voir une tâche");

        dataBase = new DAOBase(this);
        dataBase.open();
        this.groupsList = dataBase.getGroupsList();

        taskNameEditText = findViewById(R.id.activity_see_task_task_name);
        priorityTextView = findViewById(R.id.activity_see_task_priority);
        prioritySeekBar = findViewById(R.id.activity_see_task_priority_bar);
        postIt = findViewById(R.id.activity_see_task_postit);
        priorityInfTextView = findViewById(R.id.activity_see_task_priority_inf_text);
        prioritySupTextView = findViewById(R.id.activity_see_task_priority_sup_text);
        validateButton = findViewById(R.id.activity_see_task_validate);

        // Récupérer la tâche sur laquelle on a cliqué pour ouvrir cette activity (la
        // tâche à afficher, et son groupe)
        Bundle b = getIntent().getExtras();
        if(b != null) {
            String taskName = b.getString("taskName");
            String groupName = b.getString("groupName");
            taskToSee = groupsList.getGroup(groupName).getTask(taskName);
        }

        // Réaction au changement de nom
        taskNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                taskToSee.setName(taskNameEditText.getText().toString());
                postIt.setText(taskNameEditText.getText().toString());
            }
        });

        // Réaction au changement de priorité
        prioritySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                taskToSee.setPriority(progress);

                int size = progress * 5 + 150;
                postIt.setWidth(size);
                postIt.setHeight(size);
                priorityTextView.setText(Integer.toString(progress));

                // Chercher les tâches qui encadrent la priorité
                Group group = groupsList.getGroup(taskToSee.getGroup().getName());
                String taskInf = group.getTaskPriorityInf(progress);
                String taskSup = group.getTaskPrioritySup(progress);
                if (taskInf.equals("") ) {
                    priorityInfTextView.setText("");
                } else {
                    priorityInfTextView.setText("Supérieure à " + taskInf.toLowerCase());
                }
                if (taskSup.equals("")) {
                    prioritySupTextView.setText("");
                } else {
                    prioritySupTextView.setText("Inférieure à " + taskSup.toLowerCase());
                }

            }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        // Réaction au bouton valider :
        validateButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                // Modifier la tâche dans la base
                dataBase.removeTask(taskToSee);
                dataBase.addTask(taskToSee);

                // Revenir à l'activité principale
                SeeTaskActivity.this.finish();

            }
        });

        loadContent();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void loadContent(){
        // Afficher le post it
        postIt.setText(taskToSee.getName());
        int size = taskToSee.getPriority()*5+150;
        postIt.setWidth(size);
        postIt.setHeight(size);
        postIt.setBackgroundColor(getResources().getColor(taskToSee.getPerson().getColorInt()));

        // Afficher le nom de la tâche
        taskNameEditText.setText(taskToSee.getName());

        // Afficher la priorité de la tâche
        priorityTextView.setText(String.valueOf(taskToSee.getPriority()));

        // Ajouter un marque sur la barre de priorité pour chaque tâche existante
        List<Task> tasks = groupsList.getGroup(taskToSee.getGroup().getName()).getTasks();
        int[] dots = new int[tasks.size()-1];
        for (int i=0; i<tasks.size(); i++){
            Task task = tasks.get(i);
            if (!task.getName().equals(taskToSee.getName())){
                dots[i] = task.getPriority()+2;
            }
        }
        prioritySeekBar.setDots(dots);
        prioritySeekBar.setDotsDrawable(android.R.drawable.radiobutton_off_background);
        prioritySeekBar.setProgress(taskToSee.getPriority());
    }

}
