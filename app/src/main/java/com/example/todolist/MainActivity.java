package com.example.todolist;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.net.LinkAddress;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Activité principale (tâches individuelles)

    private DAOBase dataBase; // Base de données sur laquelle agir


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ouvrir la base de données
        dataBase = new DAOBase(this);
        dataBase.open();

        // Ajout de quelques données à ajouter pour le test. Partie à commenter une fois que
        // le code a été executé une fois (quand c'est fait une fois c'est stocké dans la bdd
        // du device pour de bon)
        // Donne une idée de comment ajouter des données à la bdd
        // Créer les données
        Group groupColoc = new Group("Coloc");
        Group groupIGR = new Group("Projet IGR");
        Person personMe = new Person("Moi");
        Person personAlice = new Person("Alice");
        Person personBob = new Person("Bob");
        Person personChloe = new Person("Chloe");
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


        // Test: afficher la liste des groupes et leurs tâches dans la mainActivity
        // Donne une idée de comment utiliser la bdd

        LinearLayout linearLayoutTest = findViewById(R.id.activity_main_layout_test); // Récupérer le layout où afficher le texte
        GroupsList groupsList = dataBase.getGroupsList();
        List<Group> groups = groupsList.getList();
        for (int i=0; i<groups.size(); i++){
            Group group = groups.get(i);

            // Créer le text view avec le nom du groupe et l'ajouter au layout
            TextView groupNameTextView = new TextView(this);
            groupNameTextView.setText(group.getName());
            linearLayoutTest.addView(groupNameTextView);

            List<Task> tasks = group.getTasks();
            for (int j=0; j<tasks.size(); j++){
                Task task = tasks.get(j);
                // Créer le text view avec le nom de la tâche et l'ajouter au layout
                TextView taskNameTextView = new TextView(this);
                taskNameTextView.setText("      "+task.getName());
                linearLayoutTest.addView(taskNameTextView);
            }
        }
    }
}
