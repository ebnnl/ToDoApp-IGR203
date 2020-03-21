package com.example.todolist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CreateTaskActivity extends AppCompatActivity {

    // Activité pour créer une tpche de groupe

    private DAOBase dataBase; // Pour manipuler la base
    private GroupsList groupsList;
    private EditText nameEditText; // Champ pour entrer le nom de la tâche
    private FloatingActionButton validateButton; // Bouton pour enregistrer
    private Spinner groupSpinner; // Spinner pour choisir le groupe

    private String name; // Nom de la tâche
    private Date deadline = new Date(00000000);
    private String groupName; // Nom du groupe de la tâche
    private int priority = 50;
    private String responsibleName = "Moi"; // Nom du responsable de la tâche


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        dataBase = new DAOBase(this);
        dataBase.open();
        groupsList = dataBase.getGroupsList();

        setTitle("Créer une tâche");


        nameEditText = (EditText) findViewById(R.id.activity_create_task_name_input);
        validateButton = findViewById(R.id.activity_create_task_validate);
        groupSpinner = (Spinner) findViewById(R.id.activity_create_task_group_spinner);

        validateButton.setEnabled(false); // Initialement, on ne peut pas valider (attendre qu'un nom
        // de tâche soit entré)
        validateButton.setVisibility(View.GONE);

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Quand un nom de tâche est entré, rendre le bouton enregistrer visible
                validateButton.setEnabled(true);
                validateButton.setVisibility(View.VISIBLE);
                name = nameEditText.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Configurer le spinner pour choisir le groupe de la tâche
        List groupsListNames = new ArrayList<String>();
        groupsListNames = groupsList.getNames();
        ArrayAdapter adapterGroups = new ArrayAdapter(this, android.R.layout.simple_spinner_item, groupsListNames);
        adapterGroups.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(adapterGroups);

        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                groupName = (String) groupSpinner.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Réaction au bouton valider :
        validateButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                int duration = Toast.LENGTH_SHORT;
                // Afficher un toast
                Toast toast = Toast.makeText(CreateTaskActivity.this, "Tâche créée", duration);
                toast.show();
                // Ajouter la tâche à la base
                Group group = groupsList.getGroup(groupName);
                Person person = new Person(responsibleName);
                Task newTask = new Task(name, priority, deadline, group, person);
                dataBase.addTask(newTask);

                // Revenir à l'activité principale
                Intent mainActivity = new Intent(CreateTaskActivity.this, MainActivity.class);
                startActivity(mainActivity);
                CreateTaskActivity.this.finish();
            }
        });


    }
}
