package com.example.todolist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateTaskActivity extends AppCompatActivity {

    // Activité pour créer une tpche de groupe

    private DAOBase dataBase; // Pour manipuler la base
    private GroupsList groupsList;
    private EditText nameEditText; // Champ pour entrer le nom de la tâche
    private FloatingActionButton validateButton; // Bouton pour enregistrer
    private Spinner groupSpinner; // Spinner pour choisir le groupe
    private RadioGroup responsibleRadioGroup; // Ensemble de radio button pour choisir le responsable
    private Button chooseDeadlineButton; // Bouton pour ouvrir la pop up pour choisir la deadline
    private TextView deadlineText; // Text View pour afficher la deadline
    private Dialog deadlineDialog; // Pop up pour choisir la deadline
    private SeekBar prioritySeekBar;
    private TextView priorityTextView;

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
        chooseDeadlineButton = findViewById(R.id.activity_create_task_choose_deadline_button);
        deadlineDialog = new Dialog(this);
        deadlineText = findViewById(R.id.activity_create_task_deadline_text);
        prioritySeekBar = findViewById(R.id.activity_create_task_priority_bar);
        priorityTextView = findViewById(R.id.activity_create_task_priority_text);

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

        // Configuration du bouton pour choisir la deadline
        chooseDeadlineButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Afficher les choix dans le dialogue
                deadlineDialog.setContentView(R.layout.pop_up_choose_deadline);
                // Calendar View pour choisir la deadline
                CalendarView calendarView = deadlineDialog.findViewById(R.id.pop_up_choose_deadline_calendar);
                // Mettre la date minimale à aujourd'hui
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DATE,Calendar.getInstance().getActualMinimum(Calendar.DATE));
                long date = calendar.getTime().getTime();
                calendarView.setMinDate(date);

                // Réaction au changement de date :
                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

                    @Override
                    public void onSelectedDayChange(CalendarView view, int year, int month,
                                                    int dayOfMonth) {
                       deadline = new Date(dayOfMonth, month+1, year);
                       deadlineText.setText(Integer.toString(dayOfMonth)+"/"+Integer.toString(month+1)+"/"+Integer.toString(year));
                    }
                });

                deadlineDialog.show();
            }
        });

        // Réaction au changement de priorité
        prioritySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                priorityTextView.setText(Integer.toString(progress));
                int x = seekBar.getThumb().getBounds().left;
                priorityTextView.setX(x);
                priority = progress;

                // Chercher les tâches qui encadrent la priorité
                Group group = groupsList.getGroup(groupName);
                String taskInf = group.getTaskPriorityInf(progress);
                String taskSup = group.getTaskPrioritySup(progress);
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
