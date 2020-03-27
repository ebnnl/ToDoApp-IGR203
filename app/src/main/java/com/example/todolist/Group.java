package com.example.todolist;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Group {

    // Classe représentant un groupe
    String name; // Nom du groupe
    List<Person> persons; // Liste des participants du groupe
    List<Task> tasks; // Liste des tâches du groupe

    public Group(String name) {
        this.name = name;
        this.persons = new ArrayList<Person>();
        this.tasks = new ArrayList<Task>();
    }

    public void addPerson(Person person){
        persons.add(person);
    }

    public void addTask(Task task){
        tasks.add(task);
    }

    public String getName(){
        return this.name;
    }

    public List<Person> getPersons(){
        return this.persons;
    }

    public Person getPerson(String name){
        for (int i=0; i<persons.size(); i++){
            Person person = persons.get(i);
            if (person.getName().equals(name)){
                return person;
            }
        }
        return null;
    }

    public List<Task> getTasks(){
        return tasks;
    }

    public Task getTask(String taskName){
        for (int i=0; i<tasks.size(); i++){
            Task task = tasks.get(i);
            if (task.getName().equals(taskName)){
                return task;
            }
        }
        return null;
    }

    public void removeTask(Task task){
        tasks.remove(task);
    }

    public boolean containsTask(Task task){
        for (int i=0; i<tasks.size(); i++){
            Task taski = tasks.get(i);
            if (task.getName().equals(taski.getName()) && task.getGroup().getName().equals(taski.getGroup().getName())){
                return true;
            }
        }
        return false;
    }

    // Obtenir la première tâche dont la priorité est inférieure à priority
    public String getTaskPriorityInf(int priority){
        Task taskInf = new Task("priority0", 0, null, null, null, 0, 0);
        for (int i=0; i<tasks.size(); i++){
            Task task = tasks.get(i);
            if (task.getPriority()>taskInf.getPriority() && task.getPriority()<priority){
                taskInf = task;
            }
        }
        if (taskInf.getPriority()==0){
            return "";
        }
        return taskInf.getName();
    }

    // Obtenir la première tâche dont la priorité est supérieure à priority
    public String getTaskPrioritySup(int priority){
        Task taskSup = new Task("priority100", 100, null, null, null, 0, 0);
        for (int i=0; i<tasks.size(); i++){
            Task task = tasks.get(i);
            if (task.getPriority()<taskSup.getPriority() && task.getPriority()>priority){
                taskSup = task;
            }
        }
        if (taskSup.getPriority()==100){
            return "";
        }
        return taskSup.getName();
    }

    public String getTaskPriorityEqual(int priority){
        for (int i=0; i<tasks.size(); i++){
            Task task = tasks.get(i);
            if (task.getPriority()==priority){
                return task.getName();
            }
        }
        return "";
    }
}
