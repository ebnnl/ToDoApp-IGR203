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
import android.util.Log;
import android.view.Gravity;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static com.example.todolist.R.mipmap.blue_man;

public class MainActivity extends AppCompatActivity {

    // Activité principale (tâches individuelles)

    private DAOBase dataBase; // Base de données sur laquelle agir
    private GroupsList groupsList; // Ensemble des groupes

    private androidx.appcompat.widget.Toolbar toolbar;
    private FloatingActionButton addButton; // Bouton pour ouvrir l'activité de création de tâche
    private Button chooseGroupButton; // Bouton pour ouvrir le dialogue pour choisir le groupe
    private Dialog chooseGroupDialog;
    private RelativeLayout tasksLayout; // Layout où sont affichées les tâches
    private LinearLayout responsibleLayout; // Layout où sont affichés les responsables
    private TextView responsibleToSeeTextView; // TextView pour afficher le responsable dont on veut voir les tâches
    private Dialog longTouchOnTaskDialog; // Dialog qui apparait en appuyant longtemps sur une tâche

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
       /* Group groupColoc = new Group("Coloc");
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
        Task task1 = new Task("Faire le ménage", 50, new Date(23052020), groupColoc, personAlice, 50, 150);
        Task task2 = new Task("Faire les courses", 60, new Date(21052020), groupColoc, personMe, 200, 350);
        Task task3 = new Task("Faire le rapport", 40, new Date(30032020), groupIGR, personChloe, 150, 50);
        Task task4 = new Task("Faire le prototype", 80, new Date(30032020), groupIGR, personBob, 200, 500);
        // Les ajouter à la base
        dataBase.addGroup(groupColoc);
        dataBase.addGroup(groupIGR);
        dataBase.addTask(task1);
        dataBase.addTask(task2);
        dataBase.addTask(task3);
        dataBase.addTask(task4);*/
        // ******************************************************************************

        toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        tasksLayout = findViewById(R.id.activity_main_tasks_layout); // Récupérer le layout où afficher le texte
        addButton = findViewById(R.id.activity_main_add_button);
        chooseGroupButton = findViewById(R.id.activity_main_select_group_button);
        responsibleLayout = findViewById(R.id.activity_main_responsibles_layout);
        responsibleToSeeTextView = findViewById(R.id.activity_main_responsible_text_view);
        chooseGroupDialog = new Dialog(this);
        longTouchOnTaskDialog = new Dialog(this);


        // Initialement, le groupe à afficher est le groupe perso
        Group groupPerso = new Group("Mes tâches personnelles");
        Person personMe = new Person("Moi", "yellow");
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
                // Spécifier le groupe courant
                Bundle b = new Bundle();
                b.putString("groupName", groupToSee.getName());
                createTaskActivity.putExtras(b);
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
                    button.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    button.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    buttonsLayout.addView(button); // Ajouter le bouton au layout
                    button.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                           // Au clic sur le bouton, recharger la mainActivity avec les tâches de ce groupe
                            groupToSee = group;
                            personToSee = "all";
                            loadContent();
                            chooseGroupDialog.dismiss();
                        }
                    });
                }
                // Bouton pour créer un groupe
                Button createButton = new Button(MainActivity.this);
                createButton.setText("+");
                createButton.setTextColor(getResources().getColor(R.color.colorWhite));
                createButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
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
        // Recharger la base de donnée car elle peut avoir été modifiée par MultiTouchListener
        dataBase = new DAOBase(this);
        dataBase.open();
        this.groupsList = dataBase.getGroupsList();
        // Mettre à jour groupToSee
        groupToSee = dataBase.getGroupsList().getGroup(groupToSee.getName());
        List<Task> tasks = groupToSee.getTasks();

        // Titre de la page et nom du responsable
        this.setTitle(groupToSee.getName());
        responsibleToSeeTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
        if (personToSee.equals("all")){
            responsibleToSeeTextView.setText("Toutes les tâches :");
        }
        else {
            responsibleToSeeTextView.setText("Tâches pour " + personToSee + " :");
            int color = groupToSee.getPerson(personToSee).getColorInt();
        }

        // Contenu de tasksLayout
        // (Vider avant de remplir)
        tasksLayout.removeAllViews();
        responsibleLayout.removeAllViews();



        // Test: afficher la liste tâches du groupe et de la personne concernée
        // (Donne une idée de comment utiliser la bdd)
        for (int j=0; j<tasks.size(); j++){
            final Task task = tasks.get(j);
            if (personToSee.equals("all") || personToSee.equals(task.getPerson().getName())){
                // Créer le bouton
                Button taskButton = new Button(this);
                // Texte du bouton
                taskButton.setText(task.getName());
                // Définir la taille du bouton en fonction de la priorité
                int size = task.getPriority()*5+150;
                taskButton.setWidth(size);
                taskButton.setHeight(size);
                // Positionner le bouton en fonction de ses coordonnées
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(size, size);
                layoutParams.leftMargin = task.getCoordX();
                layoutParams.topMargin = task.getCoordY();
                // Créer les listeners dont on a besoin pour zoomer et déplacer le bouton
                OnPinchListener onPinchListener = new OnPinchListener(taskButton, task);
                ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(getApplicationContext(), onPinchListener);
                MultiTouchListener touchListener = new MultiTouchListener(this, scaleGestureDetector, task);
                // Ajouter les listener au bouton
                taskButton.setOnTouchListener(touchListener);
                // Couleur de la tâche
                taskButton.setBackgroundColor(getResources().getColor(task.getPerson().getColorInt()));
                // Ajouter le bouton au layout
                tasksLayout.addView(taskButton, layoutParams);
            }
        }

        // Contenu de responsibleLayout
        // Afficher un bouton pour chaque responsable
        List<Person> responsibles = groupToSee.getPersons();
        Button buttonSeeAll = new Button(this);
        Drawable imgUsers = this.getResources().getDrawable(R.mipmap.users);
        buttonSeeAll.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
        buttonSeeAll.setBackground(this.getResources().getDrawable(R.mipmap.users));
        buttonSeeAll.setText("Tous");
        buttonSeeAll.setWidth(30);
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
            int image = responsible.getImage2();
            Drawable imgUser = this.getResources().getDrawable(image);
            buttonPerson.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
            buttonPerson.setBackground(this.getResources().getDrawable(image));
            buttonPerson.setWidth(30);
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

    // Fonction pour afficher les dialogue qui s'ouvre au click long sur un post it
    public void displayLongTouchDialog(final String taskName){
        longTouchOnTaskDialog.setContentView(R.layout.pop_up_long_touch);
        FloatingActionButton doneButton = longTouchOnTaskDialog.findViewById(R.id.pop_up_long_touch_done);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task task = groupToSee.getTask(taskName);
                dataBase.removeTask(task);
                loadContent();
                longTouchOnTaskDialog.dismiss();

                // Afficher un toast de félicitations
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(MainActivity.this, "Félicitations !", duration);
                View congratulationView = new View(MainActivity.this);
                ImageView congratulationImage = new ImageView(MainActivity.this);
                congratulationImage.setImageDrawable(MainActivity.this.getResources().getDrawable(R.mipmap.congrats));
                toast.setView(congratulationImage);
                toast.show();
            }
        });
        longTouchOnTaskDialog.show();
    }
}
