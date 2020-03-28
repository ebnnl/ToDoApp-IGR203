package com.example.todolist;

public class Task {
    // Classe représentant une tâche

    private String name; // Nom de la tâche
    private int priority; // Niveau de priorité
    private Date deadline; // Date de la deadline (Entier sous la forme JJMMAAA)
    private Group group; // Nom du groupe auquel appartient la tâche
    private Person person; // Nom de la personne qui doit faire la tâche
    private int coordX, coordY; // Position de la tâche sur l'écran

    public Task(String name, int priority, Date deadline, Group group, Person person, int coordX, int coordY){
        this.name = name;
        this.priority = priority;
        this.deadline = deadline;
        this.group = group;
        this.person = person;
        this.coordX = coordX;
        this.coordY = coordY;
    }

    public String getName(){
        return name;
    }

    public Date getDeadline(){
        return deadline;
    }

    public int getPriority(){
        return priority;
    }
    public void setPriority(int p) {
        priority = p;
    }

    public Group getGroup(){
        return group;
    }

    public Person getPerson(){
        return person;
    }

    public int getCoordX() { return coordX; }
    public void setCoordX(int x) { coordX = x; }

    public int getCoordY() { return coordY; }
    public void setCoordY(int y) { coordY = y; }
}
