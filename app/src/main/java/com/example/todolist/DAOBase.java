package com.example.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class DAOBase {

    // Classe pour manipuler la base de données

    //
    protected final static int VERSION = 1;
    protected final static String NOM = "database.db";
    protected SQLiteDatabase mDb = null;
    protected DataBaseHandler mHandler = null;

    private GroupsList groupsList; // Liste des groupes

    public DAOBase(Context pContext) {
        this.mHandler = new DataBaseHandler(pContext, NOM, null, VERSION);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    // Charger les données dans groupsList
    public SQLiteDatabase open() {
        mDb = mHandler.getWritableDatabase();

        groupsList = new GroupsList();

        // Parcourir le tableau "participants" pour créer les groupes
        Cursor cursorParticipant = mDb.rawQuery("select * from participants", null);
        while (cursorParticipant.moveToNext()){
            String participant_name = cursorParticipant.getString(0);
            String group_name = cursorParticipant.getString(1);
            String color = cursorParticipant.getString(2);
            // Si le groupe "group_name" n'a pas encore été créé, le créer et l'ajouter à la liste
            if (!groupsList.contains(group_name)){
                Group group = new Group(group_name);
                groupsList.addGroup(group);
            }
            // Ajouter la personne "participant_name" au groupe
            Group group = groupsList.getGroup(group_name);
            Person person = new Person(participant_name, color);

            group.addPerson(person);
        }
        cursorParticipant.close();

        // Parcourir le tableau de "tasks" pour remplir les tâches des groupes
        Cursor cursorTask = mDb.rawQuery("select * from tasks", null);
        while (cursorTask.moveToNext()){
            String name = cursorTask.getString(0);
            int deadline = cursorTask.getInt(1);
            String group_name = cursorTask.getString(2);
            String participant_name = cursorTask.getString(3);
            int priority = cursorTask.getInt(4);
            int coordX = cursorTask.getInt(5);
            int coordY = cursorTask.getInt(6);

            // Récupérer le groupe la liste des groupes
            Group group = groupsList.getGroup(group_name);
            Person person = group.getPerson(participant_name);
            // Ajouter la tâche à ce groupe
            Task task = new Task(name, priority, new Date(deadline), group, person, coordX, coordY);
            group.addTask(task);
        }
        cursorTask.close();

        return mDb;
    }

    public void close() {
        mDb.close();
    }

    public SQLiteDatabase getDb() {
        return mDb;
    }

    // Fonction pour ajouter une tâche à la table tasks
    public void addTask(Task task){
        // Si la tâche n'existe pas déjà
        if (!groupsList.containsTask(task)){
            // Créer un ContentValue (= ligne d'une table) correspondant à la tâche
            ContentValues value = new ContentValues();
            value.put("name", task.getName());
            value.put("deadline", task.getDeadline().getInt());
            value.put("group_name", task.getGroup().getName());
            value.put("participant_name", task.getPerson().getName());
            value.put("priority", task.getPriority());
            value.put("coordX", task.getCoordX());
            value.put("coordY", task.getCoordY());
            // Ajouter la ligne à la table
            mDb.insert("tasks", null, value);
            groupsList.addTask(task);
        }
    }

    // Fonction pour ajouter un groupe à la table participants
    public void addGroup(Group group){
        if (!groupsList.contains(group.getName())){
            List<Person> persons = group.getPersons();
            // Pour chaque personne du groupe, créer une ligne dans la table participants
            for (int i=0; i<persons.size(); i++){
                Person person = persons.get(i);
                ContentValues value = new ContentValues();
                value.put("participant_name", person.getName());
                value.put("group_name", group.getName());
                value.put("color", person.getColor());
                mDb.insert("participants", null, value);
            }
            groupsList.addGroup(group);
        }
    }

    // Fonction pour ajouter une personne à un groupe
    public void addPerson(Person person, Group group){
        ContentValues value = new ContentValues();
        value.put("participant_name", person.getName());
        value.put("group_name", group.getName());
        value.put("color", person.getColor());
        mDb.insert("participants", null, value);
        groupsList.addPerson(person, group.getName());
    }

    // Fonction pour supprimer un tâche de la table tasks
    public void removeTask(Task task){
        // Supprimer la ligne de la table tasks où le name = nom de la tâche
        mDb.delete("tasks", "name = ?", new String[]{task.getName()});
        groupsList.removeTask(task);
    }

    public void removeGroup(Group group){
        // Supprimer les lignes de la table tasks et participan où group_name = nom du groupe
        mDb.delete("tasks", "group_name = ?", new String[]{group.getName()});
        mDb.delete("participants", "group_name = ?", new String[]{group.getName()});
        groupsList.removeGroup(group);
    }

    public GroupsList getGroupsList(){
        return groupsList;
    }


}
