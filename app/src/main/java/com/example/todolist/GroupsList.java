package com.example.todolist;

import java.util.ArrayList;
import java.util.List;

public class GroupsList {
    // Classe représentant la liste de tous les groupes

    List<Group> list;

    public GroupsList() {
        this.list = new ArrayList<Group>();
    }

    public boolean contains(String groupName){
        for (int i=0; i<list.size(); i++){
            Group group = list.get(i);
            if (group.getName().equals(groupName)){
                return true;
            }
        }
        return false;
    }

    public boolean containsTask(Task task){
        for (int i=0; i<list.size(); i++){
            Group group = list.get(i);
            if (group.containsTask(task)){
                return true;
            }
        }
        return false;
    }

    public void addGroup(Group group){
        list.add(group);
    }

    public Group getGroup(String groupName){
        for (int i=0; i<list.size(); i++){
            Group group = list.get(i);
            if (group.getName().equals(groupName)){
                return group;
            }
        }
        return null;
    }

    public void addTask(Task task){
        String groupName = task.getGroup().getName();
        Group group = this.getGroup(groupName);
        group.addTask(task);
    }

    public void addPerson(Person person, String groupName){
        Group group = this.getGroup(groupName);
        group.addPerson(person);
    }

    public void removeTask(Task task){
        String groupName = task.getGroup().getName();
        Group group = this.getGroup(groupName);
        group.removeTask(task);
    }

    public List<String> getNames(){
        List<String> names = new ArrayList<String>();
        for (int i=0; i<list.size(); i++){
            Group group = list.get(i);
            names.add(group.getName());
        }
        return names;
    }

    public List<Person> getPersons(){
        List<Person> personsList = new ArrayList<Person>();
        List<String> personsNames = new ArrayList<String>();
        for (int i=0; i<list.size(); i++){
            Group group = list.get(i);
            List<Person> persons = group.getPersons();
            for (int j=0; j<persons.size(); j++){
                Person person = persons.get(j);
                if (!personsNames.contains(person.getName())){
                    personsList.add(person);
                    personsNames.add(person.getName());
                }
            }
        }
        return personsList;
    }

    public void removeGroup(Group group){
        list.remove(group);
    }

    public List<Group> getList(){
        return list;
    }



}
