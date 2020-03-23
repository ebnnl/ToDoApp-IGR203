package com.example.todolist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    private RadioGroup responsibleRadioGroup; // Ensemble de radio button pour choisir le responsable

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
        responsibleRadioGroup = findViewById(R.id.activity_create_task_responsible_group);

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
                updateResponsibles(); // Mettre à jour la liste des responsables en fonction du groupe
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Réaction au choix du responsable :
        responsibleRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                List<Person> persons = groupsList.getGroup(groupName).getPersons();
               responsibleName = persons.get(checkedId).getName();
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
                Person person = group.getPerson(responsibleName);
                Task newTask = new Task(name, priority, deadline, group, person);
                dataBase.addTask(newTask);

                // Revenir à l'activité principale
                Intent mainActivity = new Intent(CreateTaskActivity.this, MainActivity.class);
                startActivity(mainActivity);
                CreateTaskActivity.this.finish();
            }
        });


    }

    // Fonction pour mettre à jour la liste des responsables
    public void updateResponsibles(){
        // Récupérer la liste des personnes
        List<Person> persons = groupsList.getGroup(groupName).getPersons();
        // Vider l'ensemble des radio button
        responsibleRadioGroup.removeAllViews();
        // Ajouter un radio Button pour chaque personne
        for (int i=0; i<persons.size(); i++){
            Person person = persons.get(i);
            RadioButton radioButtonPerson = new RadioButton(this);
            radioButtonPerson.setText(person.getName());
            radioButtonPerson.setId(i);
            int image = person.getImage();
            Drawable img = this.getResources().getDrawable(image);
            img.setBounds(0, 0, 150, 150);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                radioButtonPerson.setBackground(img);
            }
            radioButtonPerson.setGravity(Gravity.BOTTOM);
            responsibleRadioGroup.addView(radioButtonPerson);
        }
    }
}
