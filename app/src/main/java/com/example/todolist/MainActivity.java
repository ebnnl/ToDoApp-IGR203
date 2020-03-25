package com.example.todolist;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.LinkAddress;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Activité principale (tâches individuelles)

    private DAOBase dataBase; // Base de données sur laquelle agir
    private GroupsList groupsList; // Ensemble des groupes

    private FloatingActionButton addButton; // Bouton pour ouvrir l'activité de création de tâche
    private Button chooseGroupButton; // Bouton pour ouvrir le dialogue pour choisir le groupe
    private Dialog chooseGroupDialog;
    private LinearLayout tasksLayout; // Layout où sont affichées les tâches
    private LinearLayout responsibleLayout; // Layout où sont affichés les responsables
    private TextView responsibleToSeeTextView; // TextView pour afficher le responsable dont on veut voir les tâches

    private Group groupToSee; // Groupe dont on doit afficher les tâches
    private String personToSee; // Personne dont on veut voir les tâches

    @SuppressLint("WrongViewCast")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ouvrir la base de données
        dataBase = new DAOBase(this);
        dataBase.open();
        this.groupsList = dataBase.getGroupsList();

        // ******************************************************************************
        // Ajout de quelques données à ajouter pour le test. Partie à commenter une fois que
        // le code a été executé une fois (quand c'est fait une fois c'est stocké dans la bdd
        // du device pour de bon)
        // Donne une idée de comment ajouter des données à la bdd
        // Créer les données
        Group groupColoc = new Group("Coloc");
        Group groupIGR = new Group("Projet IGR");
        Person personMe = new Person("Moi", "yellow");
        Person personAlice = new Person("Alice", "red");
        Person personBob = new Person("Bob", "blue");
        Person personChloe = new Person("Chloe", "orange");
        groupColoc.addPerson(personMe);
        groupColoc.addPerson(personAlice);
        groupColoc.addPerson(personBob);
        groupIGR.addPerson(personMe);
        groupIGR.addPerson(personBob);
        groupIGR.addPerson(personChloe);
        Task task1 = new Task("Faire le ménage", 50, new Date(23052020), groupColoc, personAlice);
        Task task2 = new Task("Faire les courses", 60, new Date(21052020), groupColoc, personMe);
        Task task3 = new Task("Faire le rapport", 40, new Date(30032020), groupIGR, personChloe);
        Task task4 = new Task("Faire le prototype", 80, new Date(30032020), groupIGR, personBob);
        // Les ajouter à la base
        dataBase.addGroup(groupColoc);
        dataBase.addGroup(groupIGR);
        dataBase.addTask(task1);
        dataBase.addTask(task2);
        dataBase.addTask(task3);
        dataBase.addTask(task4);
        // ******************************************************************************

        tasksLayout = findViewById(R.id.activity_main_tasks_layout); // Récupérer le layout où afficher le texte
        addButton = findViewById(R.id.activity_main_add_button);
        chooseGroupButton = findViewById(R.id.activity_main_select_group_button);
        responsibleLayout = findViewById(R.id.activity_main_responsibles_layout);
        responsibleToSeeTextView = findViewById(R.id.activity_main_responsible_text_view);
        chooseGroupDialog = new Dialog(this);


        // Initialement, le groupe à afficher est le groupe perso
        Group groupPerso = new Group("Mes tâches personnelles");
        groupPerso.addPerson(personMe);
        dataBase.addGroup(groupPerso);
        this.groupToSee = groupsList.getGroup("Mes tâches personnelles");
        this.personToSee = "all";

        loadContent();

        // Configuration addButton
        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent createTaskActivity = new Intent(MainActivity.this, CreateTaskActivity.class);
                startActivity(createTaskActivity);
            }
        });

        // Configuration du bouton pour choisir le groupe
        chooseGroupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Afficher les choix dans le dialogue
                chooseGroupDialog.setContentView(R.layout.pop_up_choose_group);
                // Afficher un bouton pour chaque groupe
                List<Group> groups = groupsList.getList();
                LinearLayout buttonsLayout = chooseGroupDialog.findViewById(R.id.pop_up_choose_group_linear_layout);
                for (int i=0; i<groups.size(); i++){
                    final Group group = groups.get(i); // Groupe correspondant au bouton
                    Button button = new Button(MainActivity.this); // Créer le bouton
                    button.setText(group.getName());
                    buttonsLayout.addView(button); // Ajouter le bouton au layout
                    button.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                           // Au clic sur le bouton, recharger la mainActivity avec les tâches de ce groupe
                            groupToSee = group;
                            loadContent();
                            chooseGroupDialog.dismiss();
                        }
                    });
                }
                // Bouton pour créer un groupe
                Button createButton = new Button(MainActivity.this);
                createButton.setText("+");
                buttonsLayout.addView(createButton);
                createButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent createGroupActivity = new Intent(MainActivity.this, CreateGroupActivity.class);
                        startActivity(createGroupActivity);
                    }
                });

                // Afficher le dialogue en haut à gauche
                Window window = chooseGroupDialog.getWindow();
                window.setGravity(Gravity.TOP|Gravity.RIGHT);
                chooseGroupDialog.show();
            }
        });
    }

    @Override
    public void onRestart() {

        super.onRestart();
        dataBase = new DAOBase(this);
        dataBase.open();
        this.groupsList = dataBase.getGroupsList();
        groupToSee = groupsList.getGroup(groupToSee.getName());
        loadContent();
    }

    // Fonction pour charger le contenu de l'activity
    public void loadContent(){

        // Titre de la page et nom du responsable
        this.setTitle(groupToSee.getName());
        if (personToSee.equals("all")){
            responsibleToSeeTextView.setText("Toutes les tâches :");
        }
        else {
            responsibleToSeeTextView.setText("Tâches pour " + personToSee + " :");
        }

        // Contenu de tasksLayout
        // (Vider avant de remplir)
        tasksLayout.removeAllViews();
        responsibleLayout.removeAllViews();

        // Test: afficher la liste tâches du groupe et de la personne concernée
        // (Donne une idée de comment utiliser la bdd)
        List<Task> tasks = groupToSee.getTasks();
        for (int j=0; j<tasks.size(); j++){
            Task task = tasks.get(j);
            if (personToSee.equals("all") || personToSee.equals(task.getPerson().getName())){
                // Créer le text view avec le nom de la tâche et l'ajouter au layout
                TextView taskNameTextView = new TextView(this);
                taskNameTextView.setText("      "+task.getName());
                tasksLayout.addView(taskNameTextView);
            }
        }

        // Contenu de responsibleLayout
        // Afficher un bouton pour chaque responsable
        List<Person> responsibles = groupToSee.getPersons();
        Button buttonSeeAll = new Button(this);
        buttonSeeAll.setText("Voir tout");
        buttonSeeAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                personToSee = "all";
                loadContent();
            }
        });
        responsibleLayout.addView(buttonSeeAll);

        for (int i=0; i<responsibles.size(); i++){
            final Person responsible =responsibles.get(i);
            Button buttonPerson = new Button(this);
            int image = responsible.getImage();
            Drawable img = this.getResources().getDrawable(image);
            buttonPerson.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            buttonPerson.setBackground(img);
            buttonPerson.setText(responsible.getName());
            responsibleLayout.addView(buttonPerson);
            buttonPerson.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    personToSee = responsible.getName();
                    loadContent();
                }
            });
        }
    }
}
